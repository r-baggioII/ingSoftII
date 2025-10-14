package com.example.shoppingcart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Proveedor {
    @Id
    private String id;
    private String nombre;
    private String direccion;
    private double latitud;
    private double longitud;
    private boolean eliminado = false;

    @OneToMany(mappedBy = "proveedor")
    private List<Articulo> articulos = new ArrayList<>();
}