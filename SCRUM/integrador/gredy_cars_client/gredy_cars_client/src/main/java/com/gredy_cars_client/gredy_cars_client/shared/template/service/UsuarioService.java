package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.UsuarioApiDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.UsuarioDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Servicio para gestionar usuarios en el cliente.
 * Extiende BaseClientService y añade validaciones específicas para usuarios.
 */
@Service
public class UsuarioService extends BaseClientService<UsuarioDTO, String> {

    @Autowired
    public UsuarioService(UsuarioApiDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, UsuarioDTO dto) throws ErrorServiceException {
        if (dto == null) {
            throw new ErrorServiceException("El usuario no puede ser nulo");
        }

        if (dto.getNombreUsuario() == null || dto.getNombreUsuario().trim().isEmpty()) {
            throw new ErrorServiceException("El nombre de usuario es obligatorio");
        }

        if (useCase == BaseUseCaseService.ALTA) {
            if (dto.getClave() == null || dto.getClave().trim().isEmpty()) {
                throw new ErrorServiceException("La contraseña es obligatoria");
            }
            if (dto.getClave().length() < 6) {
                throw new ErrorServiceException("La contraseña debe tener al menos 6 caracteres");
            }
        }

        if (dto.getRol() == null) {
            throw new ErrorServiceException("El rol es obligatorio");
        }

        // Validar que el usuario esté asociado a una persona
        if (dto.getPersonaId() == null || dto.getPersonaId().trim().isEmpty()) {
            throw new ErrorServiceException("El usuario debe estar asociado a una persona (cliente o empleado)");
        }
    }

    @Override
    protected void preAlta(UsuarioDTO dto) throws ErrorServiceException {
        // Lógica adicional antes de crear un usuario
        log.info("Creando usuario: {}", dto.getNombreUsuario());
    }

    @Override
    protected void postAlta(UsuarioDTO dto) throws ErrorServiceException {
        // Lógica adicional después de crear un usuario
        log.info("Usuario creado exitosamente: {}", dto.getNombreUsuario());
    }

    @Override
    protected void preModificacion(String id, UsuarioDTO dto) throws ErrorServiceException {
        // Validar que el usuario existe antes de modificar
        log.info("Modificando usuario con id: {}", id);
    }

    @Override
    protected void postModificacion(UsuarioDTO dto) throws ErrorServiceException {
        log.info("Usuario modificado exitosamente: {}", dto.getNombreUsuario());
    }

    @Override
    protected void preBaja(String id) throws ErrorServiceException {
        log.info("Eliminando usuario con id: {}", id);
    }

    @Override
    protected void postBaja(String id) throws ErrorServiceException {
        log.info("Usuario eliminado exitosamente con id: {}", id);
    }

    public void resetPassword(String id, String nuevaClave) throws ErrorServiceException {
        if (!StringUtils.hasText(nuevaClave)) {
            throw new ErrorServiceException("La nueva contraseña es obligatoria");
        }
        UsuarioDTO usuario = obtener(id)
            .orElseThrow(() -> new ErrorServiceException("Usuario no encontrado"));
        usuario.setClave(nuevaClave);
        Optional<UsuarioDTO> actualizado = modificar(id, usuario);
        if (actualizado.isEmpty()) {
            throw new ErrorServiceException("No se pudo actualizar la contraseña");
        }
    }
}
