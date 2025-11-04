package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Localidad.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LocalidadDTO extends BaseDTO<Long> {
    
    @NotBlank(message = "El nombre de la localidad es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;
    
    @Size(max = 10, message = "El código postal no puede exceder los 10 caracteres")
    private String codigoPostal;
    
    @NotNull(message = "El departamento es obligatorio")
    private DepartamentoDTO departamento;
    
    @Override
    public Long getId() {
        return super.id;
    }
    
    @Override
    public void setId(Long id) {
        super.id = id;
    }
}
