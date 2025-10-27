package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.PersonaSummaryDTO;

final class DtoMapper {

    private DtoMapper() {
    }

    static AutorDTO toAutorDto(Autor autor) {
        return new AutorDTO(
                autor.getId(),
                autor.getNombre(),
                autor.getApellido(),
                autor.getBiografia()
        );
    }

    static PersonaSummaryDTO toPersonaSummaryDto(Persona persona) {
        return new PersonaSummaryDTO(
                persona.getId(),
                persona.getNombre(),
                persona.getApellido()
        );
    }
}
