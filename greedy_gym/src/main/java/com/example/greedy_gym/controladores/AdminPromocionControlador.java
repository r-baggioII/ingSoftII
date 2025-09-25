package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Promocion;
import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.servicios.PromocionServicio;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/promociones")
public class AdminPromocionControlador {

    private final PromocionServicio promocionServicio;

    public AdminPromocionControlador(PromocionServicio promocionServicio) {
        this.promocionServicio = promocionServicio;
    }

    @GetMapping
    public List<PromocionResponse> listar(HttpSession session) {
        validarAdmin(session);
        return promocionServicio.listarPromociones().stream()
                .map(PromocionResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PromocionResponse obtener(@PathVariable String id, HttpSession session) {
        validarAdmin(session);
        return PromocionResponse.from(promocionServicio.buscarPromocion(id));
    }

    @PostMapping
    public ResponseEntity<PromocionResponse> crear(@RequestBody PromocionRequest request, HttpSession session) {
        Usuario admin = validarAdmin(session);
        Promocion promocion = promocionServicio.crearPromocion(admin, request.fechaEnvioParsed(),
                request.tituloVal(), request.textoVal(), request.destinatariosSeleccionados(),
                request.enviarATodosFlag());
        return ResponseEntity.status(HttpStatus.CREATED).body(PromocionResponse.from(promocion));
    }

    @PutMapping("/{id}")
    public PromocionResponse actualizar(@PathVariable String id, @RequestBody PromocionRequest request,
                                        HttpSession session) {
        Usuario admin = validarAdmin(session);
        Promocion promocion = promocionServicio.modificarPromocion(id, admin, request.fechaEnvioParsed(),
                request.tituloVal(), request.textoVal(), request.destinatariosSeleccionados(),
                request.enviarATodosFlag());
        return PromocionResponse.from(promocion);
    }

    @PostMapping("/{id}/enviar")
    public Map<String, Object> enviarAhora(@PathVariable String id, HttpSession session) {
        validarAdmin(session);
        int enviados = promocionServicio.enviarPromocionAhora(id);
        return Map.of("mensaje", "Promoción enviada", "enviados", enviados);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> eliminar(@PathVariable String id, HttpSession session) {
        validarAdmin(session);
        promocionServicio.eliminarPromocion(id);
        return Map.of("mensaje", "Promoción eliminada");
    }

    private Usuario validarAdmin(HttpSession session) {
        Object usuarioObj = session.getAttribute("usuario");
        if (!(usuarioObj instanceof Usuario usuario) || usuario.getRol() != RolUsuario.ADMINISTRATIVO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Debes iniciar sesión como administrador");
        }
        return usuario;
    }

    public record PromocionRequest(String titulo, String texto, String fechaEnvio,
                                   Set<String> destinatarios, Boolean enviarATodos) {
        public LocalDateTime fechaEnvioParsed() {
            if (fechaEnvio == null) {
                throw new ValidationException("La fecha de envío es obligatoria");
            }
            String valor = fechaEnvio.trim();
            try {
                return LocalDateTime.parse(valor);
            } catch (DateTimeParseException ex) {
                try {
                    return OffsetDateTime.parse(valor).toLocalDateTime();
                } catch (DateTimeParseException ex2) {
                    throw new ValidationException("Formato de fecha inválido. Usa ISO-8601.");
                }
            }
        }

        public String tituloVal() {
            if (titulo == null || titulo.trim().isEmpty()) {
                throw new ValidationException("El título es obligatorio");
            }
            return titulo;
        }

        public String textoVal() {
            if (texto == null || texto.trim().isEmpty()) {
                throw new ValidationException("El texto es obligatorio");
            }
            return texto;
        }

        public Set<String> destinatariosSeleccionados() {
            if (enviarATodosFlag()) {
                return Set.of();
            }
            return CollectionUtils.isEmpty(destinatarios) ? Set.of() : destinatarios;
        }

        public boolean enviarATodosFlag() {
            return Boolean.TRUE.equals(enviarATodos);
        }
    }

    public record PromocionResponse(String id, String titulo, String texto, String fechaEnvioPromocion,
                                    boolean enviada, Long cantidadSociosEnviados, String fechaEnvioReal,
                                    List<String> destinatariosIds, boolean enviarATodos) {
        public static PromocionResponse from(Promocion promocion) {
            return new PromocionResponse(
                    promocion.getId(),
                    promocion.getTitulo(),
                    promocion.getTexto(),
                    promocion.getFechaEnvioPromocion() != null ? promocion.getFechaEnvioPromocion().toString() : null,
                    promocion.isEnviada(),
                    promocion.getCantidadSociosEnviados(),
                    promocion.getFechaEnvioReal() != null ? promocion.getFechaEnvioReal().toString() : null,
                    promocion.getDestinatarios().stream().map(socio -> socio.getId()).toList(),
                    promocion.getDestinatarios() == null || promocion.getDestinatarios().isEmpty()
            );
        }
    }
}
