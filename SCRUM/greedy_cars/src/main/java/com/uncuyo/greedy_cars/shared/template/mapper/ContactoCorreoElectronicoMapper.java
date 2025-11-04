package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoCorreoElectronico;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversión entre ContactoCorreoElectronico y ContactoCorreoElectronicoDTO.
 * Extiende de BaseMapper para heredar los métodos comunes de conversión.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ContactoCorreoElectronicoMapper extends BaseMapper<ContactoCorreoElectronico, ContactoCorreoElectronicoDTO, String> {
    
    @Mapping(source = "persona.id", target = "personaId")
    ContactoCorreoElectronicoDTO toDTO(ContactoCorreoElectronico entity);
    
    @Mapping(target = "persona", expression = "java(mapPersonaId(dto.getPersonaId()))")
    ContactoCorreoElectronico toEntity(ContactoCorreoElectronicoDTO dto);
    
    default Persona mapPersonaId(String personaId) {
        if (personaId == null) {
            return null;
        }
        Persona persona = new Persona();
        persona.setId(personaId);
        return persona;
    }
}
