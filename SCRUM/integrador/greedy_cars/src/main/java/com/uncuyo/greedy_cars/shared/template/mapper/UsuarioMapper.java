package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.UsuarioDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre Usuario y UsuarioDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper extends BaseMapper<Usuario, UsuarioDTO, String> {

    /**
     * Al convertir entidad -> DTO, ignorar la clave (no copiarla).
     * La propiedad 'clave' seguirá aceptándose en DTO para requests (WRITE_ONLY).
     */
    @Mapping(target = "clave", ignore = true)
    UsuarioDTO toDTO(Usuario entity);

    // Al mapear de DTO -> entidad (por ejemplo para crear usuarios),
    // se permitirá mapear la clave (si viene en el DTO) a la entidad.
    Usuario toEntity(UsuarioDTO dto);
}
