package com.ejemplo.biblioteca.web.dto;

public record PersonaDTO(
        Long id,
        String nombre,
        String apellido,
        Integer dni,
        DomicilioDTO domicilio
) {
}
