package com.uncuyo.greedy_cars.shared.template.service;


import com.uncuyo.greedy_cars.shared.template.dto.PersonaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.PersonaMapper;
import com.uncuyo.greedy_cars.shared.template.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService extends BaseService<Persona, String> {

    private final PersonaMapper personaMapper;

    @Autowired
    public PersonaService(BaseRepository<Persona, String> repository, PersonaMapper personaMapper) {
        super(repository);
        this.personaMapper = personaMapper;
    }

    // Métodos con DTOs
    public List<PersonaDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Persona> personas = listarActivos();
            return personaMapper.toDTOList(personas);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar personas: " + e.getMessage());
        }
    }
    
    public Optional<PersonaDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Persona> persona = obtener(id);
            return persona.map(personaMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener persona: " + e.getMessage());
        }
    }
    
    public PersonaDTO altaDTO(PersonaDTO personaDTO) throws ErrorServiceException {
        try {
            Persona persona = personaMapper.toEntity(personaDTO);
            Persona personaGuardada = alta(persona);
            return personaMapper.toDTO(personaGuardada);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear persona: " + e.getMessage());
        }
    }
    
    public Optional<PersonaDTO> modificarDTO(String id, PersonaDTO personaDTO) throws ErrorServiceException {
        try {
            Persona persona = personaMapper.toEntity(personaDTO);
            Optional<Persona> personaModificada = modificar(id, persona);
            return personaModificada.map(personaMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar persona: " + e.getMessage());
        }
    }

    @Override
    protected void actualizarEntidad(Persona entidadExistente, Persona entidadNueva) {
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
        
        if (entidadNueva.getApellido() != null) {
            entidadExistente.setApellido(entidadNueva.getApellido());
        }
        
        if (entidadNueva.getFechaNacimiento() != null) {
            entidadExistente.setFechaNacimiento(entidadNueva.getFechaNacimiento());
        }
        
        if (entidadNueva.getTipoDocumento() != null) {
            entidadExistente.setTipoDocumento(entidadNueva.getTipoDocumento());
        }
        
        if (entidadNueva.getNumeroDocumento() != null) {
            entidadExistente.setNumeroDocumento(entidadNueva.getNumeroDocumento());
        }
        
        if (entidadNueva.getDirecciones() != null) {
            entidadExistente.setDirecciones(entidadNueva.getDirecciones());
        }
    }
}
