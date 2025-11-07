package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para la entidad Alquiler utilizado por el cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AlquilerDTO extends BaseDTO<String> {

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o una fecha futura")
    private LocalDate fechaDesde;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaHasta;

    @NotNull(message = "El cliente es obligatorio")
    private String idCliente;

    @NotNull(message = "El vehículo es obligatorio")
    private String idVehiculo;

    private List<String> documentacionIds = new ArrayList<>();

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}