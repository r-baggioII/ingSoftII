package com.ejemplo.biblioteca.web.dto;

import com.ejemplo.biblioteca.domain.TipoLibro;

public record LibroDTO(
        Long id,
        String titulo,
        String fecha,
        String genero,
        Integer paginas,
        TipoLibro tipo,
        Double pesoGramos,
        Double tamanoMb,
        AutorDTO autor,
        PersonaSummaryDTO persona
) {
}
