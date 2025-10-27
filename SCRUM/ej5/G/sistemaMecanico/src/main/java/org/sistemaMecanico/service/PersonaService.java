package org.sistemaMecanico.service;

import org.sistemaMecanico.entity.Persona;
import org.sistemaMecanico.repository.BaseRepository;
import org.sistemaMecanico.exception.ErrorServiceException;
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
