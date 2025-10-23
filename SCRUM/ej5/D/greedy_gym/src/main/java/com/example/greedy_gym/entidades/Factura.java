package com.example.greedy_gym.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "factura", uniqueConstraints = {
        @UniqueConstraint(name = "uk_factura_numero_eliminado", columnNames = {"numero_factura", "eliminado"})
})
public class Factura {

    @Id
    private String id;

    @Column(name = "numero_factura", nullable = false)
    private Long numeroFactura;

    @Column(name = "fecha_factura", nullable = false)
    private LocalDate fechaFactura;

    @Column(name = "total_pagado", nullable = false)
    private Double totalPagado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFactura estado = EstadoFactura.SIN_DEFINIR;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forma_pago_id", nullable = false)
    private FormaDePago formaDePago;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "eliminado = false")
    @JsonManagedReference
    private List<DetalleFactura> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (estado == null) {
            estado = EstadoFactura.SIN_DEFINIR;
        }
    }

    public void agregarDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }

    public void limpiarDetalles() {
        detalles.forEach(detalle -> detalle.setFactura(null));
        detalles.clear();
    }
}
