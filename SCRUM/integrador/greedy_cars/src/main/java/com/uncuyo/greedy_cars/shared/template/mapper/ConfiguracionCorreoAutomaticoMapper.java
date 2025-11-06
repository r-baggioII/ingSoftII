package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.ConfiguracionCorreoAutomaticoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.ConfiguracionCorreoAutomatico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ConfiguracionCorreoAutomaticoMapper
        extends BaseMapper<ConfiguracionCorreoAutomatico, ConfiguracionCorreoAutomaticoDTO, String> {

    @Override
    @Mapping(source = "empresa.id", target = "empresaId")
    ConfiguracionCorreoAutomaticoDTO toDTO(ConfiguracionCorreoAutomatico entity);

    @Override
    @Mapping(target = "empresa", ignore = true)
    ConfiguracionCorreoAutomatico toEntity(ConfiguracionCorreoAutomaticoDTO dto);
}
