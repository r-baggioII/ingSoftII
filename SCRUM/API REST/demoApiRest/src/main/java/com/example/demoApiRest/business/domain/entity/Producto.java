package com.example.demoApiRest.business.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Producto {

	@Id
    private String id;
    private String nombre;
    private double precio;
    @ManyToOne
    private Categoria categoria;
    @ManyToOne
    private Marca marca;
    private boolean eliminado;
    
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
	public Marca getMarca() {
		return marca;
	}
	public void setMarca(Marca marca) {
		this.marca = marca;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public boolean isEliminado() {
		return eliminado;
	}
	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	
	
    
}
