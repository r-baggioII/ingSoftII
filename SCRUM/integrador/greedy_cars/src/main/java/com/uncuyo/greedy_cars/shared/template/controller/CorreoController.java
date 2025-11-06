package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.CorreoTestRequest;
import com.uncuyo.greedy_cars.shared.template.dto.PromocionCorreoRequest;
import com.uncuyo.greedy_cars.shared.template.dto.RecordatorioDevolucionRequest;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.CorreoEventoService;
import com.uncuyo.greedy_cars.shared.template.service.NotificacionCorreoService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/correos")
public class CorreoController {

    private final CorreoEventoService correoEventoService;
    private final NotificacionCorreoService notificacionCorreoService;

    public CorreoController(CorreoEventoService correoEventoService,
                            NotificacionCorreoService notificacionCorreoService) {
        this.correoEventoService = correoEventoService;
        this.notificacionCorreoService = notificacionCorreoService;
    }

    @PostMapping("/recordatorios/devolucion")
    public ResponseEntity<?> enviarRecordatorioDevolucion(
            @Valid @RequestBody RecordatorioDevolucionRequest request) {
        try {
            correoEventoService.enviarRecordatorioDevolucion(
                    request.getAlquilerId(), request.getEmpresaId());
            // TODO: En el futuro este endpoint será invocado automáticamente desde un @Scheduled.
            return ResponseEntity.ok(Map.of("mensaje", "Recordatorio en proceso de envío"));
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/promociones")
    public ResponseEntity<?> enviarPromocion(
            @Valid @RequestBody PromocionCorreoRequest request) {
        try {
            correoEventoService.enviarPromocion(
                    request.getClienteId(), request.getCodigo(), request.getPorcentaje(), request.getEmpresaId());
            // TODO: en Greedy Gym esto lo disparaba un @Scheduled desde PromocionServicio.
            return ResponseEntity.ok(Map.of("mensaje", "Promoción en proceso de envío"));
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> enviarTest(@Valid @RequestBody CorreoTestRequest request) {
        notificacionCorreoService.enviarCorreoSimple(
                request.getDestino(),
                request.getAsunto() != null ? request.getAsunto() : "Prueba de correo",
                request.getCuerpo() != null ? request.getCuerpo() : "Este es un correo de prueba",
                request.getEmpresaId());
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud de envío aceptada"));
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("error", mensaje));
    }
}
