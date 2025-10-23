package org.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historia_clinica")
public class HistoriaClinica extends BaseEntity<String> {

    @Id
    @Column(length = 36)
    private String id;

    @OneToOne
    @JoinColumn(name = "paciente_id", nullable = true, unique = true)
    private Paciente paciente;

    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleHistoriaClinica> detalles = new ArrayList<>();

    // Constructores
    public HistoriaClinica() {
        super();
    }

    public HistoriaClinica(String id, Paciente paciente) {
        this.id = id;
        this.paciente = paciente;
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

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<DetalleHistoriaClinica> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleHistoriaClinica> detalles) {
        this.detalles = detalles;
    }
}
