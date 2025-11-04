package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.LocalidadDTO;

/**
 * DAO concreto que consume los endpoints REST de Localidad.
 */
@Repository
public class LocalidadDao extends BaseApiDao<LocalidadDTO, Long> {

    public LocalidadDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/localidades";
    }

    @Override
    protected Class<LocalidadDTO> getEntityClass() {
        return LocalidadDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<LocalidadDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<LocalidadDTO>>() {};
    }
}
