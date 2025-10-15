package com.example.shoppingcart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Carrito {
    @Id
    private String id;
    private double total;
    private boolean eliminado = false;

    @JsonManagedReference
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Detalle> detalles = new ArrayList<>();
    
    @ManyToOne
    private Usuario usuario;
}