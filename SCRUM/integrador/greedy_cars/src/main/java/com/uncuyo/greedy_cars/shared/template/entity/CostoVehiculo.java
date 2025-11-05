package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "costo_vehiculo")
public class CostoVehiculo extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotNull
    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @NotNull
    @Column(name = "fecha_hasta", nullable = false)
    private LocalDate fechaHasta;

    @NotNull
    @Column(name = "costo", nullable = false)
    private double costo;

    // Relación ManyToOne con CaracteristicaVehiculo
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caracteristica_vehiculo_id", nullable = false)
    private CaracteristicaVehiculo caracteristicaVehiculo;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    // Métodos de dominio
    public void crearCostoVehiculo(LocalDate fechaDesde, LocalDate fechaHasta, double costo, CaracteristicaVehiculo caracteristica) {
        validar(fechaDesde, fechaHasta, costo, caracteristica);
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.costo = costo;
        this.caracteristicaVehiculo = caracteristica;
        this.eliminado = false;
    }

    public void validar(LocalDate fechaDesde, LocalDate fechaHasta, double costo, CaracteristicaVehiculo caracteristica) {
        if (fechaDesde == null) throw new IllegalArgumentException("Debe indicar la fecha desde");
        if (fechaHasta == null) throw new IllegalArgumentException("Debe indicar la fecha hasta");
        if (fechaHasta.isBefore(fechaDesde)) throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
        if (costo < 0) throw new IllegalArgumentException("El costo no puede ser negativo");
        if (caracteristica == null) throw new IllegalArgumentException("Debe indicar la característica del vehículo");
    }

    public void modificarCostoVehiculo(LocalDate fechaDesde, LocalDate fechaHasta, double costo, CaracteristicaVehiculo caracteristica) {
        validar(fechaDesde, fechaHasta, costo, caracteristica);
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.costo = costo;
        this.caracteristicaVehiculo = caracteristica;
    }

    public void eliminarCostoVehiculo() {
        this.eliminado = true;
    }
}
