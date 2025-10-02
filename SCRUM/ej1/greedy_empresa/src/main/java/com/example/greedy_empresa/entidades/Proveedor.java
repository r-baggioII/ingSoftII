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
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "proveedor", uniqueConstraints = {
        @UniqueConstraint(name = "uk_proveedor_cuit", columnNames = { "cuit" })
})
public class Proveedor extends BaseEntity {

    @NotBlank
    @Size(max = 20)
    @Column(name = "cuit", nullable = false, unique = true, length = 20)
    private String cuit;

    @OneToOne(fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    @ToString.Exclude
    private Persona persona;

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "eliminado = false")
    @ToString.Exclude
    private List<Direccion> direcciones = new ArrayList<>();
}
