package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Dirección utilizado por el cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DireccionDTO extends BaseDTO<Long> {

    @Size(max = 100, message = "La calle no puede exceder los 100 caracteres")
    private String calle;

    @Size(max = 20, message = "La numeración no puede exceder los 20 caracteres")
    private String numeracion;

    @Size(max = 100, message = "El barrio no puede exceder los 100 caracteres")
    private String barrio;

    @Size(max = 20, message = "El piso/casa no puede exceder los 20 caracteres")
    private String pisoCasa;

    @Size(max = 20, message = "La puerta/manzana no puede exceder los 20 caracteres")
    private String puertaManzana;

    @Size(max = 500, message = "La observación no puede exceder los 500 caracteres")
    private String observacion;

    @NotNull(message = "La localidad es obligatoria")
    private LocalidadDTO localidad;

    @Override
    public Long getId() {
        return super.id;
    }

    @Override
    public void setId(Long id) {
        super.id = id;
    }
}
