package com.example.greedy_gym.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @NotBlank
    @Size(min = 2, max = 200)
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank
    @Size(min = 1, max = 2000)
    @Column(nullable = false, length = 2000)
    private String texto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mensaje", nullable = false, length = 20)
    private TipoMensaje tipoMensaje;

    @Column(nullable = false)
    private boolean eliminado = false;

    public Mensaje() { }

    public Mensaje(String titulo, String texto, TipoMensaje tipoMensaje) {
        this.titulo = titulo;
        this.texto = texto;
        this.tipoMensaje = tipoMensaje;
    }

    @PrePersist
    public void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public TipoMensaje getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(TipoMensaje tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(id, mensaje.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

