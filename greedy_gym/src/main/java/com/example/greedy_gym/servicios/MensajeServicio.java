package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Mensaje;
import com.example.greedy_gym.entidades.TipoMensaje;
import com.example.greedy_gym.repositorios.MensajeRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MensajeServicio {

    private final MensajeRepositorio repository;

    public MensajeServicio(MensajeRepositorio repository) {
        this.repository = repository;
    }

    public void crearMensaje(@NotBlank String idUsuario,
                             @NotBlank String titulo,
                             @NotBlank String texto,
                             TipoMensaje tipoMensaje) {
        validar(idUsuario, null, titulo, texto, tipoMensaje);
        Mensaje mensaje = new Mensaje(titulo, texto, tipoMensaje);
        repository.save(mensaje);
    }

    public boolean validar(@NotBlank String idUsuario,
                           Date fechaPromocion,
                           @NotBlank String titulo,
                           @NotBlank String texto,
                           TipoMensaje tipoMensaje) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new ValidationException("idUsuario es obligatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new ValidationException("titulo es obligatorio");
        }
        if (texto == null || texto.isBlank()) {
            throw new ValidationException("texto es obligatorio");
        }
        if (tipoMensaje == null) {
            throw new ValidationException("tipoMensaje es obligatorio");
        }
        if (tipoMensaje == TipoMensaje.PROMOCION) {
            if (fechaPromocion == null) {
                throw new ValidationException("fechaPromocion es obligatoria para PROMOCION");
            }
            if (fechaPromocion.before(new Date())) {
                throw new ValidationException("fechaPromocion no puede ser en el pasado");
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Mensaje buscarMensaje(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado: " + id));
    }

    public void modificarMensaje(String id,
                                 @NotBlank String idUsuario,
                                 @NotBlank String titulo,
                                 @NotBlank String texto,
                                 TipoMensaje tipoMensaje) {
        Mensaje actual = buscarMensaje(id);
        validar(idUsuario, null, titulo, texto, tipoMensaje);
        actual.setTitulo(titulo);
        actual.setTexto(texto);
        actual.setTipoMensaje(tipoMensaje);
        repository.save(actual);
    }

    public void eliminarMensaje(String id) {
        Mensaje actual = buscarMensaje(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public Collection<Mensaje> listarMensaje() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Collection<Mensaje> listarMensajeActivo() {
        return repository.findAll().stream().filter(m -> !m.isEliminado()).toList();
    }

    public void enviarMensaje(String id) {
        Mensaje mensaje = buscarMensaje(id);
        if (mensaje.isEliminado()) {
            throw new IllegalStateException("No se puede enviar un mensaje eliminado");
        }
        // no-op por ahora
    }
}
