package com.gredy_cars_client.gredy_cars_client.shared.template.enums;

/**
 * Replica del enum de estados expuesto por el backend para facilitar la
 * serialización/deserialización de los vehículos.
 */
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
