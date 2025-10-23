package org.example.service;

import org.example.entity.Usuario;
import org.example.exception.ErrorServiceException;
import org.example.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService extends BaseService<Usuario, String> {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void preAlta(Usuario entidad) throws ErrorServiceException {
        // Generar ID si no existe
        if (entidad.getId() == null || entidad.getId().isEmpty()) {
            entidad.setId(UUID.randomUUID().toString());
        }
        
        // Verificar que no exista un usuario con el mismo nombre de usuario
        if (usuarioRepository.existsByNombreUsuario(entidad.getNombreUsuario())) {
            throw new ErrorServiceException("Ya existe un usuario con el nombre de usuario: " + entidad.getNombreUsuario());
        }
        
        // Aquí podrías agregar encriptación de contraseña
        // Por ejemplo: entidad.setClave(passwordEncoder.encode(entidad.getClave()));
    }

    @Override
    protected void preModificacion(Usuario entidad) throws ErrorServiceException {
        // Verificar que no exista otro usuario con el mismo nombre de usuario
        usuarioRepository.findByNombreUsuario(entidad.getNombreUsuario())
            .ifPresent(usuarioExistente -> {
                if (!usuarioExistente.getId().equals(entidad.getId())) {
                    try {
                        throw new ErrorServiceException("Ya existe otro usuario con el nombre de usuario: " + entidad.getNombreUsuario());
                    } catch (ErrorServiceException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        
        // Si la contraseña está vacía al modificar, mantener la anterior
        if (entidad.getClave() == null || entidad.getClave().trim().isEmpty()) {
            Usuario usuarioExistente = usuarioRepository.findById(entidad.getId())
                .orElseThrow(() -> new ErrorServiceException("Usuario no encontrado"));
            entidad.setClave(usuarioExistente.getClave());
        }
    }

    @Override
    protected void validar(org.example.enums.BaseUseCaseService useCase, Usuario entidad) throws ErrorServiceException {
        if (entidad.getNombreUsuario() == null || entidad.getNombreUsuario().trim().isEmpty()) {
            throw new ErrorServiceException("El nombre de usuario es obligatorio");
        }
        
        // Solo validar la contraseña en altas (no en modificaciones si está vacía)
        if (useCase == org.example.enums.BaseUseCaseService.ALTA) {
            if (entidad.getClave() == null || entidad.getClave().trim().isEmpty()) {
                throw new ErrorServiceException("La contraseña es obligatoria");
            }
            
            if (entidad.getClave().length() < 6) {
                throw new ErrorServiceException("La contraseña debe tener al menos 6 caracteres");
            }
        } else if (useCase == org.example.enums.BaseUseCaseService.MODIFICACION) {
            // Solo validar si se está cambiando la contraseña
            if (entidad.getClave() != null && !entidad.getClave().trim().isEmpty() && entidad.getClave().length() < 6) {
                throw new ErrorServiceException("La contraseña debe tener al menos 6 caracteres");
            }
        }
    }

    // Métodos adicionales
    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws ErrorServiceException {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
            .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
            .orElseThrow(() -> new ErrorServiceException("No se encontró el usuario: " + nombreUsuario));
    }

    public boolean validarCredenciales(String nombreUsuario, String clave) throws ErrorServiceException {
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);
        // Aquí deberías comparar con la contraseña encriptada
        // Por ejemplo: return passwordEncoder.matches(clave, usuario.getClave());
        return usuario.getClave().equals(clave);
    }
}
