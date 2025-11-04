package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.uncuyo.greedy_cars.shared.template.enums.TipoTelefono;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "contacto_telefonico")
public class ContactoTelefonico extends Contacto {

    @NotBlank
    @Size(max = 20)
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_telefono", nullable = false, length = 20)
    private TipoTelefono tipoTelefono;
}
