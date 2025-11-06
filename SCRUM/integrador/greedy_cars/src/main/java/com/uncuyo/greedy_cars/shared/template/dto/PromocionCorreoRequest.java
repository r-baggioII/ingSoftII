package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromocionCorreoRequest {

    @NotBlank
    private String clienteId;

    @NotBlank
    private String codigo;

    @Min(1)
    private Integer porcentaje;

    private String empresaId;
}
