package com.uncuyo.greedy_cars_web.rest;

import com.uncuyo.greedy_cars_web.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Clase base para DAOs REST
 * Provee métodos comunes para hacer peticiones HTTP a la API backend
 * 
 * @param <T> Tipo del DTO a manejar
 */
public abstract class BaseDAORest<T> {

    @Autowired
    protected RestTemplate restTemplate;

    @Value("${api.base.url}")
    protected String apiBaseUrl;

    /**
     * Método abstracto que debe implementar cada DAO hijo
     * para definir la ruta específica del recurso
     * Ejemplo: "/api/v1/vehiculos"
     */
    protected abstract String getResourcePath();

    /**
     * Obtiene la URL completa del recurso
     */
    protected String getFullUrl(String endpoint) {
        return apiBaseUrl + getResourcePath() + endpoint;
    }

    /**
     * GET: Obtener lista de recursos
     */
    protected ResponseEntity<List<T>> getList(String endpoint, ParameterizedTypeReference<List<T>> responseType) {
        try {
            return restTemplate.exchange(
                    getFullUrl(endpoint),
                    HttpMethod.GET,
                    null,
                    responseType
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ApiException("Error al obtener lista: " + e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new ApiException("Error de conexión con la API: " + e.getMessage(), e);
        }
    }

    /**
     * GET: Obtener un recurso por ID
     */
    protected ResponseEntity<T> getOne(String id, Class<T> responseType) {
        try {
            return restTemplate.getForEntity(
                    getFullUrl("/" + id),
                    responseType
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ApiException("Error al obtener recurso: " + e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new ApiException("Error de conexión con la API: " + e.getMessage(), e);
        }
    }

    /**
     * POST: Crear un nuevo recurso
     */
    protected ResponseEntity<T> post(T dto, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<T> request = new HttpEntity<>(dto, headers);
            
            return restTemplate.postForEntity(
                    getFullUrl(""),
                    request,
                    responseType
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ApiException("Error al crear recurso: " + e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new ApiException("Error de conexión con la API: " + e.getMessage(), e);
        }
    }

    /**
     * PUT: Actualizar un recurso existente
     */
    protected ResponseEntity<T> put(String id, T dto, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<T> request = new HttpEntity<>(dto, headers);
            
            return restTemplate.exchange(
                    getFullUrl("/" + id),
                    HttpMethod.PUT,
                    request,
                    responseType
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ApiException("Error al actualizar recurso: " + e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new ApiException("Error de conexión con la API: " + e.getMessage(), e);
        }
    }

    /**
     * DELETE: Eliminar un recurso
     */
    protected void delete(String id) {
        try {
            restTemplate.delete(getFullUrl("/" + id));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ApiException("Error al eliminar recurso: " + e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new ApiException("Error de conexión con la API: " + e.getMessage(), e);
        }
    }
}
