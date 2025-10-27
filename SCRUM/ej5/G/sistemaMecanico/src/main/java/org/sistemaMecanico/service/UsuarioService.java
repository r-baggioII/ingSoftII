package org.sistemaMecanico.service;

import org.sistemaMecanico.entity.Usuario;
import org.sistemaMecanico.repository.UsuarioRepository;
import org.sistemaMecanico.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UsuarioService extends BaseService<Usuario, String> {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository repository) {
        super(repository);
        this.usuarioRepository = repository;
    }

    /**
     * Registra un nuevo usuario encriptando su contraseña
     */
    public Usuario registrarUsuario(Usuario usuario) throws ErrorServiceException {
        // Validar que no exista el username
        if (usuarioRepository.existsByNombreUsuarioAndEliminadoIsFalse(usuario.getNombreUsuario())) {
            throw new ErrorServiceException("El nombre de usuario ya existe");
        }

        // Encriptar la contraseña
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Guardar el usuario
        return alta(usuario);
    }

    /**
     * Busca un usuario por nombre de usuario
     */
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuarioAndEliminadoIsFalse(nombreUsuario);
    }

    /**
     * Actualiza los campos de un usuario existente con los datos de un usuario nuevo.
     */
    @Override
    protected void actualizarEntidad(Usuario entidadExistente, Usuario entidadNueva) {
        if (entidadNueva.getNombreUsuario() != null) {
            entidadExistente.setNombreUsuario(entidadNueva.getNombreUsuario());
        }

        // Solo actualizar la clave si se proporciona una nueva Y encriptarla
        if (entidadNueva.getClave() != null && !entidadNueva.getClave().isEmpty()) {
            entidadExistente.setClave(passwordEncoder.encode(entidadNueva.getClave()));
        }

        // Actualizar rol si se proporciona
        if (entidadNueva.getRol() != null) {
            entidadExistente.setRol(entidadNueva.getRol());
        }
    }
}