package com.uncuyo.greedy_cars.shared.template.service;


import com.uncuyo.greedy_cars.shared.template.dto.PersonaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.PersonaMapper;
import com.uncuyo.greedy_cars.shared.template.repository.BaseRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class PersonaService extends BaseService<Persona, String> {

    private final PersonaMapper personaMapper;
    private final ContactoRepository contactoRepository;
    private final ImagenRepository imagenRepository;
    private final DireccionRepository direccionRepository;

    @Autowired
    public PersonaService(BaseRepository<Persona, String> repository, 
                         PersonaMapper personaMapper,
                         ContactoRepository contactoRepository,
                         ImagenRepository imagenRepository,
                         DireccionRepository direccionRepository) {
        super(repository);
        this.personaMapper = personaMapper;
        this.contactoRepository = contactoRepository;
        this.imagenRepository = imagenRepository;
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
    protected void preAlta(Persona entidad) throws ErrorServiceException {
        super.preAlta(entidad);
        
        // Handle Direcciones - ensure they're managed entities
        if (entidad.getDirecciones() != null && !entidad.getDirecciones().isEmpty()) {
            List<Direccion> managedDirecciones = new ArrayList<>();
            for (Direccion direccion : entidad.getDirecciones()) {
                if (direccion.getId() != null) {
                    Optional<Direccion> existingDireccion = direccionRepository.findById(direccion.getId());
                    if (existingDireccion.isPresent()) {
                        managedDirecciones.add(existingDireccion.get());
                    } else {
                        throw new ErrorServiceException("Direccion no encontrada con ID: " + direccion.getId());
                    }
                } else {
                    managedDirecciones.add(direccion);
                }
            }
            entidad.getDirecciones().clear();
            entidad.getDirecciones().addAll(managedDirecciones);
        }
        
        // Handle Contactos - ManyToMany relationship
        // Los contactos pueden ser compartidos entre múltiples personas
        if (entidad.getContactos() != null && !entidad.getContactos().isEmpty()) {
            List<Contacto> managedContactos = new ArrayList<>();
            for (Contacto contacto : entidad.getContactos()) {
                if (contacto.getId() != null) {
                    // Es un contacto existente - lo obtenemos de la BD
                    Optional<Contacto> existingContacto = contactoRepository.findById(contacto.getId());
                    if (existingContacto.isPresent()) {
                        managedContactos.add(existingContacto.get());
                    } else {
                        throw new ErrorServiceException("Contacto no encontrado con ID: " + contacto.getId());
                    }
                } else {
                    // Es un contacto nuevo - simplemente lo agregamos
                    managedContactos.add(contacto);
                }
            }
            entidad.getContactos().clear();
            entidad.getContactos().addAll(managedContactos);
        }
        
        // Handle Imagenes - ensure they're managed entities
        if (entidad.getImagenes() != null && !entidad.getImagenes().isEmpty()) {
            List<Imagen> managedImagenes = new ArrayList<>();
            for (Imagen imagen : entidad.getImagenes()) {
                if (imagen.getId() != null) {
                    // It's an existing imagen - fetch from DB to get managed entity
                    Optional<Imagen> existingImagen = imagenRepository.findById(imagen.getId());
                    if (existingImagen.isPresent()) {
                        managedImagenes.add(existingImagen.get());
                    } else {
                        throw new ErrorServiceException("Imagen no encontrada con ID: " + imagen.getId());
                    }
                } else {
                    // It's a new imagen - just add it
                    managedImagenes.add(imagen);
                }
            }
            entidad.getImagenes().clear();
            entidad.getImagenes().addAll(managedImagenes);
        }
    }

    @Override
    protected void actualizarEntidad(Persona entidadExistente, Persona entidadNueva) {
        
        // --- Campos Simples ---
        
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

        // --- Corrección para Colecciones ---
        
        // 1. Actualizar Direcciones
        if (entidadNueva.getDirecciones() != null) {
            //No reemplazar la lista, modificar
            entidadExistente.getDirecciones().clear(); 
            entidadExistente.getDirecciones().addAll(entidadNueva.getDirecciones());
        }

        // 2. Actualizar Contactos 
        if (entidadNueva.getContactos() != null) {
            entidadExistente.getContactos().clear();
            entidadExistente.getContactos().addAll(entidadNueva.getContactos());
        }

        // 3. Actualizar Imágenes 
        if (entidadNueva.getImagenes() != null) {
            entidadExistente.getImagenes().clear();
            entidadExistente.getImagenes().addAll(entidadNueva.getImagenes());
        }
    }
}
