package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PaisDTO;

/**
 * DAO concreto que consume los endpoints REST de País.
 */
@Repository
public class PaisDao extends BaseApiDao<PaisDTO, Long> {

    public PaisDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/paises";
    }

    @Override
    protected Class<PaisDTO> getEntityClass() {
        return PaisDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<PaisDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<PaisDTO>>() {};
    }
}

