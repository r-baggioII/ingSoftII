package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsAppService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppService.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final GreedyApiProperties properties;

    public WhatsAppService(RestTemplate restTemplate, GreedyApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void enviarRecordatorioManual(String alquilerId) throws ErrorServiceException {
        if (!StringUtils.hasText(alquilerId)) {
            throw new ErrorServiceException("Debe seleccionar un alquiler para enviar el recordatorio");
        }
        String url = properties.getBaseUrl() + "/alquileres/" + alquilerId + "/recordatorio-whatsapp";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.postForEntity(url, new HttpEntity<>(null, headers), Void.class);
        } catch (RestClientResponseException e) {
            String detalle = extraerMensajeError(e);
            LOGGER.error("Error del backend al enviar recordatorio: {}", detalle);
            throw new ErrorServiceException(detalle, e);
        } catch (RestClientException e) {
            LOGGER.error("No se pudo comunicar con el backend para enviar el recordatorio", e);
            throw new ErrorServiceException("No se pudo comunicar con el backend para enviar el recordatorio", e);
        }
    }

    private String extraerMensajeError(RestClientResponseException exception) {
        String body = exception.getResponseBodyAsString();
        if (!StringUtils.hasText(body)) {
            return "El backend rechazó el recordatorio de WhatsApp";
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(body);
            if (node.has("error")) {
                return node.get("error").asText();
            }
            if (node.has("mensaje")) {
                return node.get("mensaje").asText();
            }
        } catch (IOException ignored) {
        }
        return body;
    }
}
