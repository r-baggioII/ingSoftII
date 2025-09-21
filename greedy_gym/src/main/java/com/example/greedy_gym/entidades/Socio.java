package com.example.greedy_gym.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "socio", uniqueConstraints = {
        @UniqueConstraint(name = "uk_socio_documento_activo", columnNames = {"numero_documento", "eliminado"}),
        @UniqueConstraint(name = "uk_socio_correo_activo", columnNames = {"correo_electronico", "eliminado"}),
        @UniqueConstraint(name = "uk_socio_numero_activo", columnNames = {"numero_socio", "eliminado"})
})
public class Socio extends Persona {

    @Column(name = "numero_socio", nullable = false)
    private Long numeroSocio;

    @Column(name = "usuario_id")
    private String usuarioId;
}
