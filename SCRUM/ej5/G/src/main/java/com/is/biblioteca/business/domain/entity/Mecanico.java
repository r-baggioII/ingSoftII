package com.is.biblioteca.business.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Mecanico extends Persona {
    
    private String legajo;
    
    @ManyToOne
    private Usuario usuario;
    
    public Mecanico() {
        super();
    }
    
    public String getLegajo() {
        return legajo;
    }
    
    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
