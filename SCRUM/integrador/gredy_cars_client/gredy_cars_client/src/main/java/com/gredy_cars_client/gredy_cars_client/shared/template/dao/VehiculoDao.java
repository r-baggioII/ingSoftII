package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.VehiculoDTO;

/**
 * DAO concreto que consume los endpoints REST de Vehiculo.
 */
@Repository
public class VehiculoDao extends BaseApiDao<VehiculoDTO, String> {

    public VehiculoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/vehiculos";
    }

    @Override
    protected Class<VehiculoDTO> getEntityClass() {
        return VehiculoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<VehiculoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<VehiculoDTO>>() {};
    }
}

