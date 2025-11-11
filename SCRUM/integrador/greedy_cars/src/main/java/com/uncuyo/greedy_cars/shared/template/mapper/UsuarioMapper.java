package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.UsuarioDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.repository.PersonaRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper para conversión entre Usuario y UsuarioDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UsuarioMapper implements BaseMapper<Usuario, UsuarioDTO, String> {

    @Autowired
    protected PersonaRepository personaRepository;

    /**
     * Al convertir entidad -> DTO, ignorar la clave (no copiarla).
     * La propiedad 'clave' seguirá aceptándose en DTO para requests (WRITE_ONLY).
     */
    @Mapping(target = "clave", ignore = true)
    @Mapping(source = "persona.id", target = "personaId")
    public abstract UsuarioDTO toDTO(Usuario entity);

    // Al mapear de DTO -> entidad (por ejemplo para crear usuarios),
    // se permitirá mapear la clave (si viene en el DTO) a la entidad.
    @Mapping(source = "personaId", target = "persona", qualifiedByName = "idToPersona")
    public abstract Usuario toEntity(UsuarioDTO dto);

    @Named("idToPersona")
    protected Persona idToPersona(String personaId) {
        if (personaId == null || personaId.trim().isEmpty()) {
            return null;
        }
        return personaRepository.findById(personaId).orElse(null);
    }
}
