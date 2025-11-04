package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ProvinciaDTO;

/**
 * DAO concreto que consume los endpoints REST de Provincia.
 */
@Repository
public class ProvinciaDao extends BaseApiDao<ProvinciaDTO, Long> {

    public ProvinciaDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/provincias";
    }

    @Override
    protected Class<ProvinciaDTO> getEntityClass() {
        return ProvinciaDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<ProvinciaDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<ProvinciaDTO>>() {};
    }
}
