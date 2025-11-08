package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaracteristicaVehiculoMinDTO {
    private String id;
    private String marca;
    private String modelo;
    private int cantidadPuerta;
    private int cantidadAsiento;
    private long anio;
    private int cantidadTotalVehiculo;
    private int cantidadVehiculoAlquilado;
}
