package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.ClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClienteMapper extends BaseMapper<Cliente, ClienteDTO, String> {

    @Mapping(source = "nacionalidad.id", target = "nacionalidadId")
    ClienteDTO toDTO(Cliente entity);

    @Mapping(source = "nacionalidadId", target = "nacionalidad.id")
    Cliente toEntity(ClienteDTO dto);

}
