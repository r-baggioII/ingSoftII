package com.greedy_cars_institucional.institucional.shared.template.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiculoDTO {
    private String id;
    private String patente;
    private String estadoVehiculo;
    private CaracteristicaVehiculoMinDTO caracteristicaVehiculo;
    private boolean eliminado;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CaracteristicaVehiculoMinDTO {
        private String id;
        private String marca;
        private String modelo;
        private int anio;
        private int cantidadPuerta;
        private int cantidadAsiento;
        private int cantidadTotalVehiculo;
        private int cantidadVehiculoAlquilado;
        private boolean eliminado;
    }
}