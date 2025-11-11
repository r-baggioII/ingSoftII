package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Cliente extends Persona {

    @Column(name = "direccion_estadia", length = 500)
    private String direccionEstadia;

    // Un cliente puede tener múltiples nacionalidades
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "cliente_nacionalidad",
        joinColumns = @JoinColumn(name = "cliente_id"),
        inverseJoinColumns = @JoinColumn(name = "nacionalidad_id")
    )
    private List<Nacionalidad> nacionalidades = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
    
    // Métodos de conveniencia para nacionalidades
    public void addNacionalidad(Nacionalidad nacionalidad) {
        nacionalidades.add(nacionalidad);
    }

    public void removeNacionalidad(Nacionalidad nacionalidad) {
        nacionalidades.remove(nacionalidad);
    }

}
