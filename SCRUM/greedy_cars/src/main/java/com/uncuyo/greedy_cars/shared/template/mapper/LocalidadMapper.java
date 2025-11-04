package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.LocalidadDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Localidad;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Localidad y LocalidadDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {DepartamentoMapper.class}
)
public interface LocalidadMapper extends BaseMapper<Localidad, LocalidadDTO, Long> {
    
    // Los métodos toDTO, toEntity, toDTOList y toEntityList 
    // ya están definidos en BaseMapper y serán implementados por MapStruct
    // El mapper usa DepartamentoMapper para convertir automáticamente la relación con Departamento
}
