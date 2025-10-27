package org.sistemaMecanico.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true) // Esto es clave para que el ToString incluya los campos de Persona
@Entity
@Table(name = "cliente")
public class Cliente extends Persona {

    @Column(name = "documento", nullable = false, unique = true, length = 120)
    private String documento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Vehiculo> vehiculos = new ArrayList<>();

}