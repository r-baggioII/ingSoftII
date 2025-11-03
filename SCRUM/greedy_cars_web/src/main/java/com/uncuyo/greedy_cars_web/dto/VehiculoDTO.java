package com.uncuyo.greedy_cars_web.dto;

import java.time.LocalDateTime;

/**
 * DTO para transferir datos de Vehículo entre frontend y backend
 * Debe coincidir con la estructura que envía la API
 */
public class VehiculoDTO {
    
    private String id;
    private String patente;
    private String estadoVehiculo; // DISPONIBLE, RESERVADO, VENDIDO, etc.
    
    // Campos de auditoría (si la API los envía)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Boolean eliminado;

    // Constructores
    public VehiculoDTO() {
    }

    public VehiculoDTO(String id, String patente, String estadoVehiculo) {
        this.id = id;
        this.patente = patente;
        this.estadoVehiculo = estadoVehiculo;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getEstadoVehiculo() {
        return estadoVehiculo;
    }

    public void setEstadoVehiculo(String estadoVehiculo) {
        this.estadoVehiculo = estadoVehiculo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    @Override
    public String toString() {
        return "VehiculoDTO{" +
                "id='" + id + '\'' +
                ", patente='" + patente + '\'' +
                ", estadoVehiculo='" + estadoVehiculo + '\'' +
                ", eliminado=" + eliminado +
                '}';
    }
}

