package com.uncuyo.greedy_cars.shared.template.enums;

public enum EstadoVehiculo {
    DISPONIBLE("Disponible"),
    ALQUILADO("Alquilado");

    private final String descripcion;

    EstadoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
