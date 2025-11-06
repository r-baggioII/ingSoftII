package com.greedy_cars_institucional.institucional.shared.template.service;

import org.springframework.stereotype.Service;

import com.greedy_cars_institucional.institucional.shared.template.dao.CaracteristicaVehiculoDao;
import com.greedy_cars_institucional.institucional.shared.template.dto.CaracteristicaVehiculoDTO;

@Service
public class CaracteristicaVehiculoService extends BaseClientService<CaracteristicaVehiculoDTO, String> {

    public CaracteristicaVehiculoService(CaracteristicaVehiculoDao dao) {
        super(dao);
    }
}
