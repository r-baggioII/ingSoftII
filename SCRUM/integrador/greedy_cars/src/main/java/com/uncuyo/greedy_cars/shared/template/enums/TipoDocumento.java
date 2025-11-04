package com.uncuyo.greedy_cars.shared.template.enums;

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
