package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "clientesDestino")
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

    @Column(name = "aplica_a_todos", nullable = false)
    private boolean aplicaATodos = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "promocion_cliente_destino",
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Cliente> clientesDestino = new HashSet<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
