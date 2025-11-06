package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoPreferenceRequest {

    private String facturaId;

    private String vehiculoId;

    @Min(value = 1, message = "La cantidad de días debe ser al menos 1")
    private Integer cantidadDias;
}
