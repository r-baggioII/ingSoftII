package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DocumentacionDTO;

/**
 * DAO encargado de consumir los endpoints REST de documentación.
 */
@Repository
public class DocumentacionDao extends BaseApiDao<DocumentacionDTO, String> {

    public DocumentacionDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/documentacion";
    }

    @Override
    protected Class<DocumentacionDTO> getEntityClass() {
        return DocumentacionDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<DocumentacionDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<DocumentacionDTO>>() {};
    }
}
