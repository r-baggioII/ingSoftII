package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConfiguracionCorreoAutomaticoDTO extends BaseDTO<String> {

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String clave;

    @NotBlank
    private String puerto;

    @NotBlank
    private String smtp;

    @NotNull
    private Boolean tls;

    @NotBlank
    private String empresaId;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
