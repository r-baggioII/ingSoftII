package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.EmpleadoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Empleado;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmpleadoMapper extends BaseMapper<Empleado, EmpleadoDTO, String> {

    // MapStruct will provide implementations for toDTO, toEntity, etc.

}
