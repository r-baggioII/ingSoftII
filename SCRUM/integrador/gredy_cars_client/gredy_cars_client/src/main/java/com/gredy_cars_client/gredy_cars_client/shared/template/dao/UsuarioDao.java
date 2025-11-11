package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.UsuarioDTO;

/**
 * DAO concreto que consume los endpoints REST de Usuario.
 */
@Repository
public class UsuarioDao extends BaseApiDao<UsuarioDTO, String> {

    public UsuarioDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/usuarios";
    }

    @Override
    protected Class<UsuarioDTO> getEntityClass() {
        return UsuarioDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<UsuarioDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<UsuarioDTO>>() {};
    }
}
