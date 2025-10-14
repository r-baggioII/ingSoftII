package com.ejemplo.biblioteca.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LocalidadRequest(
        @NotBlank(message = "La denominación es obligatoria")
        String denominacion
) {
}
