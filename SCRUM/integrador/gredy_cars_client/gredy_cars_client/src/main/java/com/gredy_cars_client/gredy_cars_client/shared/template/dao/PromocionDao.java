package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PromocionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


@Component
public class PromocionDao extends BaseApiDao<PromocionDTO, String> {

    private static final ParameterizedTypeReference<List<PromocionDTO>> LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    public PromocionDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PromocionDao.class);

    @Override
    protected String getResourcePath() {
        return "/promociones";
    }

    @Override
    protected Class<PromocionDTO> getEntityClass() {
        return PromocionDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<PromocionDTO>> getListTypeReference() {
        return LIST_TYPE;
    }

    @Override
    public List<PromocionDTO> findAll() throws ErrorServiceException {
        String url = collectionUrl();
        HttpHeaders headers = buildHeaders();
        LOGGER.debug("Solicitando promociones: {}", url);
        try {
            ResponseEntity<List<PromocionDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(null, headers),
                    LIST_TYPE
            );
            List<PromocionDTO> body = Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
            LOGGER.debug("Promociones recibidas: {} elementos", body.size());
            return body;
        } catch (RestClientResponseException e) {
            LOGGER.warn("Error remoto al listar promociones: status={} body={}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw translateException("listar promociones", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar promociones", e);
        }
    }


    @Override
    public PromocionDTO create(PromocionDTO payload) throws ErrorServiceException {
        try {
            String url = collectionUrl() + "/dto";
            try {
                String jsonPayload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
                LOGGER.info("Creando promoción vía POST {} con payload: {}", url, jsonPayload);
            } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
                LOGGER.warn("No se pudo serializar la promoción para logging", ex);
            }
            ResponseEntity<PromocionDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    buildRequestEntity(payload),
                    PromocionDTO.class
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            LOGGER.error("Error remoto al crear promoción. Status={}, body={}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw translateException("crear la promoción", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al crear la promoción", e);
        }
    }

    @Override
    public Optional<PromocionDTO> update(String id, PromocionDTO payload) throws ErrorServiceException {
        try {
            ResponseEntity<PromocionDTO> response = restTemplate.exchange(
                    collectionUrl() + "/dto/" + id,
                    HttpMethod.PUT,
                    buildRequestEntity(payload),
                    PromocionDTO.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (RestClientResponseException e) {
            throw translateException("actualizar la promoción " + id, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al actualizar la promoción", e);
        }
    }

    public List<PromocionDTO> listarVigentes() throws ErrorServiceException {
        try {
            ResponseEntity<List<PromocionDTO>> response = restTemplate.exchange(
                    collectionUrl() + "/vigentes",
                    HttpMethod.GET,
                    new HttpEntity<>(null, buildHeaders()),
                    LIST_TYPE
            );
            return Optional.ofNullable(response.getBody()).orElseGet(java.util.Collections::emptyList);
        } catch (RestClientResponseException e) {
            throw translateException("listar promociones vigentes", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar promociones vigentes", e);
        }
    }
}
