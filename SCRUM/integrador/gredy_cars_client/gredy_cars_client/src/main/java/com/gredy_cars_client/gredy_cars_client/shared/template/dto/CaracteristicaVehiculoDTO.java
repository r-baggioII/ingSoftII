package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CaracteristicaVehiculoDTO extends BaseDTO<String> {

    private String marca;
    private String modelo;
    private int cantidadPuerta;
    private int cantidadAsiento;
    private long anio;
    private int cantidadTotalVehiculo;
    private int cantidadVehiculoAlquilado;

    // Referencias a imágenes asociadas
    private List<String> imagenIds = new ArrayList<>();

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public void setId(String id) {
        super.id = id;
    }
}
