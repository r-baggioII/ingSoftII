package com.example.greedy_gym.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "promocion")
public class Promocion extends Mensaje {

    @Column(name = "fecha_envio_promocion", nullable = false)
    private LocalDateTime fechaEnvioPromocion;

    @Column(name = "cantidad_socios_enviados")
    private Long cantidadSociosEnviados = 0L;

    @Column(name = "enviada", nullable = false)
    private boolean enviada = false;

    @Column(name = "fecha_envio_real")
    private LocalDateTime fechaEnvioReal;

    @ManyToMany
    @JoinTable(name = "promocion_socios",
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "socio_id"))
    private Set<Socio> destinatarios = new LinkedHashSet<>();

    public LocalDateTime getFechaEnvioPromocion() {
        return fechaEnvioPromocion;
    }

    public void setFechaEnvioPromocion(LocalDateTime fechaEnvioPromocion) {
        this.fechaEnvioPromocion = fechaEnvioPromocion;
    }

    public Long getCantidadSociosEnviados() {
        return cantidadSociosEnviados;
    }

    public void setCantidadSociosEnviados(Long cantidadSociosEnviados) {
        this.cantidadSociosEnviados = cantidadSociosEnviados;
    }

    public boolean isEnviada() {
        return enviada;
    }

    public void setEnviada(boolean enviada) {
        this.enviada = enviada;
    }

    public LocalDateTime getFechaEnvioReal() {
        return fechaEnvioReal;
    }

    public void setFechaEnvioReal(LocalDateTime fechaEnvioReal) {
        this.fechaEnvioReal = fechaEnvioReal;
    }

    public Set<Socio> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(Set<Socio> destinatarios) {
        this.destinatarios = destinatarios;
    }
}
