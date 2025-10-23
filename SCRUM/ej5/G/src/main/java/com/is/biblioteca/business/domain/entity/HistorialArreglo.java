package com.is.biblioteca.business.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
public class HistorialArreglo extends BaseEntity<String> {
    
    @Id
    private String id;
    
    @Temporal(TemporalType.DATE)
    private Date fechaArreglo;
    
    private String detalleArreglo;
    
    @ManyToOne
    private Vehiculo vehiculo;
    
    @ManyToOne
    private Mecanico mecanico;
    
    public HistorialArreglo() {
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public Date getFechaArreglo() {
        return fechaArreglo;
    }
    
    public void setFechaArreglo(Date fechaArreglo) {
        this.fechaArreglo = fechaArreglo;
    }
    
    public String getDetalleArreglo() {
        return detalleArreglo;
    }
    
    public void setDetalleArreglo(String detalleArreglo) {
        this.detalleArreglo = detalleArreglo;
    }
    
    public Vehiculo getVehiculo() {
        return vehiculo;
    }
    
    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }
    
    public Mecanico getMecanico() {
        return mecanico;
    }
    
    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }
}
