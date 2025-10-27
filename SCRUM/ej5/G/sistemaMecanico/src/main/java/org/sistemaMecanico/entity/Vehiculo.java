package org.sistemaMecanico.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Where(clause = "eliminado = false") // Filtra automáticamente los 'eliminados'
@Table(name = "vehiculo")
public class Vehiculo extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;


    @Size(max = 20)
    @Column(name = "patente", nullable = false, unique = true, length = 20)
    private String patente;


    @Size(max = 100)
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @Size(max = 100)
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @OneToMany(
            mappedBy = "vehiculo",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<HistorialArreglo> historiales = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = true)
    private Cliente cliente;

    // --- MÉTODOS HEREDADOS DE BaseEntity ---
    // Lombok se encarga de generar los métodos getId() y setId(String id)
    // que satisfacen los requerimientos abstractos de BaseEntity<String>.
}