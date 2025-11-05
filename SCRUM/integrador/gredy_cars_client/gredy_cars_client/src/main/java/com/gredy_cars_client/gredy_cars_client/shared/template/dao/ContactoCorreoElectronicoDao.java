package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoCorreoElectronicoDTO;

/**
 * DAO concreto que consume los endpoints REST de correo electrónico de contacto.
 */
@Repository
public class ContactoCorreoElectronicoDao extends BaseApiDao<ContactoCorreoElectronicoDTO, String> {

    public ContactoCorreoElectronicoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/contactos-correos";
    }

    @Override
    protected Class<ContactoCorreoElectronicoDTO> getEntityClass() {
        return ContactoCorreoElectronicoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<ContactoCorreoElectronicoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<ContactoCorreoElectronicoDTO>>() {};
    }
}
