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
 * 
 * Nota: La relación ManyToMany con Persona se ignora en el DTO para evitar referencias circulares.
 * Los contactos se asocian a personas a través de los servicios de Persona/Cliente/Empleado.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ContactoCorreoElectronicoMapper extends BaseMapper<ContactoCorreoElectronico, ContactoCorreoElectronicoDTO, String> {
    
    // Ignoramos la relación personas (ManyToMany inversa) en el DTO
    @Mapping(target = "personaId", ignore = true)
    ContactoCorreoElectronicoDTO toDTO(ContactoCorreoElectronico entity);
    
    // Ignoramos la relación personas al crear la entidad desde el DTO
    @Mapping(target = "personas", ignore = true)
    ContactoCorreoElectronico toEntity(ContactoCorreoElectronicoDTO dto);
}
