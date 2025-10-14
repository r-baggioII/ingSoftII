package com.ejemplo.biblioteca.web.dto;

public record LibroDTO(
        Long id,
        String titulo,
        String fecha,
        String genero,
        Integer paginas,
        AutorDTO autor,
        PersonaSummaryDTO persona,
        Long autorId,
        Long personaId,
        Boolean hasPdf
) {
}
