package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClienteDTO extends BaseDTO<String> {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder los 120 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del cliente es obligatorio")
    @Size(max = 120, message = "El apellido no puede exceder los 120 caracteres")
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder los 20 caracteres")
    private String numeroDocumento;

    private String telefono;

    private String correoElectronico;

    private String direccionEstadia;

    /**
     * Id de la nacionalidad asociada
     */
    private String nacionalidadId;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
