package com.example.greedy_gym.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "empleado", uniqueConstraints = {
        @UniqueConstraint(name = "uk_empleado_documento_activo", columnNames = {"numero_documento", "eliminado"}),
        @UniqueConstraint(name = "uk_empleado_correo_activo", columnNames = {"correo_electronico", "eliminado"}),
        @UniqueConstraint(name = "uk_empleado_usuario_activo", columnNames = {"usuario_id", "eliminado"})
})
public class Empleado extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empleado", nullable = false)
    private TipoEmpleado tipoEmpleado;
}
