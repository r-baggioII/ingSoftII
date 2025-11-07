package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.EstadoFactura;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO cliente para las facturas emitidas por Greedy Cars.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FacturaDTO extends BaseDTO<String> {

    @Positive(message = "El número de factura debe ser positivo")
    private Long numeroFactura;

    @NotNull(message = "La fecha de factura es obligatoria")
    private LocalDate fechaFactura;

    @DecimalMin(value = "0.0", inclusive = true, message = "El total no puede ser negativo")
    private Double totalPagado;

    private EstadoFactura estado = EstadoFactura.SIN_DEFINIR;

    @Valid
    @NotNull(message = "Debe indicar al menos un detalle")
    private List<DetalleFacturaDTO> detalles = new ArrayList<>();

    @Valid
    @NotNull(message = "Debe indicar al menos una forma de pago")
    @JsonProperty("formasDePago")
    @JsonAlias("formasPago")
    private List<FormaDePagoDTO> formasPago = new ArrayList<>();

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
