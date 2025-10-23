package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medico")
public class Medico extends BaseEntity<String> {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @OneToMany(mappedBy = "medico", fetch = FetchType.LAZY)
    private List<DetalleHistoriaClinica> detallesHistoria = new ArrayList<>();

    // Constructores
    public Medico() {
        super();
    }

    public Medico(String id, String nombre, String apellido, String documento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public List<DetalleHistoriaClinica> getDetallesHistoria() {
        return detallesHistoria;
    }

    public void setDetallesHistoria(List<DetalleHistoriaClinica> detallesHistoria) {
        this.detallesHistoria = detallesHistoria;
    }
}
