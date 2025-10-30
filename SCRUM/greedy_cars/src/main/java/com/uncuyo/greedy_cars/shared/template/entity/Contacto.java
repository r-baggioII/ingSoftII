package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.uncuyo.greedy_cars.shared.template.enums.TipoContacto;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "contacto")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Contacto extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contacto", nullable = false, length = 20)
    private TipoContacto tipoContacto;

    @Size(max = 300)
    @Column(name = "observacion", length = 300)
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}

