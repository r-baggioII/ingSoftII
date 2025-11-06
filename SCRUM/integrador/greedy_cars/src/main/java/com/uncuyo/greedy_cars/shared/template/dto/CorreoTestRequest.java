package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorreoTestRequest {

    @NotBlank
    @Email
    private String destino;

    private String asunto;

    private String cuerpo;

    private String empresaId;
}
