package com.uncuyo.greedy_cars.shared.template.dto;

import com.uncuyo.greedy_cars.shared.template.enums.TipoContacto;
import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento;
import com.uncuyo.greedy_cars.shared.template.enums.TipoTelefono;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para el registro completo de un nuevo cliente.
 * Permite crear en una sola operación: Usuario, Cliente y todas sus entidades relacionadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroClienteDTO {

    // ===== Datos del Usuario =====
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
    private String clave;

    // ===== Datos Personales del Cliente =====
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

    @Size(max = 500, message = "La dirección de estadía no puede exceder los 500 caracteres")
    private String direccionEstadia;

    // ===== Dirección Principal =====
    @NotNull(message = "Los datos de dirección son obligatorios")
    @Valid
    private DireccionRegistroDTO direccion;

    // ===== Nacionalidad =====
    @NotBlank(message = "La nacionalidad es obligatoria")
    @Size(max = 100, message = "La nacionalidad no puede exceder los 100 caracteres")
    private String nacionalidad;

    // ===== Contactos =====
    @NotEmpty(message = "Debe proporcionar al menos un contacto")
    @Valid
    private List<ContactoRegistroDTO> contactos = new ArrayList<>();

    // ===== Imagen (opcional) =====
    private ImagenRegistroDTO imagen;

    /**
     * DTO interno para datos de dirección
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DireccionRegistroDTO {
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 100)
        private String calle;

        @Size(max = 20)
        private String numeracion;

        @Size(max = 100)
        private String barrio;

        @Size(max = 20)
        private String pisoCasa;

        @Size(max = 20)
        private String puertaManzana;

        @Size(max = 500)
        private String observacion;

        // Datos geográficos
        @NotBlank(message = "El país es obligatorio")
        @Size(max = 100)
        private String pais;

        @NotBlank(message = "La provincia es obligatoria")
        @Size(max = 100)
        private String provincia;

        @NotBlank(message = "El departamento es obligatorio")
        @Size(max = 100)
        private String departamento;

        @NotBlank(message = "La localidad es obligatoria")
        @Size(max = 100)
        private String localidad;

        @Size(max = 10)
        private String codigoPostal;
    }

    /**
     * DTO interno para contactos (correo o teléfono)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoRegistroDTO {
        @NotNull(message = "El tipo de contacto es obligatorio")
        private TipoContacto tipoContacto;

        // Para correo electrónico
        @Email(message = "El formato del correo electrónico no es válido")
        @Size(max = 120)
        private String mail;

        // Para teléfono
        @Size(max = 20)
        private String telefono;

        private TipoTelefono tipoTelefono;

        @Size(max = 300)
        private String observacion;
    }

    /**
     * DTO interno para imagen
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImagenRegistroDTO {
        @NotBlank(message = "El nombre de la imagen es obligatorio")
        @Size(max = 255)
        private String nombre;

        @NotBlank(message = "El tipo MIME es obligatorio")
        @Size(max = 100)
        private String mime;

        @NotBlank(message = "El contenido de la imagen es obligatorio (base64)")
        private String contenidoBase64; // Base64 encoded
    }
}
