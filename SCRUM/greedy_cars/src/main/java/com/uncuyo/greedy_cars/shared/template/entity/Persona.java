package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)  
@DiscriminatorColumn(name = "tipo_persona")      
public class Persona extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @NotBlank
    @Size(max = 120)
    @Column(name = "apellido", nullable = false, length = 120)
    private String apellido;

    @NotNull
    @Past
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @NotBlank
    @Size(max = 20)
    @Column(name = "numero_documento", nullable = false, length = 20, unique = true)
    private String numeroDocumento;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contacto> contactos = new ArrayList<>();

    // Implementación de métodos abstractos de BaseEntity
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    // Métodos de conveniencia para la relación bidireccional
    public void addContacto(Contacto contacto) {
        contactos.add(contacto);
        contacto.setPersona(this);
    }

    public void removeContacto(Contacto contacto) {
        contactos.remove(contacto);
        contacto.setPersona(null);
    }

}
