package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.PersonaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

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
public interface PersonaMapper extends BaseMapper<Persona, PersonaDTO, String> {
    
    @Mapping(source = "direcciones", target = "direccionIds", qualifiedByName = "direccionesToIds")
    PersonaDTO toDTO(Persona entity);
    
    @Mapping(source = "direccionIds", target = "direcciones", qualifiedByName = "idsToDirecciones")
    @Mapping(target = "contactos", ignore = true)
    Persona toEntity(PersonaDTO dto);
    
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
}
