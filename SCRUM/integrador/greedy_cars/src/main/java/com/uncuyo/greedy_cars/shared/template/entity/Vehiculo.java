package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.EstadoVehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "vehiculo")
public class Vehiculo extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;


    @Size(max = 20)
    @Column(name = "patente", nullable = false, unique = true, length = 20)
    private String patente;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_vehiculo", nullable = false, length = 20)
    private EstadoVehiculo estadoVehiculo;

    // --- MÉTODOS HEREDADOS DE BaseEntity ---
    // Lombok se encarga de generar los métodos getId() y setId(String id)
    // que satisfacen los requerimientos abstractos de BaseEntity<String>.
}