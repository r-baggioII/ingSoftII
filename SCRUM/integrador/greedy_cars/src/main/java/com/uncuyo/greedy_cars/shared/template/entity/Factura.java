package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.EstadoFactura;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"detalles", "formasPago"})
@Entity
@Table(
    name = "factura",
    uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(
            name = "uk_factura_numero_eliminado",
            columnNames = {"numero_factura", "eliminado"}
        )
    }
)
public class Factura extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "numero_factura", nullable = false)
    private Long numeroFactura;

    @Column(name = "fecha_factura", nullable = false)
    private LocalDate fechaFactura;

    @Column(name = "total_pagado", nullable = false)
    private Double totalPagado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoFactura estado = EstadoFactura.SIN_DEFINIR;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "eliminado = false")
    private List<DetalleFactura> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "eliminado = false")
    private List<FormaDePago> formasPago = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void agregarDetalle(DetalleFactura detalle) {
        detalle.setFactura(this);
        detalle.setEliminado(false);
        detalles.add(detalle);
    }

    public void agregarFormaPago(FormaDePago formaDePago) {
        formaDePago.setFactura(this);
        formaDePago.setEliminado(false);
        formasPago.add(formaDePago);
    }

    public void limpiarDetalles() {
        detalles.forEach(detalle -> detalle.setFactura(null));
        detalles.clear();
    }

    public void limpiarFormasPago() {
        formasPago.forEach(forma -> forma.setFactura(null));
        formasPago.clear();
    }
}
