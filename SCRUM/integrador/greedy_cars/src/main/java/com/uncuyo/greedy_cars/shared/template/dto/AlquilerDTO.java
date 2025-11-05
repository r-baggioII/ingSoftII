package com.uncuyo.greedy_cars.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AlquilerDTO extends BaseDTO<String> {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // Referencias por ID para simplificar el contrato del API
    private String idCliente;
    private String idVehiculo;

    // Documentación asociada al alquiler
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
