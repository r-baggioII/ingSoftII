package com.uncuyo.greedy_cars.shared.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para la entidad Empresa.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmpresaDTO extends BaseDTO<String> {
    
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder los 120 caracteres")
    private String nombre;
    
    // IDs de las direcciones asociadas - al menos una dirección es obligatoria
    @NotEmpty(message = "La empresa debe tener al menos una dirección asociada")
    private List<Long> direccionIds = new ArrayList<>();
    
    // IDs de los contactos asociados (opcional)
    private List<String> contactoIds = new ArrayList<>();
    
    @Override
    public String getId() {
        return super.id;
    }
    
    @Override
    public void setId(String id) {
        super.id = id;
    }
}
