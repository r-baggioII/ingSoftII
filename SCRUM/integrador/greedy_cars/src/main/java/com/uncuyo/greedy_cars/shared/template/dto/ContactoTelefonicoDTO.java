package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoContacto;
import com.uncuyo.greedy_cars.shared.template.enums.TipoTelefono;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad ContactoTelefonico.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContactoTelefonicoDTO extends BaseDTO<String> {
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;
    
    @NotNull(message = "El tipo de teléfono es obligatorio")
    private TipoTelefono tipoTelefono;
    
    @NotNull(message = "El tipo de contacto es obligatorio")
    private TipoContacto tipoContacto;
    
    @Size(max = 300, message = "La observación no puede exceder los 300 caracteres")
    private String observacion;
    
    // ID de la persona asociada (opcional)
    private String personaId;
    
    @Override
    public String getId() {
        return super.id;
    }
    
    @Override
    public void setId(String id) {
        super.id = id;
    }
}
