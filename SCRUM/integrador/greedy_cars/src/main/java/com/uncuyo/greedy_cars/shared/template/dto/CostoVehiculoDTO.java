package com.uncuyo.greedy_cars.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import com.uncuyo.greedy_cars.shared.template.dto.CaracteristicaVehiculoMinDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CostoVehiculoDTO extends BaseDTO<String> {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private double costo;

    // Referencia a la caracteristica
    private String idCaracteristicaVehiculo;

    // Minimal nested caracteristica info to avoid full entity serialization
    private CaracteristicaVehiculoMinDTO caracteristica;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
