package com.ejemplo.biblioteca.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,
        @NotNull(message = "El DNI es obligatorio")
        @Min(value = 1, message = "El DNI debe ser positivo")
        Integer dni,
        @Valid
        DomicilioRequest domicilio
) {
}
