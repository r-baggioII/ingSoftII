package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.PromocionDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.Promocion;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PromocionMapper extends BaseMapper<Promocion, PromocionDTO, String> {

    @Override
    @Mapping(target = "clientesDestinoIds", expression = "java(mapClientesDestinoIds(entity.getClientesDestino()))")
    PromocionDTO toDTO(Promocion entity);

    @Override
    @Mapping(target = "clientesDestino", ignore = true)
    Promocion toEntity(PromocionDTO dto);

    default Set<String> mapClientesDestinoIds(Set<Cliente> clientesDestino) {
        if (clientesDestino == null || clientesDestino.isEmpty()) {
            return new HashSet<>();
        }
        return clientesDestino.stream()
                .map(Cliente::getId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());
    }
}
