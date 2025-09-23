package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.EstadoCuota;
import com.example.greedy_gym.entidades.EstadoFactura;
import com.example.greedy_gym.entidades.DetalleFactura;
import com.example.greedy_gym.entidades.Factura;
import com.example.greedy_gym.entidades.FormaDePago;
import com.example.greedy_gym.entidades.TipoPago;
import com.example.greedy_gym.entidades.ValorCuota;
import com.example.greedy_gym.repositorios.CuotaMensualRepositorio;
import com.example.greedy_gym.repositorios.FormaDePagoRepositorio;
import com.example.greedy_gym.servicios.FacturaServicio;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.preference.PreferenceItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/socio/pagos")
public class MercadoPagoDemoControlador {

    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoPagoDemoControlador.class);

    private final String accessToken;
    private final CuotaMensualRepositorio cuotaMensualRepositorio;
    private final FacturaServicio facturaServicio;
    private final FormaDePagoRepositorio formaDePagoRepositorio;

    public MercadoPagoDemoControlador(@Value("${mercadopago.access-token:}") String accessToken,
                                      CuotaMensualRepositorio cuotaMensualRepositorio,
                                      FacturaServicio facturaServicio,
                                      FormaDePagoRepositorio formaDePagoRepositorio) {
        this.accessToken = "APP_USR-1161916719081917-092219-7d27c8faacdc9c984b9bd4539830b21b-2708507674";
        this.cuotaMensualRepositorio = cuotaMensualRepositorio;
        this.facturaServicio = facturaServicio;
        this.formaDePagoRepositorio = formaDePagoRepositorio;
    }

    @GetMapping("/cuotas-pendientes")
    public ResponseEntity<List<CuotaMensual>> cuotasPendientes() {
        Collection<CuotaMensual> cuotas = cuotaMensualRepositorio.findByEliminadoFalse();
        return ResponseEntity.ok(new ArrayList<>(cuotas));
    }

    @PostMapping("/preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody(required = false) List<String> cuotaIds) {
        try {
            configurarMercadoPago();
        } catch (IllegalStateException e) {
            LOGGER.error("No se pudo configurar Mercado Pago :: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        List<CuotaMensual> cuotas;
        if (cuotaIds == null || cuotaIds.isEmpty()) {
            cuotas = new ArrayList<>(cuotaMensualRepositorio.findByEliminadoFalse());
        } else {
            cuotas = cuotaMensualRepositorio.findByIdInAndEliminadoFalse(cuotaIds);
        }

        if (cuotas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontraron cuotas para generar la preferencia");
        }

        List<PreferenceItemRequest> items = new ArrayList<>();
        for (CuotaMensual cuota : cuotas) {
            ValorCuota valorCuota = cuota.getValorCuota();
            double monto = valorCuota != null ? valorCuota.getValorCuota() : 0d;
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(cuota.getId())
                    .title(String.format("Cuota %s %d", cuota.getMes(), cuota.getAnio()))
                    .description("Pago de cuota de gimnasio")
                    .pictureUrl("https://picsum.photos/600/400")
                    .categoryId("gym_membership")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(BigDecimal.valueOf(monto).setScale(2, RoundingMode.HALF_UP))
                    .build();
            items.add(item);
        }
        LOGGER.info("Items enviados a MP: {}", items.size());

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
            .success("https://roily-hydraulic-renna.ngrok-free.dev")
            .pending("https://roily-hydraulic-renna.ngrok-free.dev")
            .failure("https://roily-hydraulic-renna.ngrok-free.dev")
            .build();


        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                //.autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        try {
            Preference preference = client.create(preferenceRequest);
            LOGGER.info("Preferencia creada :: id={} sandboxInitPoint={} initPoint={}",
                    preference.getId(), preference.getSandboxInitPoint(), preference.getInitPoint());
            Map<String, String> body = new HashMap<>();
            // body.put("sandboxInitPoint", preference.getSandboxInitPoint()); // dejar comentado por si se quiere usar sandbox
            body.put("initPoint", preference.getInitPoint());
            body.put("preferenceId", preference.getId());
            return ResponseEntity.ok(body);
        } catch (MPApiException | MPException e) {
            if (e instanceof MPApiException apiException && apiException.getApiResponse() != null) {
                LOGGER.error("Error creando preferencia :: status={} body={}",
                        apiException.getApiResponse().getStatusCode(),
                        apiException.getApiResponse().getContent());
            }
            LOGGER.error("Error creando preferencia :: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creando preferencia: " + e.getMessage());
        }
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarPago(@RequestBody Map<String, String> body) {
        String preferenceId = body != null ? body.get("preferenceId") : null;
        if (preferenceId == null || preferenceId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "preferenceId requerido"));
        }
        List<CuotaMensual> cuotas = obtenerCuotasDePreferencia(preferenceId);
        if (cuotas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "No se encontraron cuotas para la preferencia"));
        }
        marcarCuotasComoPagadas(cuotas, preferenceId);
        crearFacturaParaCuotas(cuotas);
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Preferencia verificada"));
    }

    @GetMapping("/success")
    public ResponseEntity<String> pagoExitoso(@RequestParam(name = "preference_id", required = false) String preferenceId,
                                              HttpSession session) {
        List<CuotaMensual> cuotasPagadas = obtenerCuotasDePreferencia(preferenceId);
        if (!cuotasPagadas.isEmpty()) {
            marcarCuotasComoPagadas(cuotasPagadas, preferenceId);
            Optional<String> facturaId = crearFacturaParaCuotas(cuotasPagadas);
            facturaId.ifPresent(id -> session.setAttribute("ultimaFacturaId", id));
        }
        session.setAttribute("mensajePago", "¡Gracias! Tu pago fue procesado correctamente.");
        return html("Pago Exitoso", "¡Gracias! Tu pago fue procesado correctamente.", "/dashboard/socio");
    }

    @GetMapping("/pending")
    public ResponseEntity<String> pagoPendiente(@RequestParam(name = "preference_id", required = false) String preferenceId) {
        return html("Pago Pendiente", "Tu pago quedó pendiente de confirmación. Te notificaremos cuando se acredite.", "/");
    }

    @GetMapping("/failure")
    public ResponseEntity<String> pagoFallido(@RequestParam(name = "preference_id", required = false) String preferenceId) {
        return html("Pago Rechazado", "El pago no se completó. Inténtalo nuevamente o usa otro medio de pago.", "/");
    }

    private List<CuotaMensual> obtenerCuotasDePreferencia(String preferenceId) {
        List<CuotaMensual> result = new ArrayList<>();
        if (preferenceId == null || preferenceId.isBlank()) {
            LOGGER.warn("No se recibió preference_id en la redirección de éxito");
            return result;
        }
        try {
            configurarMercadoPago();
        } catch (IllegalStateException e) {
            LOGGER.error("No se pudo preparar Mercado Pago para consultar la preferencia: {}", e.getMessage());
            return result;
        }

        PreferenceClient client = new PreferenceClient();
        try {
            Preference preference = client.get(preferenceId);
            List<PreferenceItem> items = preference.getItems();
            if (items == null || items.isEmpty()) {
                LOGGER.warn("La preferencia {} no tiene items para procesar", preferenceId);
                return result;
            }
            for (PreferenceItem item : items) {
                String cuotaId = item.getId();
                if (cuotaId == null || cuotaId.isBlank()) {
                    continue;
                }
                cuotaMensualRepositorio.findByIdAndEliminadoFalse(cuotaId).ifPresent(result::add);
            }
        } catch (MPApiException | MPException e) {
            LOGGER.error("No se pudo consultar la preferencia {}: {}", preferenceId, e.getMessage(), e);
        }
        return result;
    }

    private void marcarCuotasComoPagadas(List<CuotaMensual> cuotas, String preferenceId) {
        for (CuotaMensual cuota : cuotas) {
            if (cuota.getEstado() != EstadoCuota.PAGADA) {
                cuota.setEstado(EstadoCuota.PAGADA);
                cuotaMensualRepositorio.save(cuota);
                LOGGER.info("Cuota {} marcada como PAGADA tras preferencia {}", cuota.getId(), preferenceId);
            }
        }
    }

    private Optional<String> crearFacturaParaCuotas(List<CuotaMensual> cuotas) {
        try {
            // Obtener o crear forma de pago de billetera virtual (Mercado Pago)
            FormaDePago forma = formaDePagoRepositorio
                    .findFirstByTipoPagoAndEliminadoFalse(TipoPago.BILLETERA_VIRTUAL)
                    .orElseGet(() -> {
                        FormaDePago f = new FormaDePago();
                        f.setTipoPago(TipoPago.BILLETERA_VIRTUAL);
                        f.setObservacion("Mercado Pago");
                        f.setEliminado(false);
                        return formaDePagoRepositorio.save(f);
                    });

            double total = cuotas.stream()
                    .map(CuotaMensual::getValorCuota)
                    .filter(v -> v != null)
                    .mapToDouble(ValorCuota::getValorCuota)
                    .sum();

            Factura factura = new Factura();
            factura.setFechaFactura(LocalDate.now());
            factura.setTotalPagado(total);
            factura.setEstado(EstadoFactura.PAGADA);
            factura.setFormaDePago(forma);

            List<DetalleFactura> detalles = new ArrayList<>();
            for (CuotaMensual c : cuotas) {
                DetalleFactura d = new DetalleFactura();
                d.setCuotaMensual(c);
                d.setEliminado(false);
                detalles.add(d);
            }
            factura.setDetalles(detalles);

            Factura creada = facturaServicio.crear(factura);
            LOGGER.info("Factura {} creada por total {} para {} cuotas", creada.getId(), total, cuotas.size());
            return Optional.ofNullable(creada.getId());
        } catch (Exception e) {
            LOGGER.error("No se pudo crear la factura tras el pago: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private void configurarMercadoPago() {
        String tokenNormalizado = accessToken != null ? accessToken.trim() : "";
        LOGGER.info("Mercado Pago :: usando access token={} (longitud={})",
                enmascarar(tokenNormalizado), tokenNormalizado.length());
        if (tokenNormalizado.isEmpty()) {
            throw new IllegalStateException("Access token vacío. Configura mercadopago.access-token");
        }
        try {
            MercadoPagoConfig.setAccessToken(tokenNormalizado);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo configurar el access token: " + e.getMessage(), e);
        }
    }

    private ResponseEntity<String> html(String titulo, String mensaje, String redirectUrl) {
        String html = "<!DOCTYPE html>" +
                "<html lang=\"es\">" +
                "<head><meta charset=\"UTF-8\">" +
                "<meta http-equiv=\"refresh\" content=\"5;url=" + redirectUrl + "\">" +
                "<title>" + titulo + "</title>" +
                "<style>body{font-family:Arial, sans-serif;background:#f7f7f7;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;margin:0;}" +
                ".card{background:#fff;padding:30px;border-radius:12px;box-shadow:0 8px 18px rgba(0,0,0,.1);text-align:center;}" +
                "a{color:#ed563b;text-decoration:none;font-weight:bold;}" +
                "</style></head><body>" +
                "<div class=\"card\"><h1>" + titulo + "</h1>" +
                "<p>" + mensaje + "</p>" +
                "<p>Serás redirigido en unos segundos. Si no ocurre, haz clic <a href='" + redirectUrl + "'>aquí</a>.</p>" +
                "</div></body></html>";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8)
                .body(html);
    }

    private String enmascarar(String token) {
        if (token == null || token.isEmpty()) {
            return "<vacío>";
        }
        if (token.length() <= 10) {
            return token;
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
