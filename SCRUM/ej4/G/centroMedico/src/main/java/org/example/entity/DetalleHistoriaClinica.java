package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "detalle_historia_clinica")
public class DetalleHistoriaClinica extends BaseEntity<String> {

    @Id
    @Column(length = 36)
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaHistoria;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detalleHistoria;

    @ManyToOne
    @JoinColumn(name = "historia_clinica_id", nullable = false)
    private HistoriaClinica historiaClinica;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    // Constructores
    public DetalleHistoriaClinica() {
        super();
    }

    public DetalleHistoriaClinica(String id, Date fechaHistoria, String detalleHistoria, HistoriaClinica historiaClinica, Medico medico) {
        this.id = id;
        this.fechaHistoria = fechaHistoria;
        this.detalleHistoria = detalleHistoria;
        this.historiaClinica = historiaClinica;
        this.medico = medico;
        this.eliminado = false;
    }

    // Getters y Setters
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Date getFechaHistoria() {
        return fechaHistoria;
    }

    public void setFechaHistoria(Date fechaHistoria) {
        this.fechaHistoria = fechaHistoria;
    }

    public String getDetalleHistoria() {
        return detalleHistoria;
    }

    public void setDetalleHistoria(String detalleHistoria) {
        this.detalleHistoria = detalleHistoria;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
