package com.example.shoppingcart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter @Setter
public class Imagen {
    @Id
    private String id;
    private String nombre;
    private String mime;
    private byte[] contenido;
    private boolean eliminado = false;

    @JsonBackReference
    @ManyToOne
    private Articulo articulo;
}