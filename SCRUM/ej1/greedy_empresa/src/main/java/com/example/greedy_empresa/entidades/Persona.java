package com.example.greedy_empresa.entidades;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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
@Table(name = "persona")
public class Persona extends BaseEntity {

    @NotBlank
    @Size(max = 120)
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @NotBlank
    @Size(max = 120)
    @Column(name = "apellido", nullable = false, length = 120)
    private String apellido;

    @Size(max = 30)
    @Column(name = "telefono", length = 30)
    private String telefono;

    @NotBlank
    @Email
    @Size(max = 160)
    @Column(name = "correo_electronico", nullable = false, length = 160)
    private String correoElectronico;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    @ToString.Exclude
    private Usuario usuario;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    @Where(clause = "eliminado = false")
    @ToString.Exclude
    private List<Direccion> direcciones = new ArrayList<>();
}
