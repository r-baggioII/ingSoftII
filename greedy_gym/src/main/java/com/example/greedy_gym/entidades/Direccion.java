package com.example.greedy_gym.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "direcciones")
public class Direccion {

    @Id
    private String id;

    @Column(length = 120)
    private String calle;

    @Column(length = 20)
    private String numero;

    @Column(length = 120)
    private String ciudad;

    @Column(length = 120)
    private String provincia;

    @Column(length = 120)
    private String pais;

    @Column(length = 20)
    private String codigoPostal;

    @Column(length = 120)
    private String barrio;

    @Column(length = 60)
    private String manzanaPiso;

    @Column(length = 60)
    private String casaDepartamento;

    @Column(length = 255)
    private String referencia;

    @Column(nullable = false)
    private boolean eliminado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "localidad_id", nullable = true)
    private Localidad localidad;

    @PrePersist
    private void prePersist() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }
}
