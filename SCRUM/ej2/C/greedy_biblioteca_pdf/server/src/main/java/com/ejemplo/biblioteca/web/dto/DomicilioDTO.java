package com.ejemplo.biblioteca.web.dto;

public record DomicilioDTO(
        Long id,
        String calle,
        Integer numero,
        LocalidadDTO localidad
) {
}
