package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.FormaDePagoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.FormaDePago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FormaDePagoMapper extends BaseMapper<FormaDePago, FormaDePagoDTO, String> {

    @Override
    @Mapping(source = "factura.id", target = "facturaId")
    FormaDePagoDTO toDTO(FormaDePago entity);

    @Override
    @Mapping(target = "factura", ignore = true)
    FormaDePago toEntity(FormaDePagoDTO dto);
}

