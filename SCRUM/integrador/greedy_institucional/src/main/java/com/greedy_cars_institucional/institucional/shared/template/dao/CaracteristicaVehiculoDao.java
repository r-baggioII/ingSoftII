package com.greedy_cars_institucional.institucional.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.greedy_cars_institucional.institucional.config.GreedyApiProperties;
import com.greedy_cars_institucional.institucional.shared.template.dto.CaracteristicaVehiculoDTO;

@Repository
public class CaracteristicaVehiculoDao extends BaseApiDao<CaracteristicaVehiculoDTO, String> {

    public CaracteristicaVehiculoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/caracteristicas-vehiculo";
    }

    @Override
    protected Class<CaracteristicaVehiculoDTO> getEntityClass() {
        return CaracteristicaVehiculoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<CaracteristicaVehiculoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<CaracteristicaVehiculoDTO>>() {};
    }
}
