package com.example.shoppingcart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter @Setter
public class Detalle {
    @Id
    private String id;
    private int cantidad = 1;
    private boolean eliminado = false;

    @JsonBackReference
    @ManyToOne
    private Carrito carrito;

    @ManyToOne
    private Articulo articulo;
}