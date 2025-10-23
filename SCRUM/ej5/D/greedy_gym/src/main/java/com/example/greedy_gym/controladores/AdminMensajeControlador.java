package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Mensaje;
import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.TipoMensaje;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.servicios.MensajeServicio;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/admin/mensajes")
public class AdminMensajeControlador {

    private final MensajeServicio mensajeServicio;

    public AdminMensajeControlador(MensajeServicio mensajeServicio) {
        this.mensajeServicio = mensajeServicio;
    }

    @GetMapping
    public List<MensajeResponse> listar(HttpSession session) {
        Usuario admin = validarAdmin(session);
        return mensajeServicio.listarMensajes().stream()
                .map(MensajeResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public MensajeResponse obtener(@PathVariable String id, HttpSession session) {
        validarAdmin(session);
        return MensajeResponse.from(mensajeServicio.buscarMensaje(id));
    }

    @PostMapping
    public ResponseEntity<MensajeResponse> crear(@RequestBody MensajeRequest request, HttpSession session) {
        Usuario admin = validarAdmin(session);
        Mensaje mensaje = mensajeServicio.crearMensaje(admin, request.titulo(), request.texto(), request.tipoMensaje());
        return ResponseEntity.status(HttpStatus.CREATED).body(MensajeResponse.from(mensaje));
    }

    @PutMapping("/{id}")
    public MensajeResponse actualizar(@PathVariable String id, @RequestBody MensajeRequest request, HttpSession session) {
        Usuario admin = validarAdmin(session);
        Mensaje mensaje = mensajeServicio.modificarMensaje(id, admin, request.titulo(), request.texto(), request.tipoMensaje());
        return MensajeResponse.from(mensaje);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable String id, HttpSession session) {
        validarAdmin(session);
        mensajeServicio.eliminarMensaje(id);
        return ResponseEntity.ok(Map.of("mensaje", "Mensaje eliminado"));
    }

    private Usuario validarAdmin(HttpSession session) {
        Object usuarioObj = session.getAttribute("usuario");
        if (!(usuarioObj instanceof Usuario usuario) || usuario.getRol() != RolUsuario.ADMINISTRATIVO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Debes iniciar sesión como administrador");
        }
        return usuario;
    }

    public record MensajeRequest(String titulo, String texto, String tipo) {
        public TipoMensaje tipoMensaje() {
            if (tipo == null) {
                throw new ValidationException("El tipo de mensaje es obligatorio");
            }
            try {
                return TipoMensaje.valueOf(tipo.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new ValidationException("Tipo de mensaje inválido: " + tipo);
            }
        }
    }

    public record MensajeResponse(String id, String titulo, String texto, TipoMensaje tipo,
                                  boolean eliminado, String usuarioId) {
        public static MensajeResponse from(Mensaje mensaje) {
            return new MensajeResponse(
                    mensaje.getId(),
                    mensaje.getTitulo(),
                    mensaje.getTexto(),
                    mensaje.getTipoMensaje(),
                    mensaje.isEliminado(),
                    mensaje.getUsuario() != null ? mensaje.getUsuario().getId() : null
            );
        }
    }
}
