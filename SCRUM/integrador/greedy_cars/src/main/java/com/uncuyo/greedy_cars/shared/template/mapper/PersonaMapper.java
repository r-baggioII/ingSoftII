package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.PersonaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre Persona y PersonaDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PersonaMapper implements BaseMapper<Persona, PersonaDTO, String> {
    
    @Autowired
    protected DireccionRepository direccionRepository;
    
    @Autowired
    protected ContactoRepository contactoRepository;
    
    @Autowired
    protected ImagenRepository imagenRepository;
    
    @Mapping(source = "direcciones", target = "direccionIds", qualifiedByName = "direccionesToIds")
    @Mapping(source = "contactos", target = "contactoIds", qualifiedByName = "contactosToIds")
    @Mapping(source = "imagenes", target = "imagenIds", qualifiedByName = "imagenesToIds")
    public abstract PersonaDTO toDTO(Persona entity);
    
    @Mapping(source = "direccionIds", target = "direcciones", qualifiedByName = "idsToDirecciones")
    @Mapping(source = "contactoIds", target = "contactos", qualifiedByName = "idsToContactos")
    @Mapping(source = "imagenIds", target = "imagenes", qualifiedByName = "idsToImagenes")
    public abstract Persona toEntity(PersonaDTO dto);
    
    // ==================== DIRECCIONES ====================
    
    @Named("direccionesToIds")
    protected List<Long> direccionesToIds(List<Direccion> direcciones) {
        if (direcciones == null) {
            return new ArrayList<>();
        }
        return direcciones.stream()
                .map(Direccion::getId)
                .collect(Collectors.toList());
    }
    
    @Named("idsToDirecciones")
    protected List<Direccion> idsToDirecciones(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        // Fetch actual Direccion entities from database instead of creating empty ones
        return direccionRepository.findAllById(ids);
    }
    
    // ==================== CONTACTOS ====================
    
    @Named("contactosToIds")
    protected List<String> contactosToIds(List<Contacto> contactos) {
        if (contactos == null) {
            return new ArrayList<>();
        }
        return contactos.stream()
                .map(Contacto::getId)
                .collect(Collectors.toList());
    }
    
    @Named("idsToContactos")
    protected List<Contacto> idsToContactos(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        // Fetch existing Contacto entities from database
        List<Contacto> contactos = contactoRepository.findAllById(ids);
        // Set the bidirectional relationship - will be completed in service
        return contactos;
    }
    
    // ==================== IMAGENES ====================
    
    @Named("imagenesToIds")
    protected List<String> imagenesToIds(List<Imagen> imagenes) {
        if (imagenes == null) {
            return new ArrayList<>();
        }
        return imagenes.stream()
                .map(Imagen::getId)
                .collect(Collectors.toList());
    }
    
    @Named("idsToImagenes")
    protected List<Imagen> idsToImagenes(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        // Fetch existing Imagen entities from database
        return imagenRepository.findAllById(ids);
    }
}
