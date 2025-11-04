package com.uncuyo.greedy_cars.shared.template.entity;

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
@Table(name = "direccion")
public class Direccion extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 100)
    @Column(name = "calle", length = 100)
    private String calle;

    @Size(max = 20)
    @Column(name = "numeracion", length = 20)
    private String numeracion;

    @Size(max = 100)
    @Column(name = "barrio", length = 100)
    private String barrio;

    @Size(max = 20)
    @Column(name = "piso_casa", length = 20)
    private String pisoCasa;

    @Size(max = 20)
    @Column(name = "puerta_manzana", length = 20)
    private String puertaManzana;

    @Size(max = 500)
    @Column(name = "observacion", length = 500)
    private String observacion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "localidad_id", nullable = false)
    private Localidad localidad;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
