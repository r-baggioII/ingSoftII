package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.ProvinciaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Provincia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Provincia y ProvinciaDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {PaisMapper.class}
)
public interface ProvinciaMapper extends BaseMapper<Provincia, ProvinciaDTO, Long> {
    
    // Los métodos toDTO, toEntity, toDTOList y toEntityList 
    // ya están definidos en BaseMapper y serán implementados por MapStruct
    // El mapper usa PaisMapper para convertir automáticamente la relación con Pais
}
