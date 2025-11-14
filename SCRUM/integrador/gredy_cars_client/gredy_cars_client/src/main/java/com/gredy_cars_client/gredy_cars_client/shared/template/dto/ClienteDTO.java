package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
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
 * DTO para la entidad Cliente utilizado por el cliente.
 * Incluye campos heredados de Persona más nacionalidad y dirección de estadía.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Size(max = 500, message = "La dirección de estadía no puede exceder los 500 caracteres")
    private String direccionEstadia;

    // IDs para enviar al servidor
    private List<Long> direccionIds = new ArrayList<>();
    
    private List<String> contactoIds = new ArrayList<>();
    
    private List<String> imagenIds = new ArrayList<>();
    
    private List<String> nacionalidadIds = new ArrayList<>();
    
    // Objetos completos recibidos del servidor
    private List<DireccionDTO> direcciones = new ArrayList<>();
    
    private List<ImagenDTO> imagenes = new ArrayList<>();
    
    private List<NacionalidadDTO> nacionalidades = new ArrayList<>();
    
    // Listas separadas de contactos (deserializadas del servidor)
    private List<ContactoCorreoElectronicoDTO> contactosCorreo = new ArrayList<>();
    
    private List<ContactoTelefonicoDTO> contactosTelefono = new ArrayList<>();

    private Boolean recibirPromociones = Boolean.TRUE;

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
