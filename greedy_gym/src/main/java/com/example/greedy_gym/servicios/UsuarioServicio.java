package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.UsuarioRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UsuarioServicio {

    private final UsuarioRepositorio repository;

    public UsuarioServicio(UsuarioRepositorio repository) {
        this.repository = repository;
    }

    public Usuario crearUsuario(@NotBlank String nombreUsuario, 
                               @NotBlank String clave, 
                               @NotNull RolUsuario rol) {
        Usuario usuario = new Usuario(nombreUsuario, clave, rol);
        return repository.save(usuario);
    }

    public void validar(String nombreUsuario, String clave, RolUsuario rol) {
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
}
