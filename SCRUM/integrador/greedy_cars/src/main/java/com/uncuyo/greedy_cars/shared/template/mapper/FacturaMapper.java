package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.FacturaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {DetalleFacturaMapper.class, FormaDePagoMapper.class}
)
public interface FacturaMapper extends BaseMapper<Factura, FacturaDTO, String> {

    @Override
    @Mapping(source = "detalles", target = "detalles")
    @Mapping(source = "formasPago", target = "formasPago")
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "clienteNombreCompleto", ignore = true)
    FacturaDTO toDTO(Factura entity);

    @Override
    @Mapping(target = "detalles", ignore = true)
    @Mapping(target = "formasPago", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    Factura toEntity(FacturaDTO dto);
}
