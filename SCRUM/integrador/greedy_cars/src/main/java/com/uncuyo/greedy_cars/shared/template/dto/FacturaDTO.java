package com.uncuyo.greedy_cars.shared.template.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoFactura;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
public class FacturaDTO extends BaseDTO<String> {

    @Positive(message = "El número de factura debe ser positivo")
    private Long numeroFactura;

    @NotNull(message = "La fecha de la factura es obligatoria")
    private LocalDate fechaFactura;

    @DecimalMin(value = "0.0", inclusive = true, message = "El total pagado no puede ser negativo")
    private Double totalPagado;

    private EstadoFactura estado = EstadoFactura.SIN_DEFINIR;

    @NotBlank(message = "Debe indicar el cliente asociado a la factura")
    private String clienteId;

    private String clienteNombreCompleto;

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
