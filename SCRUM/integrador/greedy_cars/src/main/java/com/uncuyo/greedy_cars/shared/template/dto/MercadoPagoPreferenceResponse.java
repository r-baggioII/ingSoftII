package com.uncuyo.greedy_cars.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MercadoPagoPreferenceResponse {

    private final String initPoint;
    private final String preferenceId;
    private final Double monto;
}
