package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DepartamentoDTO;

/**
 * DAO concreto que consume los endpoints REST de Departamento.
 */
@Repository
public class DepartamentoDao extends BaseApiDao<DepartamentoDTO, Long> {

    public DepartamentoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/departamentos";
    }

    @Override
    protected Class<DepartamentoDTO> getEntityClass() {
        return DepartamentoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<DepartamentoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<DepartamentoDTO>>() {};
    }
}
