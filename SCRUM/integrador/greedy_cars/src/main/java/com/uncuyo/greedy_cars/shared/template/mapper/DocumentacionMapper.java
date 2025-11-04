package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.DocumentacionDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Documentacion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Documentacion y DocumentacionDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DocumentacionMapper extends BaseMapper<Documentacion, DocumentacionDTO, String> {
    
}
