package com.example.greedy_gym.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "forma_de_pago")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FormaDePago {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 60)
    private TipoPago tipoPago;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
