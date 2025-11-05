package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.DetalleFacturaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.DetalleFactura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DetalleFacturaMapper extends BaseMapper<DetalleFactura, DetalleFacturaDTO, String> {

    @Override
    @Mapping(source = "alquiler.id", target = "alquilerId")
    @Mapping(source = "factura.id", target = "facturaId")
    DetalleFacturaDTO toDTO(DetalleFactura entity);

    @Override
    @Mapping(target = "alquiler", ignore = true)
    @Mapping(target = "factura", ignore = true)
    DetalleFactura toEntity(DetalleFacturaDTO dto);
}

