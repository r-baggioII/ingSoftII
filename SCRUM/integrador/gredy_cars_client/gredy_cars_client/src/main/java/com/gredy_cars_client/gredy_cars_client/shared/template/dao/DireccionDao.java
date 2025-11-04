package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;

/**
 * DAO concreto que consume los endpoints REST de Dirección.
 */
@Repository
public class DireccionDao extends BaseApiDao<DireccionDTO, Long> {

    public DireccionDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/direcciones";
    }

    @Override
    protected Class<DireccionDTO> getEntityClass() {
        return DireccionDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<DireccionDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<DireccionDTO>>() {};
    }
}
