package com.minimarket.model;

import javax.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String denominacion;

    public Categoria() {}
    public Categoria(String denominacion) { this.denominacion = denominacion; }

    public Long getId() { return id; }
    public String getDenominacion() { return denominacion; }
    public void setDenominacion(String denominacion) { this.denominacion = denominacion; }
}
