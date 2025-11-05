package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoTelefonicoDTO;

/**
 * DAO para consumir los endpoints REST de contactos telefónicos.
 */
@Repository
public class ContactoTelefonicoDao extends BaseApiDao<ContactoTelefonicoDTO, String> {

    public ContactoTelefonicoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/contactos-telefonicos";
    }

    @Override
    protected Class<ContactoTelefonicoDTO> getEntityClass() {
        return ContactoTelefonicoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<ContactoTelefonicoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<ContactoTelefonicoDTO>>() {};
    }
}

