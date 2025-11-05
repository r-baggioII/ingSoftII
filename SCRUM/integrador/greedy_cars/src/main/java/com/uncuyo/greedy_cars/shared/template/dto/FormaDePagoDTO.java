package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoPago;
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
public class FormaDePagoDTO extends BaseDTO<String> {

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    @Size(max = 500, message = "La observación no puede exceder los 500 caracteres")
    private String observacion;

    private String facturaId;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
