package com.ejemplo.biblioteca.web.dto.mapper;

import com.ejemplo.biblioteca.domain.Domicilio;
import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.web.dto.DomicilioDTO;
import com.ejemplo.biblioteca.web.dto.DomicilioRequest;
import com.ejemplo.biblioteca.web.dto.LocalidadDTO;
import com.ejemplo.biblioteca.web.dto.PersonaDTO;
import com.ejemplo.biblioteca.web.dto.PersonaRequest;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public Persona toEntity(PersonaRequest request) {
        return toEntity(request, null);
    }

    public Persona toEntity(PersonaRequest request, Long id) {
        Persona persona = new Persona();
        persona.setId(id);
        persona.setNombre(request.nombre());
        persona.setApellido(request.apellido());
        persona.setDni(request.dni());
        persona.setDomicilio(toDomicilio(request.domicilio()));
        return persona;
    }

    public PersonaDTO toDto(Persona persona) {
        Domicilio domicilio = persona.getDomicilio();
        Localidad localidad = domicilio.getLocalidad();
        LocalidadDTO localidadDTO = new LocalidadDTO(localidad.getId(), localidad.getDenominacion());
        DomicilioDTO domicilioDTO = new DomicilioDTO(
                domicilio.getId(),
                domicilio.getCalle(),
                domicilio.getNumero(),
                localidadDTO
        );
        return new PersonaDTO(
                persona.getId(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getDni(),
                domicilioDTO
        );
    }

    private Domicilio toDomicilio(DomicilioRequest request) {
        Domicilio domicilio = new Domicilio();
        domicilio.setCalle(request.calle());
        domicilio.setNumero(request.numero());
        Localidad localidad = new Localidad();
        localidad.setId(request.localidadId());
        domicilio.setLocalidad(localidad);
        return domicilio;
    }
}
