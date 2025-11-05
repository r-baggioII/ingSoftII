package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.TipoImagen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@ToString(callSuper = true, exclude = "imagenes")
@Entity
@Table(name = "caracteristica_vehiculo")
public class CaracteristicaVehiculo extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @NotBlank
    @Size(max = 100)
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "cantidad_puerta", nullable = false)
    private int cantidadPuerta;

    @Column(name = "cantidad_asiento", nullable = false)
    private int cantidadAsiento;

    @Column(name = "anio", nullable = false)
    private long anio;

    @Column(name = "cantidad_total_vehiculo", nullable = false)
    private int cantidadTotalVehiculo;

    @Column(name = "cantidad_vehiculo_alquilado", nullable = false)
    private int cantidadVehiculoAlquilado;

    // Relación uno a muchos con imagenes
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "caracteristica_vehiculo_id")
    private List<Imagen> imagenes = new ArrayList<>();

    // Relación uno a muchos con costos
    @OneToMany(mappedBy = "caracteristicaVehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CostoVehiculo> costos = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    // Métodos de dominio
    public void crearCaracteristicaVehiculo(String marca, String modelo, int cantidadPuerta,
                                           int cantidadAsiento, long anio,
                                           int cantidadTotalVehiculo, int cantidadVehiculoAlquilado,
                                           List<Imagen> imagenes) {
        validar(marca, modelo, cantidadPuerta, cantidadAsiento, anio, cantidadTotalVehiculo, cantidadVehiculoAlquilado, imagenes);
        this.marca = marca;
        this.modelo = modelo;
        this.cantidadPuerta = cantidadPuerta;
        this.cantidadAsiento = cantidadAsiento;
        this.anio = anio;
        this.cantidadTotalVehiculo = cantidadTotalVehiculo;
        this.cantidadVehiculoAlquilado = cantidadVehiculoAlquilado;
        this.imagenes = imagenes != null ? imagenes : new ArrayList<>();
        this.eliminado = false;
    }

    public void validar(String marca, String modelo, int cantidadPuerta,
                        int cantidadAsiento, long anio,
                        int cantidadTotalVehiculo, int cantidadVehiculoAlquilado,
                        List<Imagen> imagenes) {
        if (marca == null || marca.trim().isEmpty()) throw new IllegalArgumentException("Debe indicar la marca");
        if (modelo == null || modelo.trim().isEmpty()) throw new IllegalArgumentException("Debe indicar el modelo");
        if (cantidadPuerta <= 0) throw new IllegalArgumentException("La cantidad de puertas debe ser mayor a 0");
        if (cantidadAsiento <= 0) throw new IllegalArgumentException("La cantidad de asientos debe ser mayor a 0");
        if (anio <= 0) throw new IllegalArgumentException("El año debe ser un número válido");
        if (cantidadTotalVehiculo < 0) throw new IllegalArgumentException("La cantidad total de vehículos no puede ser negativa");
        if (cantidadVehiculoAlquilado < 0) throw new IllegalArgumentException("La cantidad de vehículos alquilados no puede ser negativa");
        if (cantidadVehiculoAlquilado > cantidadTotalVehiculo) throw new IllegalArgumentException("La cantidad alquilada no puede ser mayor que la cantidad total");
        // imagenes pueden ser null o vacías, no es obligatorio pero si están deben existir (validación a nivel de servicio)
    }

    public void modificarCaracteristicaVehiculo(String marca, String modelo, int cantidadPuerta,
                                               int cantidadAsiento, long anio,
                                               int cantidadTotalVehiculo, int cantidadVehiculoAlquilado,
                                               List<Imagen> imagenes) {
        validar(marca, modelo, cantidadPuerta, cantidadAsiento, anio, cantidadTotalVehiculo, cantidadVehiculoAlquilado, imagenes);
        this.marca = marca != null ? marca : this.marca;
        this.modelo = modelo != null ? modelo : this.modelo;
        this.cantidadPuerta = cantidadPuerta;
        this.cantidadAsiento = cantidadAsiento;
        this.anio = anio;
        this.cantidadTotalVehiculo = cantidadTotalVehiculo;
        this.cantidadVehiculoAlquilado = cantidadVehiculoAlquilado;
        if (imagenes != null) {
            this.imagenes.clear();
            this.imagenes.addAll(imagenes);
        }
    }

    public void eliminarCaracteristicaVehiculo() {
        this.eliminado = true;
    }
}
