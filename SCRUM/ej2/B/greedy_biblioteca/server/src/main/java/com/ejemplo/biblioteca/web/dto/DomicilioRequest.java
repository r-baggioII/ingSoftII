package com.ejemplo.biblioteca.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DomicilioRequest(
        @NotBlank(message = "La calle es obligatoria")
        String calle,
        @NotNull(message = "El número es obligatorio")
        @Min(value = 1, message = "El número debe ser positivo")
        Integer numero,
        @NotNull(message = "La localidad es obligatoria")
        Long localidadId
) {
}
