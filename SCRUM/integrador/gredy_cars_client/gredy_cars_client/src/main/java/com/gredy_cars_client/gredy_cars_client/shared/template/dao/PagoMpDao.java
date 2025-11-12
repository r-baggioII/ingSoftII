package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.MercadoPagoPreferenceRequest;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.MercadoPagoPreferenceResponse;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

@Repository
public class PagoMpDao {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PagoMpDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getBaseUrl();
    }

    public MercadoPagoPreferenceResponse crearPreferencia(MercadoPagoPreferenceRequest request) throws ErrorServiceException {
        if (request == null) {
            throw new ErrorServiceException("Los datos de la preferencia son obligatorios");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));

        HttpEntity<MercadoPagoPreferenceRequest> entity = new HttpEntity<>(request, headers);
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/pagos/mp/preferencia")
                .toUriString();
        try {
            ResponseEntity<MercadoPagoPreferenceResponse> response =
                    restTemplate.postForEntity(url, entity, MercadoPagoPreferenceResponse.class);
            if (response.getBody() == null) {
                throw new ErrorServiceException("La API de pagos no devolvió una preferencia válida");
            }
            return response.getBody();
        } catch (RestClientResponseException e) {
            String message = e.getResponseBodyAsString() != null
                    ? e.getResponseBodyAsString()
                    : "Respuesta inválida del servidor de pagos";
            throw new ErrorServiceException(message, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("No se pudo contactar con la API de pagos", e);
        }
    }
}
