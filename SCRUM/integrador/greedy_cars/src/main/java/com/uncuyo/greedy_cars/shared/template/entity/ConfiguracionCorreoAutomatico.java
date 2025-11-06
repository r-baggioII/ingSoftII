package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = "empresa")
@Entity
@Table(name = "configuracion_correo_automatico")
public class ConfiguracionCorreoAutomatico extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    @NotBlank
    @Size(max = 200)
    @Column(name = "clave", nullable = false, length = 200)
    private String clave;

    @NotBlank
    @Size(max = 10)
    @Column(name = "puerto", nullable = false, length = 10)
    private String puerto;

    @NotBlank
    @Size(max = 150)
    @Column(name = "smtp", nullable = false, length = 150)
    private String smtp;

    @NotNull
    @Column(name = "tls", nullable = false)
    private Boolean tls = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
