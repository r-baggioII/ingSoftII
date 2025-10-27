package org.sistemaMecanico.service;

import org.sistemaMecanico.entity.Cliente;
import org.sistemaMecanico.repository.BaseRepository;
import org.sistemaMecanico.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService extends BaseService<Cliente, String> {

    @Autowired
    public ClienteService(BaseRepository<Cliente, String> repository) {
        super(repository);
    }

    @Override
    protected void actualizarEntidad(Cliente entidadExistente, Cliente entidadNueva) {
        // Actualizar campos heredados de Persona
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
        if (entidadNueva.getApellido() != null) {
            entidadExistente.setApellido(entidadNueva.getApellido());
        }
        
        // Actualizar campos propios de Cliente
        if (entidadNueva.getDocumento() != null) {
            entidadExistente.setDocumento(entidadNueva.getDocumento());
        }

    }
}
