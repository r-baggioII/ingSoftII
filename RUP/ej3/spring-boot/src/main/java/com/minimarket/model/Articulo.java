package com.minimarket.model;

import javax.persistence.*;

@Entity
@Table(name = "articulos")
public class Articulo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String denominacion;

    @Column(nullable = false)
    private int cantidad;  // stock

    @Column(nullable = false)
    private int precio;    // entero (según enunciado)

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public Articulo() {}
    public Articulo(String denominacion, int cantidad, int precio, Categoria categoria) {
        this.denominacion = denominacion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public String getDenominacion() { return denominacion; }
    public void setDenominacion(String denominacion) { this.denominacion = denominacion; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
