package com.uncuyo.greedy_cars.shared.template.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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
public class PromocionDTO extends BaseDTO<String> {

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El porcentaje debe ser mayor a 0")
    @Max(value = 100, message = "El porcentaje no puede superar el 100")
    private Double porcentajeDescuento;

    @NotBlank(message = "El código de descuento es obligatorio")
    private String codigoDescuento;

    @JsonAlias("descripcion")
    private String descripcionDescuento;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonAlias("fechaInicio")
    private LocalDate fechaInicioPromocion;

    @NotNull(message = "La fecha de fin es obligatoria")
    @JsonAlias("fechaFin")
    private LocalDate fechaFinPromocion;

    private Boolean aplicaATodos = Boolean.TRUE;

    private Set<String> clientesDestinoIds = new HashSet<>();

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
