package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.UsuarioRepositorio;
import jakarta.validation.ValidationException;
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

    public Usuario crearUsuario(String nombreUsuario, String clave, String correoElectronico) {
        validar(nombreUsuario, correoElectronico);
        Usuario usuario = new Usuario(nombreUsuario, clave, correoElectronico);
        return repository.save(usuario);
    }

    public void validar(String nombreUsuario, String correoElectronico) {
        if (repository.findByNombreUsuarioIgnoreCase(nombreUsuario).isPresent()) {
            throw new ValidationException("El nombre de usuario ya existe: " + nombreUsuario);
        }
        if (repository.findByCorreoElectronicoIgnoreCase(correoElectronico).isPresent()) {
            throw new ValidationException("El correo electrónico ya está registrado: " + correoElectronico);
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(String id) {
        return repository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    public void modificarUsuario(String id, String nombreUsuario, String clave, String correoElectronico) {
        Usuario actual = buscarUsuario(id);
        if (!actual.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
            validar(nombreUsuario, correoElectronico);
            actual.setNombreUsuario(nombreUsuario);
        }
        actual.setClave(clave);
        actual.setCorreoElectronico(correoElectronico);
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
}
