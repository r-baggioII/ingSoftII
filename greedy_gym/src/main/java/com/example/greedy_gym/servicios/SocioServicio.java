package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.TipoDocumento;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class SocioServicio {

    private final SocioRepositorio socioRepositorio;

    public Socio crearSocio(Socio socio) {
        if (socio == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos del socio son obligatorios");
        }
        return crearSocio(socio.getNombre(), socio.getApellido(), socio.getFechaNacimiento(),
                socio.getTipoDocumento(), socio.getNumeroDocumento(), socio.getTelefono(),
                socio.getCorreoElectronico(), socio.getNumeroSocio());
    }

    public Socio crearSocio(String nombre, String apellido, LocalDate fechaNacimiento,
            TipoDocumento tipoDocumento, String numeroDocumento, String telefono,
            String correoElectronico, Long numeroSocio) {
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                telefono, correoElectronico, numeroSocio);
        Socio socio = new Socio();
        aplicarDatos(socio, nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                telefono, correoElectronico, numeroSocio);
        socio.setEliminado(false);
        return socioRepositorio.save(socio);
    }

    public Socio modificarSocio(String id, Socio cambios) {
        if (cambios == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos del socio son obligatorios");
        }
        return mdoificarSocio(id, cambios.getNombre(), cambios.getApellido(), cambios.getFechaNacimiento(),
                cambios.getTipoDocumento(), cambios.getNumeroDocumento(), cambios.getNumeroSocio());
    }

    public Socio mdoificarSocio(String id, String nombre, String apellido, LocalDate fechaNacimiento,
            TipoDocumento tipoDocumento, String numeroDocumento, Long numeroSocio) {
        Socio existente = socioRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Socio no encontrado"));
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                existente.getTelefono(), existente.getCorreoElectronico(), numeroSocio, id);
        aplicarDatos(existente, nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                existente.getTelefono(), existente.getCorreoElectronico(), numeroSocio);
        return socioRepositorio.save(existente);
    }

    @Transactional(readOnly = true)
    public Socio buscarPersona(String id) {
        return socioRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Socio no encontrado"));
    }

    public void eliminarPersona(String id) {
        Socio socio = socioRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Socio no encontrado"));
        socio.setEliminado(true);
        socioRepositorio.save(socio);
    }

    @Transactional(readOnly = true)
    public List<Socio> listarSocio() {
        return socioRepositorio.findAllByOrderByApellidoAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Socio> listarSocioActivo() {
        return socioRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc();
    }

    public Socio asociarSocioUsuario(String socioId, String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        }
        Socio socio = socioRepositorio.findByIdAndEliminadoFalse(socioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Socio no encontrado"));
        socio.setUsuarioId(usuarioId);
        return socioRepositorio.save(socio);
    }

    // Método sobrecargado según el diagrama de clases
    public void asociarSocioUsuario(Socio socio, Usuario usuario) {
        if (socio == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El socio es obligatorio");
        }
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }
        asociarSocioUsuario(socio.getId(), usuario.getId());
    }

    // Método de validación según el diagrama de clases - sobrecarga con long en lugar de Long
    public void validar(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
            String numeroDocumento, String telefono, String correoElectronico, long numeroSocio) {
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico, (Long) numeroSocio, null);
    }

    private void validar(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
            String numeroDocumento, String telefono, String correoElectronico, Long numeroSocio) {
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico, numeroSocio, null);
    }

    private void validar(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
            String numeroDocumento, String telefono, String correoElectronico, Long numeroSocio,
            String idActual) {
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El apellido es obligatorio");
        }
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de nacimiento es inválida");
        }
        if (tipoDocumento == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de documento es obligatorio");
        }
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de documento es obligatorio");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El teléfono es obligatorio");
        }
        if (correoElectronico == null || correoElectronico.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico es obligatorio");
        }
        if (numeroSocio == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de socio es obligatorio");
        }
        
        // Validar que no exista otro socio con el mismo número de documento
        socioRepositorio.findByNumeroDocumentoAndEliminadoFalse(numeroDocumento)
                .filter(encontrado -> !Objects.equals(encontrado.getId(), idActual))
                .ifPresent(e -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un socio con ese documento");
                });
        
        // Validar que no exista otro socio con el mismo correo electrónico
        socioRepositorio.findByCorreoElectronicoAndEliminadoFalse(correoElectronico)
                .filter(encontrado -> !Objects.equals(encontrado.getId(), idActual))
                .ifPresent(e -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un socio con ese correo electrónico");
                });
        
        // Validar que no exista otro socio con el mismo número de socio
        socioRepositorio.findByNumeroSocioAndEliminadoFalse(numeroSocio)
                .filter(encontrado -> !Objects.equals(encontrado.getId(), idActual))
                .ifPresent(e -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un socio con ese número de socio");
                });
    }

    private void aplicarDatos(Socio socio, String nombre, String apellido, LocalDate fechaNacimiento,
            TipoDocumento tipoDocumento, String numeroDocumento, String telefono,
            String correoElectronico, Long numeroSocio) {
        socio.setNombre(nombre.trim());
        socio.setApellido(apellido.trim());
        socio.setFechaNacimiento(fechaNacimiento);
        socio.setTipoDocumento(tipoDocumento);
        socio.setNumeroDocumento(numeroDocumento.trim());
        socio.setTelefono(telefono.trim());
        socio.setCorreoElectronico(correoElectronico.trim().toLowerCase());
        socio.setNumeroSocio(numeroSocio);
    }
}
