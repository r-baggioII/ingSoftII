package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.PaisDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Pais;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Pais y PaisDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaisMapper extends BaseMapper<Pais, PaisDTO, Long> {
    
    // Los métodos toDTO, toEntity, toDTOList y toEntityList 
    // ya están definidos en BaseMapper y serán implementados por MapStruct
}
