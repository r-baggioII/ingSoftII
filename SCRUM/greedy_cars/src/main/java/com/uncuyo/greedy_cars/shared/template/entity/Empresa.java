package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "empresa")
@Inheritance(strategy = InheritanceType.JOINED)
public class Empresa extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contacto> contactos = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "empresa_direccion",
        joinColumns = @JoinColumn(name = "empresa_id"),
        inverseJoinColumns = @JoinColumn(name = "direccion_id")
    )
    private List<Direccion> direcciones = new ArrayList<>();

    // Implementación de métodos abstractos de BaseEntity
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    // Métodos de conveniencia para la relación bidireccional con contactos
    public void addContacto(Contacto contacto) {
        contactos.add(contacto);
        contacto.setEmpresa(this);
    }

    public void removeContacto(Contacto contacto) {
        contactos.remove(contacto);
        contacto.setEmpresa(null);
    }

    // Métodos de conveniencia para la relación con direcciones
    public void addDireccion(Direccion direccion) {
        direcciones.add(direccion);
    }

    public void removeDireccion(Direccion direccion) {
        direcciones.remove(direccion);
    }
}
