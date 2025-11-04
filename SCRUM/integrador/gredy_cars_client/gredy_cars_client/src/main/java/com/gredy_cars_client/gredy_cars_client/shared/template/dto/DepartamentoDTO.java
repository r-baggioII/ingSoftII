package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO para la entidad Departamento utilizado por el cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DepartamentoDTO extends BaseDTO<Long> {

    @NotBlank(message = "El nombre del departamento es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotNull(message = "La provincia es obligatoria")
    private ProvinciaDTO provincia;

    @Override
    public Long getId() {
        return super.id;
    }

    @Override
    public void setId(Long id) {
        super.id = id;
    }
}
