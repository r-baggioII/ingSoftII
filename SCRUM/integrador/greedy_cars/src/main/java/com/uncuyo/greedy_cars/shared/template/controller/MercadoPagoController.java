package com.uncuyo.greedy_cars.shared.template.controller;

import com.mercadopago.resources.preference.Preference;
import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceRequest;
import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceResponse;
import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.entity.FormaDePago;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoFactura;
import com.uncuyo.greedy_cars.shared.template.enums.TipoPago;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.AlquilerRepository;
import com.uncuyo.greedy_cars.shared.template.repository.FacturaRepository;
import com.uncuyo.greedy_cars.shared.template.service.FacturaService;
import com.uncuyo.greedy_cars.shared.template.service.MercadoPagoService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos/mp")
public class MercadoPagoController {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoController.class);

    private final MercadoPagoService mercadoPagoService;
    private final FacturaRepository facturaRepository;
    private final AlquilerRepository alquilerRepository;
    private final FacturaService facturaService;
    
    @Value("${mercadopago.frontend-url-success}")
    private String frontendSuccessUrl;
    
    @Value("${mercadopago.frontend-url-failure}")
    private String frontendFailureUrl;
    
    @Value("${mercadopago.frontend-url-pending}")
    private String frontendPendingUrl;

    public MercadoPagoController(
            MercadoPagoService mercadoPagoService,
            FacturaRepository facturaRepository,
            AlquilerRepository alquilerRepository,
            FacturaService facturaService) {
        this.mercadoPagoService = mercadoPagoService;
        this.facturaRepository = facturaRepository;
        this.alquilerRepository = alquilerRepository;
        this.facturaService = facturaService;
    }

    @PostMapping("/preferencia")
    public ResponseEntity<?> crearPreferencia(@Valid @RequestBody MercadoPagoPreferenceRequest request) {
        try {
            String facturaId = request.getFacturaId();
            String vehiculoId = request.getVehiculoId();

            if ((facturaId == null || facturaId.isBlank()) && (vehiculoId == null || vehiculoId.isBlank())) {
                throw new ErrorServiceException("Debe indicar el vehículo si no existe una factura asociada");
            }

            Integer diasSolicitados = request.getCantidadDias();
            if ((facturaId == null || facturaId.isBlank()) && (diasSolicitados == null || diasSolicitados <= 0)) {
                throw new ErrorServiceException("Debe indicar una cantidad de días válida cuando no existe factura");
            }
            int cantidadDias = diasSolicitados != null ? diasSolicitados : 1;

            double monto = mercadoPagoService.calcularMonto(facturaId, vehiculoId, cantidadDias);
            Preference preference = mercadoPagoService.createPreference(facturaId, vehiculoId, cantidadDias);

            MercadoPagoPreferenceResponse response = MercadoPagoPreferenceResponse.builder()
                    .initPoint(preference.getInitPoint())
                    .preferenceId(preference.getId())
                    .monto(monto)
                    .build();

            // TODO CLIENTE: invocar este endpoint desde el portal (botón "Pagar con billetera virtual"),
            // leer initPoint y redirigir al checkout de Mercado Pago antes de mostrar la pantalla de confirmación.
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            log.warn("Error negocio al crear preferencia Mercado Pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al crear preferencia Mercado Pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al crear la preferencia"));
        }
    }

    @GetMapping("/success")
    @Transactional
    public void pagoExitoso(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "external_reference", required = false) String externalReference,
            @RequestParam(name = "preference_id", required = false) String preferenceId,
            HttpServletResponse response) throws IOException {
        try {
            log.info("Callback de éxito Mercado Pago - paymentId={}, status={}, externalReference={}, preferenceId={}",
                    paymentId, status, externalReference, preferenceId);

            if (externalReference == null || externalReference.isBlank()) {
                log.error("No se recibió la referencia externa del pago");
                response.sendRedirect(buildUrlWithParams(frontendFailureUrl, "error", "sin_referencia"));
                return;
            }

            Factura facturaProcesada;
            double monto;

            if (externalReference.startsWith("ALQUILER:")) {
                String[] partes = externalReference.split(":");
                if (partes.length < 3) {
                    log.error("Referencia externa de alquiler inválida: {}", externalReference);
                    response.sendRedirect(buildUrlWithParams(frontendFailureUrl, "error", "referencia_invalida"));
                    return;
                }
                String vehiculoId = partes[1];
                int cantidadDias;
                try {
                    cantidadDias = Integer.parseInt(partes[2]);
                } catch (NumberFormatException ex) {
                    log.error("No se pudo interpretar la cantidad de días: {}", partes[2]);
                    response.sendRedirect(buildUrlWithParams(frontendFailureUrl, "error", "dias_invalidos"));
                    return;
                }

                monto = mercadoPagoService.calcularMonto(null, vehiculoId, cantidadDias);
                facturaProcesada = generarFacturaParaAlquiler(vehiculoId, cantidadDias, monto);
            } else {
                Optional<Factura> facturaOpt = facturaRepository.findByIdAndEliminadoIsFalse(externalReference);
                if (facturaOpt.isEmpty()) {
                    log.error("Factura no encontrada para la referencia: {}", externalReference);
                    response.sendRedirect(buildUrlWithParams(frontendFailureUrl, "error", "factura_no_encontrada"));
                    return;
                }
                facturaProcesada = facturaOpt.get();
                monto = facturaProcesada.getTotalPagado() != null ? facturaProcesada.getTotalPagado() : 0D;
            }

            marcarFacturaComoPagada(facturaProcesada, monto, paymentId);

            log.info("Pago procesado exitosamente para factura: {}", facturaProcesada.getId());
            
            // Redirigir al frontend con parámetros de éxito
            String redirectUrl = buildUrlWithParams(frontendSuccessUrl, 
                    "factura_id", facturaProcesada.getId(),
                    "payment_id", paymentId != null ? paymentId : "");
            response.sendRedirect(redirectUrl);
            
        } catch (ErrorServiceException e) {
            log.warn("Error de negocio al procesar pago exitoso de Mercado Pago: {}", e.getMessage());
            response.sendRedirect(buildUrlWithParams(frontendFailureUrl, "error", "error_negocio"));
        } catch (Exception e) {
            log.error("Error inesperado al procesar pago exitoso de Mercado Pago", e);
            response.sendRedirect(buildUrlWithParams(frontendFailureUrl, "error", "error_sistema"));
        }
    }

    @GetMapping("/failure")
    public void pagoFallido(
            @RequestParam Map<String, String> queryParams,
            HttpServletResponse response) throws IOException {
        log.warn("Callback de fallo Mercado Pago recibido: {}", queryParams);
        response.sendRedirect(frontendFailureUrl);
    }

    @GetMapping("/pending")
    public void pagoPendiente(
            @RequestParam Map<String, String> queryParams,
            HttpServletResponse response) throws IOException {
        log.info("Callback de pago pendiente Mercado Pago: {}", queryParams);
        response.sendRedirect(frontendPendingUrl);
    }

    private Factura generarFacturaParaAlquiler(String vehiculoId, int cantidadDias, double monto) {
        Optional<Alquiler> alquilerOpt = alquilerRepository
                .findFirstByVehiculoIdAndEliminadoIsFalseOrderByFechaHastaDesc(vehiculoId);

        Alquiler alquiler = alquilerOpt.orElseThrow(
                () -> new ErrorServiceException("No se encontró un alquiler asociado al vehículo para generar la factura"));

        Factura factura = facturaService.crearFacturaBorradorDesdeAlquiler(alquiler, monto, cantidadDias, null);
        factura.setCliente(alquiler.getCliente());
        registrarFormaDePago(factura, null);
        return facturaRepository.save(factura);
    }

    private void marcarFacturaComoPagada(Factura factura, double monto, String paymentId) {
        factura.setEstado(EstadoFactura.PAGADA);
        if (monto > 0) {
            factura.setTotalPagado(monto);
        }
        registrarFormaDePago(factura, paymentId);
        facturaRepository.save(factura);
    }

    private void registrarFormaDePago(Factura factura, String paymentId) {
        String observacionBase = "Mercado Pago checkout";
        String observacion = paymentId != null && !paymentId.isBlank()
                ? observacionBase + " payment_id=" + paymentId
                : observacionBase;

        if (factura.getFormasPago() == null) {
            factura.setFormasPago(new ArrayList<>());
        }

        boolean yaRegistrada = factura.getFormasPago().stream()
                .anyMatch(forma -> forma.getTipoPago() == TipoPago.BILLETERA_VIRTUAL
                        && observacion.equalsIgnoreCase(forma.getObservacion()));

        if (!yaRegistrada) {
            FormaDePago formaPago = new FormaDePago();
            formaPago.setTipoPago(TipoPago.BILLETERA_VIRTUAL);
            formaPago.setObservacion(observacion);
            formaPago.setEliminado(false);
            factura.agregarFormaPago(formaPago);
        }
    }

    private Long obtenerSiguienteNumeroFactura() {
        Long max = facturaRepository.obtenerMaxNumeroFactura();
        if (max == null) {
            max = 0L;
        }
        return max + 1;
    }
    
    private String buildUrlWithParams(String baseUrl, String... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Los parámetros deben ser pares clave-valor");
        }
        
        StringBuilder url = new StringBuilder(baseUrl);
        boolean firstParam = !baseUrl.contains("?");
        
        for (int i = 0; i < params.length; i += 2) {
            String key = params[i];
            String value = params[i + 1];
            
            if (value != null && !value.isEmpty()) {
                url.append(firstParam ? "?" : "&");
                url.append(key).append("=").append(value);
                firstParam = false;
            }
        }
        
        return url.toString();
    }
}
