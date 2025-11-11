package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PersonaDTO;

/**
 * DAO concreto que consume los endpoints REST de Persona.
 */
@Repository
public class PersonaDao extends BaseApiDao<PersonaDTO, String> {

    public PersonaDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/personas";
    }

    @Override
    protected Class<PersonaDTO> getEntityClass() {
        return PersonaDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<PersonaDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<PersonaDTO>>() {};
    }

    @Override
    protected org.springframework.http.HttpHeaders buildHeaders() {
        org.springframework.http.HttpHeaders headers = super.buildHeaders();
        // Reenviar cookie JWT al backend (autenticación)
        org.springframework.web.context.request.ServletRequestAttributes attrs = (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            jakarta.servlet.http.HttpServletRequest request = attrs.getRequest();
            String cookieHeader = request.getHeader(org.springframework.http.HttpHeaders.COOKIE);
            if (cookieHeader != null) {
                headers.add(org.springframework.http.HttpHeaders.COOKIE, cookieHeader);
            }
        }
        return headers;
    }
}
