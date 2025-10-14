package com.ejemplo.biblioteca.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LibroUpdateRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,
        @NotBlank(message = "La fecha es obligatoria")
        String fecha,
        @NotBlank(message = "El género es obligatorio")
        String genero,
        @NotNull(message = "Las páginas son obligatorias")
        @Min(value = 1, message = "Las páginas deben ser positivas")
        Integer paginas,
        @NotNull(message = "El autor es obligatorio")
        Long autorId,
        @NotNull(message = "La persona es obligatoria")
        Long personaId
) {
}
