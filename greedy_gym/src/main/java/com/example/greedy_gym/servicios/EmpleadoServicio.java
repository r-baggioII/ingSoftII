package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Empleado;
import com.example.greedy_gym.entidades.TipoDocumento;
import com.example.greedy_gym.entidades.TipoEmpleado;
import com.example.greedy_gym.repositorios.EmpleadoRepositorio;
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
public class EmpleadoServicio {

    private final EmpleadoRepositorio empleadoRepositorio;

    public Empleado crearEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos del empleado son obligatorios");
        }
        return crearEmpleado(empleado.getNombre(), empleado.getApellido(), empleado.getFechaNacimiento(),
                empleado.getTipoDocumento(), empleado.getNumeroDocumento(), empleado.getTelefono(),
                empleado.getCorreoElectronico(), empleado.getTipoEmpleado());
    }

    public Empleado crearEmpleado(String nombre, String apellido, LocalDate fechaNacimiento,
            TipoDocumento tipoDocumento, String numeroDocumento, String telefono,
            String correoElectronico, TipoEmpleado tipoEmpleado) {
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                telefono, correoElectronico, tipoEmpleado, null);
        Empleado empleado = new Empleado();
        aplicarDatos(empleado, nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                telefono, correoElectronico, tipoEmpleado);
        empleado.setEliminado(false);
        return empleadoRepositorio.save(empleado);
    }

    public Empleado modificarEmpleado(String id, Empleado cambios) {
        if (cambios == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos del empleado son obligatorios");
        }
        return modificarEmpleado(id, cambios.getNombre(), cambios.getApellido(), cambios.getFechaNacimiento(),
                cambios.getTipoDocumento(), cambios.getNumeroDocumento(), cambios.getTelefono(),
                cambios.getCorreoElectronico(), cambios.getTipoEmpleado());
    }

    public Empleado modificarEmpleado(String id, String nombre, String apellido, LocalDate fechaNacimiento,
            TipoDocumento tipoDocumento, String numeroDocumento, String telefono,
            String correoElectronico, TipoEmpleado tipoEmpleado) {
        Empleado existente = empleadoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        validar(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                telefono, correoElectronico, tipoEmpleado, id);
        aplicarDatos(existente, nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento,
                telefono, correoElectronico, tipoEmpleado);
        return empleadoRepositorio.save(existente);
    }

    @Transactional(readOnly = true)
    public Empleado buscarPersona(String id) {
        return empleadoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
    }

    public void eliminarPersona(String id) {
        Empleado empleado = empleadoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        empleado.setEliminado(true);
        empleadoRepositorio.save(empleado);
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarEmpleado() {
        return empleadoRepositorio.findAllByOrderByApellidoAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarEmpleadoActivo() {
        return empleadoRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc();
    }

    public Empleado asociarEmpleadoUsuario(String empleadoId, String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        }
        Empleado empleado = empleadoRepositorio.findByIdAndEliminadoFalse(empleadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        empleado.setUsuarioId(usuarioId);
        return empleadoRepositorio.save(empleado);
    }

    private void validar(String nombre, String apellido, LocalDate fechaNacimiento, TipoDocumento tipoDocumento,
            String numeroDocumento, String telefono, String correoElectronico, TipoEmpleado tipoEmpleado,
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
        if (tipoEmpleado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de empleado es obligatorio");
        }
        empleadoRepositorio.findByNumeroDocumentoAndEliminadoFalse(numeroDocumento)
                .filter(encontrado -> !Objects.equals(encontrado.getId(), idActual))
                .ifPresent(e -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un empleado con ese documento");
                });
        empleadoRepositorio.findByCorreoElectronicoAndEliminadoFalse(correoElectronico)
                .filter(encontrado -> !Objects.equals(encontrado.getId(), idActual))
                .ifPresent(e -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un empleado con ese correo electrónico");
                });
    }

    private void aplicarDatos(Empleado empleado, String nombre, String apellido, LocalDate fechaNacimiento,
            TipoDocumento tipoDocumento, String numeroDocumento, String telefono,
            String correoElectronico, TipoEmpleado tipoEmpleado) {
        empleado.setNombre(nombre.trim());
        empleado.setApellido(apellido.trim());
        empleado.setFechaNacimiento(fechaNacimiento);
        empleado.setTipoDocumento(tipoDocumento);
        empleado.setNumeroDocumento(numeroDocumento.trim());
        empleado.setTelefono(telefono.trim());
        empleado.setCorreoElectronico(correoElectronico.trim().toLowerCase());
        empleado.setTipoEmpleado(tipoEmpleado);
    }
}

