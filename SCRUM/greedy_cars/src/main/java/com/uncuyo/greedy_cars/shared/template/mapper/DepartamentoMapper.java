package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.DepartamentoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Departamento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Departamento y DepartamentoDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {ProvinciaMapper.class}
)
public interface DepartamentoMapper extends BaseMapper<Departamento, DepartamentoDTO, Long> {
    
    // Los métodos toDTO, toEntity, toDTOList y toEntityList 
    // ya están definidos en BaseMapper y serán implementados por MapStruct
    // El mapper usa ProvinciaMapper para convertir automáticamente la relación con Provincia
}
