package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO cliente para el detalle de líneas dentro de una factura.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DetalleFacturaDTO extends BaseDTO<String> {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El subtotal no puede ser negativo")
    private Double subtotal;

    @NotBlank(message = "Debe indicar el alquiler vinculado")
    private String alquilerId;

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
