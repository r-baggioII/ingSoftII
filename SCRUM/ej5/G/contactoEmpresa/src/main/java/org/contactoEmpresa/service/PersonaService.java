package org.contactoEmpresa.service;

import org.contactoEmpresa.entity.Persona;
import org.contactoEmpresa.repository.BaseRepository;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaService extends BaseService<Persona, String> {

    @Autowired
    public PersonaService(BaseRepository<Persona, String> repository) {
        super(repository);
    }

    @Override
    protected void actualizarEntidad(Persona entidadExistente, Persona entidadNueva) {
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
        
        if (entidadNueva.getApellido() != null) {
            entidadExistente.setApellido(entidadNueva.getApellido());
        }
    }
}
