package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para registro intermedio de clientes autenticados con Auth0
 * No incluye usuario ni contraseña (Auth0 maneja la autenticación)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroIntermedioDTO {
    
    // Datos de Auth0
    private String externalId;
    private String email;
    private Boolean recibirPromociones;
    
    // Datos personales
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String tipoDocumento;
    private String numeroDocumento;
    private String direccionEstadia;
    private String nacionalidad;
    
    // Dirección
    private DireccionDTO direccion;
    
    // Contactos
    private List<ContactoDTO> contactos;
    
    // Imagen (opcional)
    private ImagenDTO imagen;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DireccionDTO {
        private String calle;
        private String numeracion;
        private String barrio;
        private String pisoCasa;
        private String puertaManzana;
        private String observacion;
        private String pais;
        private String provincia;
        private String departamento;
        private String localidad;
        private String codigoPostal;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoDTO {
        private String tipoContacto;
        private String mail;
        private String telefono;
        private String tipoTelefono;
        private String observacion;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImagenDTO {
        private String nombre;
        private String mime;
        private String contenidoBase64;
    }
}
