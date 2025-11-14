package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento;
import com.uncuyo.greedy_cars.shared.template.enums.TipoEmpleado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
 * DTO para la entidad Empleado.
 * Extiende las propiedades de Persona e incluye el tipo de empleado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmpleadoDTO extends BaseDTO<String> {

    // Campos heredados de Persona
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder los 120 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 120, message = "El apellido no puede exceder los 120 caracteres")
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder los 20 caracteres")
    private String numeroDocumento;

    // IDs de las direcciones asociadas (opcional para empleado)
    private List<Long> direccionIds = new ArrayList<>();
    
    // IDs de contactos existentes a asociar (opcional)
    private List<String> contactoIds = new ArrayList<>();
    
    // IDs de imágenes existentes a asociar (opcional)
    private List<String> imagenIds = new ArrayList<>();

    // Campo específico de Empleado
    @NotNull(message = "El tipo de empleado es obligatorio")
    private TipoEmpleado tipoEmpleado;
    
    // Objetos completos para devolver al cliente (no se usan para guardar)
    private List<DireccionDTO> direcciones;
    private List<ImagenDTO> imagenes;
    private List<ContactoCorreoElectronicoDTO> contactosCorreo;
    private List<ContactoTelefonicoDTO> contactosTelefono;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
