package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.CaracteristicaVehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CaracteristicaVehiculoMapper {

    public CaracteristicaVehiculoDTO toDTO(CaracteristicaVehiculo entity) {
        if (entity == null) return null;
        CaracteristicaVehiculoDTO dto = new CaracteristicaVehiculoDTO();
        dto.setId(entity.getId());
        dto.setEliminado(Boolean.TRUE.equals(entity.getEliminado()));
        dto.setMarca(entity.getMarca());
        dto.setModelo(entity.getModelo());
        dto.setCantidadPuerta(entity.getCantidadPuerta());
        dto.setCantidadAsiento(entity.getCantidadAsiento());
        dto.setAnio(entity.getAnio());
        dto.setCantidadTotalVehiculo(entity.getCantidadTotalVehiculo());
        dto.setCantidadVehiculoAlquilado(entity.getCantidadVehiculoAlquilado());
        if (entity.getImagenes() != null) {
            dto.setImagenIds(entity.getImagenes().stream()
                    .filter(Objects::nonNull)
                    .map(Imagen::getId)
                    .collect(Collectors.toList()));
        } else {
            dto.setImagenIds(new ArrayList<>());
        }
        return dto;
    }

    public CaracteristicaVehiculo toEntity(CaracteristicaVehiculoDTO dto, List<Imagen> imagenes) {
        if (dto == null) return null;
        CaracteristicaVehiculo entity = new CaracteristicaVehiculo();
        entity.setId(dto.getId());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setCantidadPuerta(dto.getCantidadPuerta());
        entity.setCantidadAsiento(dto.getCantidadAsiento());
        entity.setAnio(dto.getAnio());
        entity.setCantidadTotalVehiculo(dto.getCantidadTotalVehiculo());
        entity.setCantidadVehiculoAlquilado(dto.getCantidadVehiculoAlquilado());
        entity.setImagenes(imagenes != null ? imagenes : new ArrayList<>());
        return entity;
    }
}
