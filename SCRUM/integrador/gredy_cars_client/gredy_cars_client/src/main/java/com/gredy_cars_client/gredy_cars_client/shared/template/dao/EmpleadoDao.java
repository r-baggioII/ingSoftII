package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpleadoDTO;

/**
 * DAO concreto que consume los endpoints REST de Empleado.
 */
@Repository
public class EmpleadoDao extends BaseApiDao<EmpleadoDTO, String> {

    public EmpleadoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/empleados";
    }

    @Override
    protected Class<EmpleadoDTO> getEntityClass() {
        return EmpleadoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<EmpleadoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<EmpleadoDTO>>() {};
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

    @Override
    public EmpleadoDTO create(EmpleadoDTO payload) throws com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException {
        try {
            org.springframework.http.ResponseEntity<EmpleadoDTO> response = restTemplate.exchange(
                collectionUrl() + "/new",
                org.springframework.http.HttpMethod.POST,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return response.getBody();
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw translateException("crear el empleado", e);
        } catch (org.springframework.web.client.RestClientException e) {
            throw new com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException("Error de comunicación al crear el empleado", e);
        }
    }

    @Override
    public java.util.Optional<EmpleadoDTO> update(String id, EmpleadoDTO payload) throws com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException {
        if (id == null || id.trim().isEmpty()) {
            throw new com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException("El ID del empleado no puede estar vacío para actualizar");
        }
        try {
            org.springframework.http.ResponseEntity<EmpleadoDTO> response = restTemplate.exchange(
                collectionUrl() + "/update/" + id,
                org.springframework.http.HttpMethod.PUT,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return java.util.Optional.ofNullable(response.getBody());
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return java.util.Optional.empty();
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw translateException("actualizar el empleado con id " + id, e);
        } catch (org.springframework.web.client.RestClientException e) {
            throw new com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException("Error de comunicación al actualizar el empleado con id " + id, e);
        }
    }
}
