package com.example.greedy_gym.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "sucursal", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sucursal_nombre_empresa", columnNames = {"nombre", "empresa_id", "eliminado"}),
        @UniqueConstraint(name = "uk_sucursal_direccion_activa", columnNames = {"direccion_id", "eliminado"})
})
public class Sucursal {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "direccion_id", nullable = false)
    private Direccion direccion;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
