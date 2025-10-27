package org.sistemaMecanico.entity; // Asegúrate que este sea tu paquete

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Import para la fecha

import java.util.Date; // Import para el campo fecha
import java.util.HashSet;
import java.util.Set;

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
@Where(clause = "eliminado = false")
@Table(name = "historial_arreglo")
public class HistorialArreglo extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Column(name = "fecha_arreglo", nullable = false)
    private Date fechaArreglo;

    @NotBlank(message = "El detalle no puede estar vacío")
    @Column(name = "detalle_arreglo", nullable = false, columnDefinition = "TEXT") // TEXT para descripciones largas
    private String detalleArreglo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "arreglo_mecanico",
            joinColumns = @JoinColumn(name = "historial_arreglo_id"),
            inverseJoinColumns = @JoinColumn(name = "mecanico_id")
    )
    private Set<Mecanico> mecanicos = new HashSet<>();


    // --- MÉTODOS HEREDADOS DE BaseEntity ---
    // Lombok se encarga de generar los métodos getId() y setId(String id)
    // que satisfacen los requerimientos abstractos de BaseEntity<String>.
}