package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PromocionDTO extends BaseDTO<String> {

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @NotNull(message = "Debe indicar el porcentaje de descuento")
    @DecimalMin(value = "0.0", inclusive = false, message = "El porcentaje debe ser mayor a 0")
    @Max(value = 100, message = "El porcentaje no puede superar el 100%")
    private Double porcentajeDescuento;

    @NotBlank(message = "Debe indicar el código de la promoción")
    private String codigoDescuento;

    private String descripcionDescuento;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicioPromocion;

    @NotNull(message = "La fecha de fin es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFinPromocion;

    private Boolean aplicaATodos = Boolean.TRUE;

    private Set<String> clientesDestinoIds = new HashSet<>();
}
