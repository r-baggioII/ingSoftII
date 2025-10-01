package com.example.greedy_empresa.entidades;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "departamento", uniqueConstraints = {
        @UniqueConstraint(name = "uk_departamento_nombre_provincia", columnNames = { "nombre", "provincia_id" })
})
public class Departamento extends BaseEntity {

    @NotBlank
    @Size(max = 120)
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provincia_id", nullable = false)
    @ToString.Exclude
    private Provincia provincia;

    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY)
    @Where(clause = "eliminado = false")
    @ToString.Exclude
    private List<Localidad> localidades = new ArrayList<>();

    @Transient
    private String paisId;

    @Transient
    private String provinciaId;
}
