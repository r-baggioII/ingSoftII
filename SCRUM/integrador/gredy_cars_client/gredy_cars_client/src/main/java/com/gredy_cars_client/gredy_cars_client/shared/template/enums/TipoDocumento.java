package com.gredy_cars_client.gredy_cars_client.shared.template.enums;

/**
 * Enum para tipos de documento de identidad.
 */
public enum TipoDocumento {
    DNI("DNI"),
    PASAPORTE("Pasaporte"),
    CEDULA("Cédula"),
    LICENCIA("Licencia de Conducir"),
    OTRO("Otro");

    private final String descripcion;

    TipoDocumento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
