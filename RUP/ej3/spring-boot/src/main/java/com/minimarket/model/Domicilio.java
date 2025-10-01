package com.minimarket.model;

import javax.persistence.*;

@Entity
@Table(name = "domicilios")
public class Domicilio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombreCalle;

    @Column(nullable = false)
    private int numero;

    public Domicilio() {}
    public Domicilio(String nombreCalle, int numero) {
        this.nombreCalle = nombreCalle; this.numero = numero;
    }

    public Long getId() { return id; }
    public String getNombreCalle() { return nombreCalle; }
    public void setNombreCalle(String nombreCalle) { this.nombreCalle = nombreCalle; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
}
