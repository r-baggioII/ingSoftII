package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoContacto;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoTelefono;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para el registro completo de un nuevo cliente desde el formulario público.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroClienteDTO {

    // ===== Datos del Usuario =====
    private String nombreUsuario;
    private String clave;

    // ===== Datos Personales del Cliente =====
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String direccionEstadia;

    // ===== Dirección Principal =====
    private DireccionRegistroDTO direccion;

    // ===== Nacionalidad =====
    private String nacionalidad;

    // ===== Contactos =====
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
        private String calle;
        private String numeracion;
        private String barrio;
        private String pisoCasa;
        private String puertaManzana;
        private String observacion;

        // Datos geográficos
        private String pais;
        private String provincia;
        private String departamento;
        private String localidad;
        private String codigoPostal;
    }

    /**
     * DTO interno para contactos (correo o teléfono)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoRegistroDTO {
        private TipoContacto tipoContacto;

        // Para correo electrónico
        private String mail;

        // Para teléfono
        private String telefono;
        private TipoTelefono tipoTelefono;

        private String observacion;
    }

    /**
     * DTO interno para imagen
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImagenRegistroDTO {
        private String nombre;
        private String mime;
        private String contenidoBase64;
    }
}
