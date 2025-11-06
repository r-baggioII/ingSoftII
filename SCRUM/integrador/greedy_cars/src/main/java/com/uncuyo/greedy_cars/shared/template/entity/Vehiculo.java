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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caracteristica_vehiculo_id")
    private CaracteristicaVehiculo caracteristicaVehiculo;

    // Implementación explícita de métodos abstractos de BaseEntity
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
