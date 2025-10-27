package org.sistemaMecanico.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank; // Import para la validación
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true) // Esto es clave para que el ToString incluya los campos de Persona
@Entity
@Table(name = "mecanico")
public class Mecanico extends Persona {

    @NotBlank(message = "El legajo no puede estar vacío")
    @Column(name = "legajo", nullable = false, unique = true, length = 120)
    private String legajo;

    @ManyToMany(mappedBy = "mecanicos")
    private Set<HistorialArreglo> arreglos = new HashSet<>();

}