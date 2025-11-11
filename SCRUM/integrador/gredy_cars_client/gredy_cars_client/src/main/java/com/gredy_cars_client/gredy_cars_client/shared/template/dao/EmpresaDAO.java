package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpresaDTO;

/**
 * DAO concreto que consume los endpoints REST de empresas.
 */
@Repository
public class EmpresaDAO extends BaseApiDao<EmpresaDTO, String> {

    public EmpresaDAO(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/empresas";
    }

    @Override
    protected Class<EmpresaDTO> getEntityClass() {
        return EmpresaDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<EmpresaDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<EmpresaDTO>>() {};
    }
}
