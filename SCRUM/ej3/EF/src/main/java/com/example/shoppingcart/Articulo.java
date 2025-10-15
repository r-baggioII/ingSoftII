package com.example.shoppingcart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Articulo {
    @Id
    private String id;
    private String nombre;
    private double precio;
    private boolean eliminado = false;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Imagen> imagenes = new ArrayList<>();

    @JsonIgnoreProperties({"articulos", "eliminado"})
    @ManyToOne(fetch = FetchType.EAGER)
    private Proveedor proveedor;
}