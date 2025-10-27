package org.sistemaMecanico.service;

import org.sistemaMecanico.entity.Usuario;
import org.sistemaMecanico.repository.UsuarioRepository;
import org.sistemaMecanico.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends BaseService<Usuario, String> {

    @Autowired
    public UsuarioService(UsuarioRepository repository) {
        super(repository);
    }

    /**
     * Actualiza los campos de un usuario existente con los datos de un usuario nuevo.
     * No sobrescribe el ID ni el campo eliminado.
     * NOTA: La contraseña debe actualizarse con cuidado (hash).
     */
    @Override
    protected void actualizarEntidad(Usuario entidadExistente, Usuario entidadNueva) {
        if (entidadNueva.getNombreUsuario() != null) {
            entidadExistente.setNombreUsuario(entidadNueva.getNombreUsuario());
        }
        
        // Solo actualizar la clave si se proporciona una nueva
        // En un escenario real, aquí deberías hashear la contraseña
        if (entidadNueva.getClave() != null && !entidadNueva.getClave().isEmpty()) {
            entidadExistente.setClave(entidadNueva.getClave());
        }
    }
}
