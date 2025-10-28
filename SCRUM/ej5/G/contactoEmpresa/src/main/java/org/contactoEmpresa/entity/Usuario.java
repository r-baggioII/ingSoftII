package org.contactoEmpresa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Where(clause = "eliminado = false")
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

    @NotBlank(message = "La clave no puede estar vacía")
    @Column(name = "clave", nullable = false, length = 255)
    private String clave;

    // ✨ NUEVO: Campo para el rol
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Rol rol = Rol.USER;

    // ✨ NUEVO: Enum para los roles
    public enum Rol {
        ADMIN,
        USER,
        MECANICO
    }
}