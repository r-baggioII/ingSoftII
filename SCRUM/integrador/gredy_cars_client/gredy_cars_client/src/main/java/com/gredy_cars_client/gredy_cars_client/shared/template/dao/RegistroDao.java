package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.RegistroClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * DAO para consumir el endpoint público de registro de clientes.
 * No requiere autenticación JWT.
 */
@Repository
public class RegistroDao {

    private final RestTemplate restTemplate;
    private final GreedyApiProperties properties;

    public RegistroDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * Registra un nuevo cliente en el sistema.
     * Este endpoint es público y no requiere autenticación.
     *
     * @param dto datos del registro
     * @return respuesta del servidor con información del cliente creado
     * @throws ErrorServiceException si ocurre algún error
     */
    public Map<String, Object> registrarCliente(RegistroClienteDTO dto) throws ErrorServiceException {
        try {
            String url = properties.getBaseUrl() + "/registro";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<RegistroClienteDTO> requestEntity = new HttpEntity<>(dto, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );
            
            return response.getBody();
            
        } catch (RestClientResponseException e) {
            // Extraer mensaje de error del servidor
            String errorMessage = extractErrorMessage(e);
            throw new ErrorServiceException("Error al registrar cliente: " + errorMessage);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación con el servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si un nombre de usuario está disponible.
     *
     * @param nombreUsuario el nombre de usuario a verificar
     * @return mapa con la disponibilidad del usuario
     * @throws ErrorServiceException si ocurre algún error
     */
    public Map<String, Object> verificarDisponibilidadUsuario(String nombreUsuario) throws ErrorServiceException {
        try {
            String url = properties.getBaseUrl() + "/registro/verificar-usuario/" + nombreUsuario;
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            return response.getBody();
            
        } catch (RestClientResponseException e) {
            String errorMessage = extractErrorMessage(e);
            throw new ErrorServiceException("Error al verificar usuario: " + errorMessage);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación con el servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae el mensaje de error de una excepción del cliente REST
     */
    private String extractErrorMessage(RestClientResponseException e) {
        try {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isEmpty()) {
                // Intentar extraer el mensaje del JSON
                if (responseBody.contains("\"message\"")) {
                    int start = responseBody.indexOf("\"message\":\"") + 11;
                    int end = responseBody.indexOf("\"", start);
                    if (start > 10 && end > start) {
                        return responseBody.substring(start, end);
                    }
                }
                return responseBody;
            }
            return e.getMessage();
        } catch (Exception ex) {
            return e.getMessage();
        }
    }
}
