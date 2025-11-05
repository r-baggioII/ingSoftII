package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoContacto;
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
 * DTO para contacto de correo electrónico replicando el contrato remoto.
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
