package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * DAO concreto que consume los endpoints REST de Cliente.
 */
@Repository
public class ClienteDao extends BaseApiDao<ClienteDTO, String> {

    public ClienteDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/clientes";
    }

    @Override
    protected Class<ClienteDTO> getEntityClass() {
        return ClienteDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<ClienteDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<ClienteDTO>>() {};
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
    public ClienteDTO create(ClienteDTO payload) throws com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException {
        try {
            org.springframework.http.ResponseEntity<ClienteDTO> response = restTemplate.exchange(
                collectionUrl() + "/new",
                org.springframework.http.HttpMethod.POST,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return response.getBody();
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw translateException("crear el cliente", e);
        } catch (org.springframework.web.client.RestClientException e) {
            throw new com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException("Error de comunicación al crear el cliente", e);
        }
    }

    @Override
    public java.util.Optional<ClienteDTO> update(String id, ClienteDTO payload) throws com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException {
        if (id == null || id.trim().isEmpty()) {
            throw new com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException("El ID del cliente no puede estar vacío para actualizar");
        }
        try {
            org.springframework.http.ResponseEntity<ClienteDTO> response = restTemplate.exchange(
                collectionUrl() + "/update/" + id,
                org.springframework.http.HttpMethod.PUT,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return java.util.Optional.ofNullable(response.getBody());
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return java.util.Optional.empty();
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw translateException("actualizar el cliente con id " + id, e);
        } catch (org.springframework.web.client.RestClientException e) {
            throw new com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException("Error de comunicación al actualizar el cliente con id " + id, e);
        }
    }

    public List<AlquilerDTO> obtenerAlquileresPorCliente(String clienteId) throws ErrorServiceException {
        if (clienteId == null || clienteId.isBlank()) {
            return Collections.emptyList();
        }
        String url = collectionUrl() + "/" + clienteId + "/alquileres";
        try {
            org.springframework.http.ResponseEntity<List<AlquilerDTO>> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(null, buildHeaders()),
                new ParameterizedTypeReference<List<AlquilerDTO>>() {}
            );
            return java.util.Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (HttpClientErrorException.NotFound e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw translateException("listar alquileres del cliente " + clienteId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al obtener los alquileres del cliente " + clienteId, e);
        }
    }

    public List<AlquilerDTO> obtenerAlquileresPendientesFactura(String clienteId) throws ErrorServiceException {
        if (clienteId == null || clienteId.isBlank()) {
            return Collections.emptyList();
        }
        String url = collectionUrl() + "/" + clienteId + "/alquileres/pendientes-factura";
        try {
            org.springframework.http.ResponseEntity<List<AlquilerDTO>> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(null, buildHeaders()),
                new ParameterizedTypeReference<List<AlquilerDTO>>() {}
            );
            return java.util.Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (HttpClientErrorException.NotFound e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw translateException("listar alquileres pendientes de facturación del cliente " + clienteId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al obtener los alquileres pendientes de facturación del cliente " + clienteId, e);
        }
    }

    public List<ClienteDTO> buscarPorQuery(String query) throws ErrorServiceException {
        if (!StringUtils.hasText(query)) {
            return findAll();
        }
        String url = UriComponentsBuilder.fromHttpUrl(collectionUrl())
                .path("/search")
                .queryParam("query", query.trim())
                .toUriString();
        try {
            org.springframework.http.ResponseEntity<List<ClienteDTO>> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(null, buildHeaders()),
                getListTypeReference()
            );
            return java.util.Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("buscar clientes por '" + query + "'", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al buscar clientes", e);
        }
    }
}
