package org.sistemaMecanico.entity; // Paquete de tu proyecto

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Where(clause = "eliminado = false") // Para el soft-delete, como en las otras clases
@Table(name = "usuario")
public class Usuario extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Generación automática de ID
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 4, max = 50)
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @NotBlank(message = "La clave no puede estar vacía")
    @Column(name = "clave", nullable = false, length = 255) // Longitud para clave hasheada
    private String clave;

}