package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.gredy_cars_client.gredy_cars_client.shared.template.enums.CanalContacto;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoContacto;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoTelefono;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Contacto utilizado por el cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContactoDTO extends BaseDTO<Long> {

    @NotNull(message = "El tipo de contacto es obligatorio")
    private TipoContacto tipoContacto;

    @NotNull(message = "El canal de contacto es obligatorio")
    private CanalContacto canalContacto;

    @Size(max = 200, message = "El valor no puede exceder los 200 caracteres")
    private String valor;

    private TipoTelefono tipoTelefono;

    @Size(max = 10, message = "El código de área no puede exceder los 10 caracteres")
    private String codigoArea;

    @Size(max = 20, message = "El número no puede exceder los 20 caracteres")
    private String numero;

    @Override
    public Long getId() {
        return super.id;
    }

    @Override
    public void setId(Long id) {
        super.id = id;
    }
}
