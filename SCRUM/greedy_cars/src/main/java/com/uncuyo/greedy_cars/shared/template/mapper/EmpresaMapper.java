package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.EmpresaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre Empresa y EmpresaDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmpresaMapper extends BaseMapper<Empresa, EmpresaDTO, String> {
    
    @Mapping(source = "direcciones", target = "direccionIds", qualifiedByName = "direccionesToIds")
    @Mapping(source = "contactos", target = "contactoIds", qualifiedByName = "contactosToIds")
    EmpresaDTO toDTO(Empresa entity);
    
    @Mapping(source = "direccionIds", target = "direcciones", qualifiedByName = "idsToDirecciones")
    @Mapping(source = "contactoIds", target = "contactos", qualifiedByName = "idsToContactos")
    Empresa toEntity(EmpresaDTO dto);
    
    @Named("direccionesToIds")
    default List<Long> direccionesToIds(List<Direccion> direcciones) {
        if (direcciones == null) {
            return new ArrayList<>();
        }
        return direcciones.stream()
                .map(Direccion::getId)
                .collect(Collectors.toList());
    }
    
    @Named("idsToDirecciones")
    default List<Direccion> idsToDirecciones(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(id -> {
                    Direccion direccion = new Direccion();
                    direccion.setId(id);
                    return direccion;
                })
                .collect(Collectors.toList());
    }
    
    @Named("contactosToIds")
    default List<String> contactosToIds(List<Contacto> contactos) {
        if (contactos == null) {
            return new ArrayList<>();
        }
        return contactos.stream()
                .map(Contacto::getId)
                .collect(Collectors.toList());
    }
    
    @Named("idsToContactos")
    default List<Contacto> idsToContactos(List<String> ids) {
        // Los contactos se manejarán en el servicio, no aquí
        return new ArrayList<>();
    }
}
