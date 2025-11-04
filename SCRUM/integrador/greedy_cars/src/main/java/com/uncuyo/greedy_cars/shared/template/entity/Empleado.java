package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.TipoEmpleado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Empleado ahora hereda de Persona. Se usa JOINED inheritance en Persona.
 */
@Entity
@Table(name = "empleados")
@DiscriminatorValue("EMPLEADO")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Empleado extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empleado", length = 50)
    private TipoEmpleado tipoEmpleado;

}
