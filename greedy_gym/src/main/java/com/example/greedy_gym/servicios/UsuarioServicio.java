package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Empleado;
import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.TipoEmpleado;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.UsuarioRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UsuarioServicio {

    private final UsuarioRepositorio repository;
    private final SocioServicio socioServicio;
    private final EmpleadoServicio empleadoServicio;

    public UsuarioServicio(UsuarioRepositorio repository,
                           SocioServicio socioServicio,
                           EmpleadoServicio empleadoServicio) {
        this.repository = repository;
        this.socioServicio = socioServicio;
        this.empleadoServicio = empleadoServicio;
    }

    public Usuario crearUsuario(@NotBlank String nombreUsuario, 
                               @NotBlank String clave, 
                               @NotNull RolUsuario rol) {
        validar(nombreUsuario, clave, rol);
        Usuario usuario = new Usuario(nombreUsuario, clave, rol);
        return repository.save(usuario);
    }

    public Usuario crearUsuario(CrearUsuarioCommand command) {
        if (command == null) {
            throw new ValidationException("Los datos del usuario son obligatorios");
        }

        Usuario usuario = crearUsuario(command.nombreUsuario(), command.clave(), command.rol());

        if (command.rol() == RolUsuario.SOCIO) {
            manejarAsociacionSocio(command, usuario);
        } else {
            manejarAsociacionEmpleado(command, usuario);
        }

        return usuario;
    }

    private void manejarAsociacionSocio(CrearUsuarioCommand command, Usuario usuario) {
        if (command.socioId() != null && !command.socioId().isBlank()) {
            socioServicio.asociarSocioUsuario(command.socioId(), usuario.getId());
            return;
        }
        Socio socioNuevo = command.nuevoSocio();
        if (socioNuevo == null) {
            throw new ValidationException("Debe proporcionar los datos del socio o un socio existente");
        }
        socioServicio.crearSocioConUsuario(socioNuevo, usuario);
    }

    private void manejarAsociacionEmpleado(CrearUsuarioCommand command, Usuario usuario) {
        if (command.empleadoId() != null && !command.empleadoId().isBlank()) {
            empleadoServicio.asociarEmpleadoUsuario(command.empleadoId(), usuario.getId());
            return;
        }
        Empleado nuevoEmpleado = command.nuevoEmpleado();
        if (nuevoEmpleado == null) {
            throw new ValidationException("Debe proporcionar los datos del empleado");
        }
        if (nuevoEmpleado.getTipoEmpleado() == null) {
            if (command.rol() == RolUsuario.ADMINISTRATIVO) {
                nuevoEmpleado.setTipoEmpleado(TipoEmpleado.ADMINISTRATIVO);
            } else if (command.rol() == RolUsuario.PROFESOR) {
                nuevoEmpleado.setTipoEmpleado(TipoEmpleado.PROFESOR);
            }
        }
        if (nuevoEmpleado.getTipoEmpleado() == null) {
            throw new ValidationException("Debe indicar el tipo de empleado");
        }
        empleadoServicio.crearEmpleadoConUsuario(nuevoEmpleado, usuario);
    }

    public void validar(String nombreUsuario, String clave, RolUsuario rol) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ValidationException("El nombre de usuario es obligatorio");
        }
        if (clave == null || clave.isBlank()) {
            throw new ValidationException("La clave es obligatoria");
        }
        if (rol == null) {
            throw new ValidationException("El rol es obligatorio");
        }
        if (repository.findByNombreUsuarioIgnoreCase(nombreUsuario).isPresent()) {
            throw new ValidationException("El nombre de usuario ya existe: " + nombreUsuario);
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(String id) {
        return repository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    public void modificarUsuario(String id, @NotBlank String nombreUsuario, 
                                @NotBlank String clave, 
                                @NotNull RolUsuario rol) {
        Usuario actual = buscarUsuario(id);
        if (!actual.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
            validar(nombreUsuario, clave, rol);
            actual.setNombreUsuario(nombreUsuario);
        }
        actual.setClave(clave);
        actual.setRol(rol);
        repository.save(actual);
    }

    public void eliminarUsuario(String id) {
        Usuario actual = buscarUsuario(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosActivos() {
        return repository.findAll().stream()
                .filter(usuario -> !usuario.isEliminado())
                .toList();
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorNombre(String nombre) {
        return repository.findByNombreUsuarioIgnoreCase(nombre)
                .filter(usuario -> !usuario.isEliminado())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + nombre));
    }

    @Transactional(readOnly = true)
    public Usuario login(String nombreUsuario, String clave) {
        Usuario usuario = repository.findByNombreUsuarioIgnoreCase(nombreUsuario)
                .filter(u -> !u.isEliminado())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + nombreUsuario));
        if (!usuario.getClave().equals(clave)) {
            throw new ValidationException("Clave incorrecta");
        }
        return usuario;
    }

    public void modificarClave(String id, String claveActual, String nuevaClave, String confirmarClave) {
        Usuario actual = buscarUsuario(id);
        if (!actual.getClave().equals(claveActual)) {
            throw new ValidationException("Clave actual incorrecta");
        }
        if (!nuevaClave.equals(confirmarClave)) {
            throw new ValidationException("La nueva clave y la confirmación no coinciden");
        }
        actual.setClave(nuevaClave);
        repository.save(actual);
    }

    public record CrearUsuarioCommand(String nombreUsuario,
                                      String clave,
                                      RolUsuario rol,
                                      String socioId,
                                      Socio nuevoSocio,
                                      String empleadoId,
                                      Empleado nuevoEmpleado) {

        public CrearUsuarioCommand {
            Objects.requireNonNull(nombreUsuario, "nombreUsuario no puede ser nulo");
            Objects.requireNonNull(clave, "clave no puede ser nula");
            Objects.requireNonNull(rol, "rol no puede ser nulo");
        }
    }
}
