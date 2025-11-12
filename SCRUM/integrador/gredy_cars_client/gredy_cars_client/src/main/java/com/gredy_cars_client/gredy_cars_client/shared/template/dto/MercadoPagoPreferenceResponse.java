package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoPreferenceResponse {

    private String initPoint;
    private String preferenceId;
    private Double monto;
}
