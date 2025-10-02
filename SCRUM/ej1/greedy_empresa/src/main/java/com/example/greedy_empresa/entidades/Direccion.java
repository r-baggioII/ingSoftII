package com.example.greedy_empresa.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "direccion")
public class Direccion extends BaseEntity {

    @NotBlank
    @Size(max = 160)
    @Column(name = "calle", nullable = false, length = 160)
    private String calle;

    @NotBlank
    @Size(max = 20)
    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Size(max = 20)
    @Column(name = "numero_interno", length = 20)
    private String numeroInterno;

    @Size(max = 50)
    @Column(name = "manzana", length = 50)
    private String manzana;

    @Size(max = 50)
    @Column(name = "casa_piso", length = 50)
    private String casaPiso;

    @Size(max = 120)
    @Column(name = "sector_departamento", length = 120)
    private String sectorDepartamento;

    @Size(max = 255)
    @Column(name = "referencia", length = 255)
    private String referencia;

    // Coordenadas geográficas (Google Maps) - opcionales
    @Size(max = 32)
    @Column(name = "latitud", length = 32)
    private String latitud;

    @Size(max = 32)
    @Column(name = "longitud", length = 32)
    private String longitud;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "localidad_id", nullable = false)
    @ToString.Exclude
    private Localidad localidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    @ToString.Exclude
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    @ToString.Exclude
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    @ToString.Exclude
    private Proveedor proveedor;

    @Transient
    public boolean hasGeoPoint() {
        return latitud != null && !latitud.isBlank() && longitud != null && !longitud.isBlank();
    }

    @Transient
    public String getGoogleMapsUrl() {
        return hasGeoPoint() ? "https://www.google.com/maps?q=" + latitud + "," + longitud : null;
    }
}
