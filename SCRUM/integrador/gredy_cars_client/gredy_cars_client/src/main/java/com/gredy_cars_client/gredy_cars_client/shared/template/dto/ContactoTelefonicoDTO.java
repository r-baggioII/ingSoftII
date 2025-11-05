package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoContacto;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoTelefono;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para contacto telefónico que coincide con el contrato del backend.
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

