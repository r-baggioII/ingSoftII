package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordatorioDevolucionRequest {

    @NotBlank
    private String alquilerId;

    private String empresaId;
}
