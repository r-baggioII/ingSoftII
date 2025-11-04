package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.ContactoTelefonicoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoTelefonico;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre ContactoTelefonico y ContactoTelefonicoDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ContactoTelefonicoMapper extends BaseMapper<ContactoTelefonico, ContactoTelefonicoDTO, String> {
    
    @Mapping(source = "persona.id", target = "personaId")
    ContactoTelefonicoDTO toDTO(ContactoTelefonico entity);
    
    @Mapping(target = "persona", expression = "java(mapPersonaId(dto.getPersonaId()))")
    ContactoTelefonico toEntity(ContactoTelefonicoDTO dto);
    
    default Persona mapPersonaId(String personaId) {
        if (personaId == null) {
            return null;
        }
        Persona persona = new Persona();
        persona.setId(personaId);
        return persona;
    }
}
