package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.EmpresaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Empresa;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper para conversión entre Empresa y EmpresaDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EmpresaMapper implements BaseMapper<Empresa, EmpresaDTO, String> {
    
    @Autowired
    protected DireccionRepository direccionRepository;
    
    @Autowired
    protected ContactoRepository contactoRepository;
    
    @Mapping(source = "direcciones", target = "direccionIds", qualifiedByName = "direccionesToIds")
    @Mapping(source = "contactos", target = "contactoIds", qualifiedByName = "contactosToIds")
    public abstract EmpresaDTO toDTO(Empresa entity);
    
    @Mapping(source = "direccionIds", target = "direcciones", qualifiedByName = "idsToDirecciones")
    @Mapping(source = "contactoIds", target = "contactos", qualifiedByName = "idsToContactos")
    @Mapping(target = "configuracionesCorreo", ignore = true)
    public abstract Empresa toEntity(EmpresaDTO dto);
    
    @Named("direccionesToIds")
    protected List<Long> direccionesToIds(List<Direccion> direcciones) {
        if (direcciones == null || direcciones.isEmpty()) {
            return new ArrayList<>();
        }
        return direcciones.stream()
                .map(Direccion::getId)
                .collect(Collectors.toList());
    }
    
    @Named("contactosToIds")
    protected List<String> contactosToIds(List<Contacto> contactos) {
        if (contactos == null || contactos.isEmpty()) {
            return new ArrayList<>();
        }
        return contactos.stream()
                .map(Contacto::getId)
                .collect(Collectors.toList());
    }
    
    @Named("idsToDirecciones")
    protected List<Direccion> idsToDirecciones(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(id -> direccionRepository.findById(id).orElse(null))
                .filter(d -> d != null)
                .collect(Collectors.toList());
    }
    
    @Named("idsToContactos")
    protected List<Contacto> idsToContactos(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(id -> contactoRepository.findById(id).orElse(null))
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }
}
