package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Where;
import com.uncuyo.greedy_cars.shared.template.enums.Rol;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "usuario")
public class Usuario extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 4, max = 50)
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "clave", length = 255)
    private String clave;  // Puede ser null para usuarios de Auth0

    @Size(max = 120)
    @Column(name = "email", length = 120)
    private String email; // Email del usuario (para Auth0 y notificaciones)

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Rol rol = Rol.CLIENTE;
    
    // ===== CAMPOS PARA AUTH0 =====
    
    @Column(name = "external_id", unique = true, length = 100)
    private String externalId; // Almacena el auth0Sub (ej: "auth0|123456789")
    
    @Column(name = "is_external")
    private Boolean isExternal = false; // true si es usuario de Auth0
    
    @Column(name = "provider", length = 50)
    private String provider; // "AUTH0", "GOOGLE", "FACEBOOK", etc.
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false; // Si el email fue verificado por Auth0

    // Relación con Persona (puede ser Cliente o Empleado)
    // No se usa cascade para evitar eliminaciones accidentales de personas
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", unique = true)
    private Persona persona;
}