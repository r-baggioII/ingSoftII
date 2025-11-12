package com.uncuyo.greedy_cars.shared.template.dao;

import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceRequest;
import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceResponse;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PagoMpDao {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PagoMpDao(RestTemplateBuilder restTemplateBuilder,
                     @Value("${greedy.api.base-url:http://localhost:9000/greedy_cars}") String baseUrl) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
        this.baseUrl = normalizarBaseUrl(baseUrl);
    }

    public MercadoPagoPreferenceResponse crearPreferencia(MercadoPagoPreferenceRequest request) throws ErrorServiceException {
        try {
            ResponseEntity<MercadoPagoPreferenceResponse> response = restTemplate.postForEntity(
                    baseUrl + "/api/pagos/mp/preferencia",
                    request,
                    MercadoPagoPreferenceResponse.class);

            if (response == null || response.getBody() == null) {
                throw new ErrorServiceException("No se recibió respuesta de la API de pagos");
            }
            return response.getBody();
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error al invocar la API de pagos", e);
        }
    }

    private String normalizarBaseUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return "";
        }
        if (rawUrl.endsWith("/")) {
            return rawUrl.substring(0, rawUrl.length() - 1);
        }
        return rawUrl;
    }
}
