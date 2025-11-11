package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.EmpleadoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Empleado;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre Empleado y EmpleadoDTO.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EmpleadoMapper implements BaseMapper<Empleado, EmpleadoDTO, String> {

    @Autowired
    protected DireccionRepository direccionRepository;
    
    @Autowired
    protected ContactoRepository contactoRepository;
    
    @Autowired
    protected ImagenRepository imagenRepository;

    @Mapping(source = "direcciones", target = "direccionIds", qualifiedByName = "direccionesToIds")
    @Mapping(source = "contactos", target = "contactoIds", qualifiedByName = "contactosToIds")
    @Mapping(source = "imagenes", target = "imagenIds", qualifiedByName = "imagenesToIds")
    public abstract EmpleadoDTO toDTO(Empleado entity);

    @Mapping(source = "direccionIds", target = "direcciones", qualifiedByName = "idsToDirecciones")
    @Mapping(source = "contactoIds", target = "contactos", qualifiedByName = "idsToContactos")
    @Mapping(source = "imagenIds", target = "imagenes", qualifiedByName = "idsToImagenes")
    @Mapping(target = "id", ignore = true)
    public abstract Empleado toEntity(EmpleadoDTO dto);

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
        // Create transient Direccion objects with just the ID
        // The preAlta method will load the managed entities
        return ids.stream()
                .map(id -> {
                    Direccion dir = new Direccion();
                    dir.setId(id);
                    return dir;
                })
                .collect(Collectors.toList());
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
        return contactoRepository.findAllById(ids);
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
        return imagenRepository.findAllById(ids);
    }
}
