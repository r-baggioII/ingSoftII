package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.DireccionDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Direccion y DireccionDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {LocalidadMapper.class}
)
public interface DireccionMapper extends BaseMapper<Direccion, DireccionDTO, Long> {
    
    // Los métodos toDTO, toEntity, toDTOList y toEntityList 
    // ya están definidos en BaseMapper y serán implementados por MapStruct
    // El mapper usa LocalidadMapper para convertir automáticamente la relación con Localidad
}
