package com.is.biblioteca.business.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Imagen {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String nombre;
    private String mime;
    
    @Lob
    private byte[] contenido;
    
    public Imagen() {
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getMime() {
        return mime;
    }
    
    public void setMime(String mime) {
        this.mime = mime;
    }
    
    public byte[] getContenido() {
        return contenido;
    }
    
    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }
}
