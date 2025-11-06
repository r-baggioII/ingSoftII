package com.greedy_cars_institucional.institucional.shared.template.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CaracteristicaVehiculoDTO extends BaseDTO<String> {

    private static final long serialVersionUID = 1L;

    private String marca;
    private String modelo;
    private int cantidadPuerta;
    private int cantidadAsiento;
    private long anio;
    private int cantidadTotalVehiculo;
    private int cantidadVehiculoAlquilado;
    private List<String> imagenIds = new ArrayList<>();

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }

    public void setImagenIds(List<String> imagenIds) {
        this.imagenIds = imagenIds != null ? imagenIds : new ArrayList<>();
    }
}
