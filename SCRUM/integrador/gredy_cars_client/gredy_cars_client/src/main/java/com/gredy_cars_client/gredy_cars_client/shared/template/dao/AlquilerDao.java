package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;

/**
 * DAO concreto que consume los endpoints REST de Alquiler.
 */
@Repository
public class AlquilerDao extends BaseApiDao<AlquilerDTO, String> {

    public AlquilerDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/alquileres";
    }

    @Override
    protected Class<AlquilerDTO> getEntityClass() {
        return AlquilerDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<AlquilerDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<AlquilerDTO>>() {};
    }
}