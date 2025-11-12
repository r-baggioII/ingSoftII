package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento;
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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para la entidad Cliente.
 * Extiende las propiedades de Persona e incluye campos específicos de Cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO extends BaseDTO<String> {

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

    // IDs de las direcciones asociadas - al menos una dirección es obligatoria
    @NotEmpty(message = "El cliente debe tener al menos una dirección asociada")
    private List<Long> direccionIds = new ArrayList<>();
    
    // IDs de contactos existentes a asociar (opcional)
    private List<String> contactoIds = new ArrayList<>();
    
    // IDs de imágenes existentes a asociar (opcional)
    private List<String> imagenIds = new ArrayList<>();

    // IDs de nacionalidades - al menos una es obligatoria
    @NotEmpty(message = "El cliente debe tener al menos una nacionalidad")
    private List<String> nacionalidadIds = new ArrayList<>();

    // Campos específicos de Cliente
    @Size(max = 500, message = "La dirección de estadía no puede exceder los 500 caracteres")
    private String direccionEstadia;
    
    // ID del usuario asociado (opcional)
    private String usuarioId;
    
    // Objetos completos para devolver al cliente (no se usan para guardar)
    private List<DireccionDTO> direcciones;
    private List<Object> contactos;  // Puede ser ContactoCorreoElectronicoDTO o ContactoTelefonicoDTO
    private List<ImagenDTO> imagenes;
    private List<NacionalidadDTO> nacionalidades;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
