package com.greedy_cars_institucional.institucional.shared.template.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CostoVehiculoDTO extends BaseDTO<String> {

    private static final long serialVersionUID = 1L;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private double costo;
    private String idCaracteristicaVehiculo;
    private CaracteristicaVehiculoDTO caracteristica;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
