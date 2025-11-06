package com.uncuyo.greedy_cars.shared.template.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.CostoVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.FacturaRepository;
import com.uncuyo.greedy_cars.shared.template.repository.VehiculoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MercadoPagoService {

    private final FacturaRepository facturaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final CostoVehiculoService costoVehiculoService;

    private final String accessToken;
    private final String backUrlSuccess;
    private final String backUrlFailure;
    private final String backUrlPending;

    public MercadoPagoService(
            FacturaRepository facturaRepository,
            VehiculoRepository vehiculoRepository,
            CostoVehiculoService costoVehiculoService,
            @Value("${mercadopago.access-token}") String accessToken,
            @Value("${mercadopago.back-url-success}") String backUrlSuccess,
            @Value("${mercadopago.back-url-failure}") String backUrlFailure,
            @Value("${mercadopago.back-url-pending}") String backUrlPending) {
        this.facturaRepository = facturaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.costoVehiculoService = costoVehiculoService;
        this.accessToken = accessToken;
        this.backUrlSuccess = backUrlSuccess;
        this.backUrlFailure = backUrlFailure;
        this.backUrlPending = backUrlPending;
    }

    @Transactional(readOnly = true)
    public double calcularMonto(String facturaId, String vehiculoId, int cantidadDias) {
        CalculatedAmount context = calcularContexto(facturaId, vehiculoId, cantidadDias);
        return context.monto();
    }

    @Transactional(readOnly = true)
    public Preference createPreference(String facturaId, String vehiculoId, int cantidadDias) {
        CalculatedAmount context = calcularContexto(facturaId, vehiculoId, cantidadDias);

        if (accessToken == null || accessToken.isBlank() || "REEMPLAZAR_CON_TOKEN_REAL".equals(accessToken)) {
            throw new ErrorServiceException("Debe configurar el access token de Mercado Pago antes de generar preferencias");
        }

        MercadoPagoConfig.setAccessToken(accessToken);

        String externalReference = buildExternalReference(facturaId, vehiculoId, cantidadDias);
        String itemTitle = buildItemTitle(context);

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(itemTitle)
                .description("Reserva de alquiler de vehículo")
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(BigDecimal.valueOf(context.monto()))
                .build();

        // TODO: reemplazar NGROK_URL por el dominio real o túnel ngrok en runtime.
        PreferenceBackUrlsRequest backUrlsRequest = PreferenceBackUrlsRequest.builder()
                .success(backUrlSuccess)
                .failure(backUrlFailure)
                .pending(backUrlPending)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(itemRequest))
                .backUrls(backUrlsRequest)
                .externalReference(externalReference)
                .build();

        PreferenceClient preferenceClient = new PreferenceClient();
        try {
            return preferenceClient.create(preferenceRequest);
        } catch (MPApiException e) {
            throw new ErrorServiceException("Error API de Mercado Pago al crear la preferencia", e);
        } catch (MPException e) {
            throw new ErrorServiceException("Error al crear la preferencia de Mercado Pago", e);
        } catch (RuntimeException e) {
            throw new ErrorServiceException("Error inesperado al crear la preferencia", e);
        }
    }

    public String buildExternalReference(String facturaId, String vehiculoId, int cantidadDias) {
        if (facturaId != null && !facturaId.isBlank()) {
            return facturaId;
        }
        if (vehiculoId == null || vehiculoId.isBlank()) {
            throw new ErrorServiceException("No se indicó el vehículo para la preferencia");
        }
        return "ALQUILER:" + vehiculoId + ":" + cantidadDias;
    }

    private CalculatedAmount calcularContexto(String facturaId, String vehiculoId, int cantidadDias) {
        if (cantidadDias <= 0) {
            throw new ErrorServiceException("La cantidad de días debe ser mayor a cero");
        }

        if (facturaId != null && !facturaId.isBlank()) {
            Factura factura = facturaRepository.findByIdAndEliminadoIsFalse(facturaId)
                    .orElseThrow(() -> new ErrorServiceException("Factura no encontrada o eliminada"));

            Double total = factura.getTotalPagado();
            if (total == null) {
                throw new ErrorServiceException("La factura no tiene un total registrado");
            }

            Vehiculo vehiculo = factura.getDetalles().stream()
                    .filter(Objects::nonNull)
                    .map(detalle -> detalle.getAlquiler() != null ? detalle.getAlquiler().getVehiculo() : null)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            CaracteristicaVehiculo caracteristica = vehiculo != null ? vehiculo.getCaracteristicaVehiculo() : null;

            return new CalculatedAmount(redondear(total), factura, vehiculo, caracteristica);
        }

        if (vehiculoId == null || vehiculoId.isBlank()) {
            throw new ErrorServiceException("Debe indicar el vehículo para calcular el monto");
        }

        Vehiculo vehiculo = vehiculoRepository.findByIdAndEliminadoIsFalse(vehiculoId)
                .orElseThrow(() -> new ErrorServiceException("Vehículo no encontrado o eliminado"));

        CaracteristicaVehiculo caracteristica = vehiculo.getCaracteristicaVehiculo();
        if (caracteristica == null) {
            throw new ErrorServiceException("El vehículo no tiene una característica asociada");
        }

        Optional<CostoVehiculo> costoOpt = costoVehiculoService.buscarCostoVehiculoVigente(caracteristica.getId());

        CostoVehiculo costoVigente = costoOpt
                .orElseThrow(() -> new ErrorServiceException("No hay un costo vigente para la característica del vehículo"));

        double montoCalculado = redondear(costoVigente.getCosto() * cantidadDias);
        return new CalculatedAmount(montoCalculado, null, vehiculo, caracteristica);
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String buildItemTitle(CalculatedAmount context) {
        Vehiculo vehiculo = context.vehiculo();
        CaracteristicaVehiculo caracteristica = context.caracteristica();

        StringBuilder title = new StringBuilder("Alquiler vehículo");

        if (vehiculo != null && vehiculo.getPatente() != null) {
            title.append(" ").append(vehiculo.getPatente());
        }

        if (caracteristica != null) {
            if (caracteristica.getMarca() != null) {
                title.append(" ").append(caracteristica.getMarca());
            }
            if (caracteristica.getModelo() != null) {
                title.append(" ").append(caracteristica.getModelo());
            }
        }

        return title.toString().trim();
    }

    private record CalculatedAmount(double monto, Factura factura, Vehiculo vehiculo, CaracteristicaVehiculo caracteristica) {}
}
