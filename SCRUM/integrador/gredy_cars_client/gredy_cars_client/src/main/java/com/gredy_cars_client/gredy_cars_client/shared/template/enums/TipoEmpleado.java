package com.gredy_cars_client.gredy_cars_client.shared.template.enums;

/**
 * Enum para tipos de empleado.
 */
public enum TipoEmpleado {
    ADMINISTRATIVO("Administrativo"),
    JEFE("Jefe");

    private final String descripcion;

    TipoEmpleado(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
