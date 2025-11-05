package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "alquiler")
public class Alquiler extends BaseEntity<String> {

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

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @OneToMany
    @JoinTable(
        name = "alquiler_documentacion",
        joinColumns = @JoinColumn(name = "alquiler_id"),
        inverseJoinColumns = @JoinColumn(name = "documentacion_id")
    )
    private List<Documentacion> documentaciones = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    // Métodos de dominio simples que no dependen de repositorios
    public void crearAlquiler(LocalDate fechaDesde, LocalDate fechaHasta, Cliente cliente, Vehiculo vehiculo) {
        validar(fechaDesde, fechaHasta, cliente, vehiculo);
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.eliminado = false;
    }

    public void validar(LocalDate fechaDesde, LocalDate fechaHasta, Cliente cliente, Vehiculo vehiculo) {
        if (fechaDesde == null) {
            throw new IllegalArgumentException("Debe indicar la fecha desde");
        }
        if (fechaHasta == null) {
            throw new IllegalArgumentException("Debe indicar la fecha hasta");
        }
        if (fechaHasta.isBefore(fechaDesde)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("Debe indicar el cliente");
        }
        if (vehiculo == null) {
            throw new IllegalArgumentException("Debe indicar el vehiculo");
        }
    }

    public void modificarAlquiler(LocalDate fechaDesde, LocalDate fechaHasta, Cliente cliente, Vehiculo vehiculo) {
        validar(fechaDesde, fechaHasta, cliente, vehiculo);
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
    }

    public void eliminarAlquiler() {
        this.eliminado = true;
    }
}
