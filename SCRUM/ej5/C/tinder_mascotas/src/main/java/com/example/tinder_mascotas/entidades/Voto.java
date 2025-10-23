package com.example.tinder_mascotas.entidades;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.ManyToOne;

@Entity
public class Voto {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Temporal(TemporalType.TIMESTAMP)
    private Date voto1;
    @Temporal(TemporalType.TIMESTAMP)
    private Date voto2;

    @ManyToOne
    private Mascota mascota1;
    @ManyToOne
    private Mascota mascota2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getVoto1() {
        return voto1;
    }

    public void setVoto1(Date voto1) {
        this.voto1 = voto1;
    }

    public Date getVoto2() {
        return voto2;
    }

    public void setVoto2(Date voto2) {
        this.voto2 = voto2;
    }

    public Mascota getMascota1() {
        return mascota1;
    }

    public void setMascota1(Mascota mascota1) {
        this.mascota1 = mascota1;
    }

    public Mascota getMascota2() {
        return mascota2;
    }

    public void setMascota2(Mascota mascota2) {
        this.mascota2 = mascota2;
    }
}
