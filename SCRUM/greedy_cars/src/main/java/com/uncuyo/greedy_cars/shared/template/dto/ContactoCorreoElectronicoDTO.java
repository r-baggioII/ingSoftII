package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoContacto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad ContactoCorreoElectronico.
 * Extiende de BaseDTO para heredar propiedades comunes como id y eliminado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContactoCorreoElectronicoDTO extends BaseDTO<String> {
    
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    @Size(max = 120, message = "El correo electrónico no puede exceder los 120 caracteres")
    private String mail;
    
    @NotNull(message = "El tipo de contacto es obligatorio")
    private TipoContacto tipoContacto;
    
    @Size(max = 300, message = "La observación no puede exceder los 300 caracteres")
    private String observacion;
    
    // ID de la persona asociada (opcional)
    private String personaId;
    
    // ID de la empresa asociada (opcional)
    private String empresaId;
    
    @Override
    public String getId() {
        return super.id;
    }
    
    @Override
    public void setId(String id) {
        super.id = id;
    }
}
