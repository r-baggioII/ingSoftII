package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoEmpleado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para la entidad Empleado utilizado por el cliente.
 * Incluye campos heredados de Persona y el tipo de empleado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmpleadoDTO extends BaseDTO<String> {

    @NotBlank(message = "El nombre del empleado es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder los 120 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del empleado es obligatorio")
    @Size(max = 120, message = "El apellido no puede exceder los 120 caracteres")
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder los 20 caracteres")
    private String numeroDocumento;

    @NotNull(message = "El tipo de empleado es obligatorio")
    private TipoEmpleado tipoEmpleado;
    
    // IDs de relaciones (para enviar al servidor)
    private List<Long> direccionIds = new ArrayList<>();
    private List<String> contactoIds = new ArrayList<>();
    private List<String> imagenIds = new ArrayList<>();
    
    // Objetos completos (para mostrar en el cliente, no se envían al servidor)
    private List<ContactoCorreoElectronicoDTO> contactosCorreo = new ArrayList<>();
    private List<ContactoTelefonicoDTO> contactosTelefono = new ArrayList<>();
    private List<DireccionDTO> direcciones = new ArrayList<>();
    private List<ImagenDTO> imagenes = new ArrayList<>();

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
