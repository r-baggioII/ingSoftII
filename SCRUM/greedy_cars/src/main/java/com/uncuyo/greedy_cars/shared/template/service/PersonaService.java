package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uncuyo.greedy_cars.shared.template.dto.PersonaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.PersonaMapper;
import com.uncuyo.greedy_cars.shared.template.repository.BaseRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaService extends BaseService<Persona, String> {

    private final PersonaMapper personaMapper;
    private final DireccionRepository direccionRepository;

    @Autowired
    public PersonaService(BaseRepository<Persona, String> repository, 
                         PersonaMapper personaMapper,
                         DireccionRepository direccionRepository) {
        super(repository);
        this.personaMapper = personaMapper;
        this.direccionRepository = direccionRepository;
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
            
            // Buscar y asociar las direcciones existentes
            if (personaDTO.getDireccionIds() != null && !personaDTO.getDireccionIds().isEmpty()) {
                List<Direccion> direccionesExistentes = personaDTO.getDireccionIds().stream()
                    .map(id -> direccionRepository.findById(id)
                        .orElseThrow(() -> new ErrorServiceException("Dirección no encontrada con ID: " + id)))
                    .collect(Collectors.toList());
                
                persona.setDirecciones(direccionesExistentes);
            }
            
            Persona personaGuardada = alta(persona);
            return personaMapper.toDTO(personaGuardada);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear persona: " + e.getMessage());
        }
    }

    public Optional<PersonaDTO> modificarDTO(String id, PersonaDTO personaDTO) throws ErrorServiceException {
        try {
            Persona persona = personaMapper.toEntity(personaDTO);
            
            // Buscar y asociar las direcciones existentes
            if (personaDTO.getDireccionIds() != null && !personaDTO.getDireccionIds().isEmpty()) {
                List<Direccion> direccionesExistentes = personaDTO.getDireccionIds().stream()
                    .map(direccionId -> direccionRepository.findById(direccionId)
                        .orElseThrow(() -> new ErrorServiceException("Dirección no encontrada con ID: " + direccionId)))
                    .collect(Collectors.toList());
                
                persona.setDirecciones(direccionesExistentes);
            }
            
            Optional<Persona> personaModificada = modificar(id, persona);
            return personaModificada.map(personaMapper::toDTO);
        } catch (ErrorServiceException e) {
            throw e;
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
        
        // Actualizar direcciones si se proporcionan
        if (entidadNueva.getDirecciones() != null) {
            entidadExistente.getDirecciones().clear();
            entidadExistente.getDirecciones().addAll(entidadNueva.getDirecciones());
        }
    }
}
