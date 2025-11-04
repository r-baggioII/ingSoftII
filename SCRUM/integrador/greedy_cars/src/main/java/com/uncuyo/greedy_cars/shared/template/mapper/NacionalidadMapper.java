package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.NacionalidadDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Nacionalidad;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NacionalidadMapper extends BaseMapper<Nacionalidad, NacionalidadDTO, String> {

}
