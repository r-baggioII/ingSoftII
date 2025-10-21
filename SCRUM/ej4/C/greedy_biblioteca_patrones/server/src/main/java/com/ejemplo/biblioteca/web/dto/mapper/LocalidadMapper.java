package com.ejemplo.biblioteca.web.dto.mapper;

import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.web.dto.LocalidadDTO;
import com.ejemplo.biblioteca.web.dto.LocalidadRequest;
import org.springframework.stereotype.Component;

@Component
public class LocalidadMapper {

    public Localidad toEntity(LocalidadRequest request) {
        return toEntity(request, null);
    }

    public Localidad toEntity(LocalidadRequest request, Long id) {
        Localidad localidad = new Localidad();
        localidad.setId(id);
        localidad.setDenominacion(request.denominacion());
        return localidad;
    }

    public LocalidadDTO toDto(Localidad localidad) {
        return new LocalidadDTO(localidad.getId(), localidad.getDenominacion());
    }
}
