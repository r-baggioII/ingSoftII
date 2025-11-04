package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.NacionalidadDTO;

/**
 * DAO concreto que consume los endpoints REST de Nacionalidad.
 */
@Repository
public class NacionalidadDao extends BaseApiDao<NacionalidadDTO, String> {

    public NacionalidadDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/nacionalidades";
    }

    @Override
    protected Class<NacionalidadDTO> getEntityClass() {
        return NacionalidadDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<NacionalidadDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<NacionalidadDTO>>() {};
    }
}

