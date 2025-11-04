package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Pais.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaisDTO extends BaseDTO<Long> {
    
    @NotBlank(message = "El nombre del país es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;
    
    @Override
    public Long getId() {
        return super.id;
    }
    
    @Override
    public void setId(Long id) {
        super.id = id;
    }
}
