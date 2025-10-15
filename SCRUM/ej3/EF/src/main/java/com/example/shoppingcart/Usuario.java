package com.example.shoppingcart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Usuario {
    @Id
    private String id;
    private String nombre;
    private String clave;
    private boolean eliminado = false;

    @JsonManagedReference
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Carrito> carritos = new ArrayList<>();
}