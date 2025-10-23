package com.is.biblioteca.business.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Vehiculo extends BaseEntity<String> {
    
    @Id
    private String id;
    private String patente;
    private String marca;
    private String modelo;
    
    @ManyToOne
    private Cliente cliente;
    
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialArreglo> historialArreglos = new ArrayList<>();
    
    public Vehiculo() {
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPatente() {
        return patente;
    }
    
    public void setPatente(String patente) {
        this.patente = patente;
    }
    
    public String getMarca() {
        return marca;
    }
    
    public void setMarca(String marca) {
        this.marca = marca;
    }
    
    public String getModelo() {
        return modelo;
    }
    
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public List<HistorialArreglo> getHistorialArreglos() {
        return historialArreglos;
    }
    
    public void setHistorialArreglos(List<HistorialArreglo> historialArreglos) {
        this.historialArreglos = historialArreglos;
    }
    
    public void addHistorialArreglo(HistorialArreglo historialArreglo) {
        historialArreglos.add(historialArreglo);
        historialArreglo.setVehiculo(this);
    }
    
    public void removeHistorialArreglo(HistorialArreglo historialArreglo) {
        historialArreglos.remove(historialArreglo);
        historialArreglo.setVehiculo(null);
    }
}
