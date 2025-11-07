package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Template DAO in charge of orchestrating REST calls against the Greedy server.
 * Concrete DAOs only provide the resource path and the type information while
 * the template handles common boilerplate (URLs, headers, exception mapping).
 *
 * @param <T>  payload type
 * @param <ID> identifier type
 */
public abstract class BaseApiDao<T, ID> {

    private static final Logger log = LoggerFactory.getLogger(BaseApiDao.class);

    protected final RestTemplate restTemplate;
    protected final String baseUrl;

    protected BaseApiDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getBaseUrl();
    }

    protected abstract String getResourcePath();

    protected abstract Class<T> getEntityClass();

    protected abstract ParameterizedTypeReference<List<T>> getListTypeReference();

    protected String collectionUrl() {
        return baseUrl + getResourcePath();
    }

    protected String entityUrl(ID id) {
        return collectionUrl() + "/" + id;
    }

    protected HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    protected <R> HttpEntity<R> buildRequestEntity(R body) {
        return new HttpEntity<>(body, buildHeaders());
    }

    public List<T> findAll() throws ErrorServiceException {
        try {
            ResponseEntity<List<T>> response = restTemplate.exchange(
                collectionUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                getListTypeReference()
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("listar recursos", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar recursos", e);
        }
    }

    public Optional<T> findById(ID id) throws ErrorServiceException {
        if (id == null || id.toString().trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                entityUrl(id),
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                getEntityClass()
            );
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientResponseException e) {
            throw translateException("obtener el recurso con id " + id, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al obtener el recurso con id " + id, e);
        }
    }

    public T create(T payload) throws ErrorServiceException {
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                collectionUrl(),
                HttpMethod.POST,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw translateException("crear el recurso", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al crear el recurso", e);
        }
    }

    public Optional<T> update(ID id, T payload) throws ErrorServiceException {
        if (id == null || id.toString().trim().isEmpty()) {
            throw new ErrorServiceException("El ID del recurso no puede estar vacío para actualizar");
        }
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                entityUrl(id),
                HttpMethod.PUT,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientResponseException e) {
            throw translateException("actualizar el recurso con id " + id, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al actualizar el recurso con id " + id, e);
        }
    }

    public void delete(ID id) throws ErrorServiceException {
        if (id == null || id.toString().trim().isEmpty()) {
            throw new ErrorServiceException("El ID del recurso no puede estar vacío para eliminar");
        }
        try {
            restTemplate.exchange(
                entityUrl(id),
                HttpMethod.DELETE,
                new HttpEntity<>(null, buildHeaders()),
                Void.class
            );
        } catch (RestClientResponseException e) {
            throw translateException("eliminar el recurso con id " + id, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al eliminar el recurso con id " + id, e);
        }
    }

    protected ErrorServiceException translateException(String operation, RestClientResponseException e) {
        log.error("Error al {}. Status: {}, body: {}", operation, e.getRawStatusCode(), e.getResponseBodyAsString());
        String message = e.getResponseBodyAsString();
        if (message == null || message.isBlank()) {
            message = "Error del servidor remoto al " + operation;
        }
        return new ErrorServiceException(message, e);
    }
}
