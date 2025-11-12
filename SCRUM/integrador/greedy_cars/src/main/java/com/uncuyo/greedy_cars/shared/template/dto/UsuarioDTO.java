package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Usuario.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UsuarioDTO extends BaseDTO<String> {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String nombreUsuario;
    
    // La clave es opcional en modificaciones (si es null o vacía, no se cambia)
    // Solo se valida si se proporciona un valor
    @Size(min = 6, message = "La clave debe tener al menos 6 caracteres")
    private String clave;
    
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
    
    // ID de la persona asociada (obligatorio)
    @NotBlank(message = "La persona asociada es obligatoria")
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
