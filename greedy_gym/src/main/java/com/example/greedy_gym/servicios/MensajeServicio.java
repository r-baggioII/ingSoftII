package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Mensaje;
import com.example.greedy_gym.entidades.TipoMensaje;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.MensajeRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class MensajeServicio {

    private final MensajeRepositorio mensajeRepositorio;

    public Mensaje crearMensaje(Usuario usuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        validar(usuario, titulo, texto, tipoMensaje);
        Mensaje mensaje = new Mensaje();
        mensaje.setUsuario(usuario);
        mensaje.setTitulo(titulo.trim());
        mensaje.setTexto(texto.trim());
        mensaje.setTipoMensaje(tipoMensaje);
        return mensajeRepositorio.save(mensaje);
    }

    public Mensaje modificarMensaje(String id, Usuario usuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        Mensaje existente = mensajeRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado: " + id));
        validar(usuario, titulo, texto, tipoMensaje);
        existente.setUsuario(usuario);
        existente.setTitulo(titulo.trim());
        existente.setTexto(texto.trim());
        existente.setTipoMensaje(tipoMensaje);
        return mensajeRepositorio.save(existente);
    }

    @Transactional(readOnly = true)
    public Mensaje buscarMensaje(String id) {
        return mensajeRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Mensaje> listarMensajes() {
        return mensajeRepositorio.findByEliminadoFalseOrderByCreadoEnDesc();
    }

    public void eliminarMensaje(String id) {
        Mensaje mensaje = mensajeRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado: " + id));
        mensaje.setEliminado(true);
        mensajeRepositorio.save(mensaje);
    }

    @Transactional(readOnly = true)
    public Optional<Mensaje> buscarMensajeActivoPorTipo(TipoMensaje tipoMensaje) {
        return mensajeRepositorio.findFirstByTipoMensajeAndEliminadoFalseOrderByActualizadoEnDesc(tipoMensaje);
    }

    private void validar(Usuario usuario, String titulo, String texto, TipoMensaje tipoMensaje) {
        if (usuario == null) {
            throw new ValidationException("El usuario es obligatorio");
        }
        if (!StringUtils.hasText(titulo)) {
            throw new ValidationException("El título es obligatorio");
        }
        if (!StringUtils.hasText(texto)) {
            throw new ValidationException("El texto es obligatorio");
        }
        if (tipoMensaje == null) {
            throw new ValidationException("El tipo de mensaje es obligatorio");
        }
    }
}
