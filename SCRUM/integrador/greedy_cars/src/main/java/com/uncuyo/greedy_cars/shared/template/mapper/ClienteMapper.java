package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.ClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.entity.Nacionalidad;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;
import com.uncuyo.greedy_cars.shared.template.repository.NacionalidadRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre Cliente y ClienteDTO.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ClienteMapper implements BaseMapper<Cliente, ClienteDTO, String> {

    @Autowired
    protected DireccionRepository direccionRepository;
    
    @Autowired
    protected ContactoRepository contactoRepository;
    
    @Autowired
    protected ImagenRepository imagenRepository;
    
    @Autowired
    protected NacionalidadRepository nacionalidadRepository;

    @Mapping(source = "direcciones", target = "direccionIds", qualifiedByName = "direccionesToIds")
    @Mapping(source = "contactos", target = "contactoIds", qualifiedByName = "contactosToIds")
    @Mapping(source = "imagenes", target = "imagenIds", qualifiedByName = "imagenesToIds")
    @Mapping(source = "nacionalidades", target = "nacionalidadIds", qualifiedByName = "nacionalidadesToIds")
    public abstract ClienteDTO toDTO(Cliente entity);

    @Mapping(source = "direccionIds", target = "direcciones", qualifiedByName = "idsToDirecciones")
    @Mapping(source = "contactoIds", target = "contactos", qualifiedByName = "idsToContactos")
    @Mapping(source = "imagenIds", target = "imagenes", qualifiedByName = "idsToImagenes")
    @Mapping(source = "nacionalidadIds", target = "nacionalidades", qualifiedByName = "idsToNacionalidades")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)  // El ID se genera automáticamente, no lo mapeamos del DTO
    public abstract Cliente toEntity(ClienteDTO dto);

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
    
    // ==================== NACIONALIDADES ====================
    
    @Named("nacionalidadesToIds")
    protected List<String> nacionalidadesToIds(List<Nacionalidad> nacionalidades) {
        if (nacionalidades == null) {
            return new ArrayList<>();
        }
        return nacionalidades.stream()
                .map(Nacionalidad::getId)
                .collect(Collectors.toList());
    }
    
    @Named("idsToNacionalidades")
    protected List<Nacionalidad> idsToNacionalidades(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return nacionalidadRepository.findAllById(ids);
    }
}
