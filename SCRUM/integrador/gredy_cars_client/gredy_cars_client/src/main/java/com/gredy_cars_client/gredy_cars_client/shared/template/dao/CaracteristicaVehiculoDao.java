package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CaracteristicaVehiculoDTO;

/**
 * DAO concreto que consume los endpoints REST de CaracteristicaVehiculo.
 */
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

