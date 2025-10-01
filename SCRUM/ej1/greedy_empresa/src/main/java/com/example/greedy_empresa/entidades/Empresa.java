package com.example.greedy_empresa.entidades;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
@Table(name = "empresa", uniqueConstraints = {
        @UniqueConstraint(name = "uk_empresa_razon_social", columnNames = { "razon_social" })
})
public class Empresa extends BaseEntity {

    @NotBlank
    @Size(max = 160)
    @Column(name = "razon_social", nullable = false, unique = true, length = 160)
    private String razonSocial;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    @Where(clause = "eliminado = false")
    @ToString.Exclude
    private List<Direccion> direcciones = new ArrayList<>();
}
