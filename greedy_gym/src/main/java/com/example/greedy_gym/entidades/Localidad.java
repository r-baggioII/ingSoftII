package com.example.greedy_gym.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "localidades")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Localidad {

    @Id
    private String id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(nullable = false)
    private boolean eliminado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Departamento departamento;

    @PrePersist
    private void prePersist() {
        if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
    }
}
