package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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
