package com.uncuyo.greedy_cars.shared.template.mapper;

import com.uncuyo.greedy_cars.shared.template.dto.CostoVehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.dto.CaracteristicaVehiculoMinDTO;
import com.uncuyo.greedy_cars.shared.template.entity.CostoVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import org.springframework.stereotype.Component;

@Component
public class CostoVehiculoMapper {

    public CostoVehiculoDTO toDTO(CostoVehiculo entity) {
        if (entity == null) return null;
        CostoVehiculoDTO dto = new CostoVehiculoDTO();
        dto.setId(entity.getId());
        dto.setEliminado(Boolean.TRUE.equals(entity.getEliminado()));
        dto.setFechaDesde(entity.getFechaDesde());
        dto.setFechaHasta(entity.getFechaHasta());
        dto.setCosto(entity.getCosto());
        if (entity.getCaracteristicaVehiculo() != null) {
            dto.setIdCaracteristicaVehiculo(entity.getCaracteristicaVehiculo().getId());
            CaracteristicaVehiculoMinDTO min = new CaracteristicaVehiculoMinDTO();
            min.setId(entity.getCaracteristicaVehiculo().getId());
            min.setMarca(entity.getCaracteristicaVehiculo().getMarca());
            min.setModelo(entity.getCaracteristicaVehiculo().getModelo());
            dto.setCaracteristica(min);
        } else {
            dto.setIdCaracteristicaVehiculo(null);
            dto.setCaracteristica(null);
        }
        return dto;
    }

    public CostoVehiculo toEntity(CostoVehiculoDTO dto, CaracteristicaVehiculo caracteristica) {
        if (dto == null) return null;
        CostoVehiculo entity = new CostoVehiculo();
        entity.setId(dto.getId());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setFechaDesde(dto.getFechaDesde());
        entity.setFechaHasta(dto.getFechaHasta());
        entity.setCosto(dto.getCosto());
        entity.setCaracteristicaVehiculo(caracteristica);
        return entity;
    }
}
