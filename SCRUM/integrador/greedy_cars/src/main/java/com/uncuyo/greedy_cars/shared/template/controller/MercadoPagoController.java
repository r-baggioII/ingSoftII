package com.uncuyo.greedy_cars.shared.template.controller;

import com.mercadopago.resources.preference.Preference;
import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceRequest;
import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceResponse;
import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.DetalleFactura;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.entity.FormaDePago;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoFactura;
import com.uncuyo.greedy_cars.shared.template.enums.TipoPago;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.AlquilerRepository;
import com.uncuyo.greedy_cars.shared.template.repository.FacturaRepository;
import com.uncuyo.greedy_cars.shared.template.service.MercadoPagoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public MercadoPagoController(
            MercadoPagoService mercadoPagoService,
            FacturaRepository facturaRepository,
            AlquilerRepository alquilerRepository) {
        this.mercadoPagoService = mercadoPagoService;
        this.facturaRepository = facturaRepository;
        this.alquilerRepository = alquilerRepository;
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
    public ResponseEntity<?> pagoExitoso(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "external_reference", required = false) String externalReference,
            @RequestParam(name = "preference_id", required = false) String preferenceId) {
        try {
            log.info("Callback de éxito Mercado Pago - paymentId={}, status={}, externalReference={}, preferenceId={}",
                    paymentId, status, externalReference, preferenceId);

            if (externalReference == null || externalReference.isBlank()) {
                throw new ErrorServiceException("No se recibió la referencia externa del pago");
            }

            Factura facturaProcesada;
            double monto;

            if (externalReference.startsWith("ALQUILER:")) {
                String[] partes = externalReference.split(":");
                if (partes.length < 3) {
                    throw new ErrorServiceException("Referencia externa de alquiler inválida");
                }
                String vehiculoId = partes[1];
                int cantidadDias;
                try {
                    cantidadDias = Integer.parseInt(partes[2]);
                } catch (NumberFormatException ex) {
                    throw new ErrorServiceException("No se pudo interpretar la cantidad de días desde la referencia externa");
                }

                monto = mercadoPagoService.calcularMonto(null, vehiculoId, cantidadDias);
                facturaProcesada = generarFacturaParaAlquiler(vehiculoId, cantidadDias, monto);
            } else {
                facturaProcesada = facturaRepository.findByIdAndEliminadoIsFalse(externalReference)
                        .orElseThrow(() -> new ErrorServiceException("Factura no encontrada para la referencia recibida"));
                monto = facturaProcesada.getTotalPagado() != null ? facturaProcesada.getTotalPagado() : 0D;
            }

            marcarFacturaComoPagada(facturaProcesada, monto, paymentId);

            // TODO: ajustar URL de retorno cuando se defina el ngrok definitivo.
            // TODO FRONT: al recibir esta respuesta, refrescar la vista de facturas y habilitar la descarga del PDF
            // solo cuando el estado resulte PAGADA/Habilitada por este callback.
            Map<String, Object> body = Map.of(
                    "mensaje", "pago confirmado",
                    "facturaId", facturaProcesada.getId(),
                    "estado", facturaProcesada.getEstado().name());
            return ResponseEntity.ok(body);
        } catch (ErrorServiceException e) {
            log.warn("Error de negocio al procesar pago exitoso de Mercado Pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al procesar pago exitoso de Mercado Pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al procesar el pago"));
        }
    }

    @GetMapping("/failure")
    public ResponseEntity<?> pagoFallido(@RequestParam Map<String, String> queryParams) {
        log.warn("Callback de fallo Mercado Pago recibido: {}", queryParams);
        // TODO: ajustar URL de retorno cuando se defina el ngrok definitivo.
        return ResponseEntity.ok(Map.of("mensaje", "El pago fue rechazado o cancelado"));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> pagoPendiente(@RequestParam Map<String, String> queryParams) {
        log.info("Callback de pago pendiente Mercado Pago: {}", queryParams);
        // TODO: ajustar URL de retorno cuando se defina el ngrok definitivo.
        return ResponseEntity.ok(Map.of("mensaje", "El pago está pendiente de aprobación"));
    }

    private Factura generarFacturaParaAlquiler(String vehiculoId, int cantidadDias, double monto) {
        Optional<Alquiler> alquilerOpt = alquilerRepository
                .findFirstByVehiculoIdAndEliminadoIsFalseOrderByFechaHastaDesc(vehiculoId);

        Alquiler alquiler = alquilerOpt.orElseThrow(
                () -> new ErrorServiceException("No se encontró un alquiler asociado al vehículo para generar la factura"));

        Factura factura = new Factura();
        factura.setEliminado(false);
        factura.setFechaFactura(LocalDate.now());
        factura.setEstado(EstadoFactura.PAGADA);
        factura.setTotalPagado(monto);
        factura.setNumeroFactura(obtenerSiguienteNumeroFactura());

        DetalleFactura detalle = new DetalleFactura();
        detalle.setCantidad(cantidadDias);
        detalle.setSubtotal(monto);
        detalle.setAlquiler(alquiler);
        detalle.setEliminado(false);
        factura.agregarDetalle(detalle);

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
}
