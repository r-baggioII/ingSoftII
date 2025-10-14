package com.ejemplo.biblioteca.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AutorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,
        @NotBlank(message = "La biografía es obligatoria")
        String biografia
) {
}
