package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "promocion")
public class Promocion extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "porcentaje_descuento", nullable = false)
    private Double porcentajeDescuento;

    @Column(name = "codigo_descuento", nullable = false, unique = true, length = 100)
    private String codigoDescuento;

    @Column(name = "descripcion_descuento", length = 500)
    private String descripcionDescuento;

    @Column(name = "fecha_inicio_promocion", nullable = false)
    private LocalDate fechaInicioPromocion;

    @Column(name = "fecha_fin_promocion", nullable = false)
    private LocalDate fechaFinPromocion;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
