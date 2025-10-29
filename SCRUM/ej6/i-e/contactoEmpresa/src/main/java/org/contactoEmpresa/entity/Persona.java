package org.contactoEmpresa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Where(clause = "eliminado = false")
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)  // ADD THIS LINE
@DiscriminatorColumn(name = "tipo_persona")       // OPTIONAL: helps identify types
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

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contacto> contactos = new ArrayList<>();

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
