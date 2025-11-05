package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.ImagenDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Imagen y ImagenDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ImagenMapper extends BaseMapper<Imagen, ImagenDTO, String> {
    
    // Los métodos de mapeo se heredan de BaseMapper
    // No se requieren mappings personalizados ya que los campos coinciden directamente
}
