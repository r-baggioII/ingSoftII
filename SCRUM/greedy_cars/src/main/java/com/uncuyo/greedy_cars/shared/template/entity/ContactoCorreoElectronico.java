package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "contacto_correo_electronico")
public class ContactoCorreoElectronico extends Contacto {

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(name = "mail", nullable = false, length = 120)
    private String mail;
}












