package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "clientes")
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Cliente extends Persona {

    @Column(name = "direccion_estadia", length = 500)
    private String direccionEstadia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nacionalidad_id")
    private Nacionalidad nacionalidad;

}
