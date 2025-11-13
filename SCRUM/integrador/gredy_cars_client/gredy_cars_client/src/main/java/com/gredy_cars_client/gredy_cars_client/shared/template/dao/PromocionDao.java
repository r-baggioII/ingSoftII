package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PromocionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.StringUtils;

@Component
public class PromocionDao extends BaseApiDao<PromocionDTO, String> {

    private static final ParameterizedTypeReference<List<PromocionDTO>> LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    public PromocionDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

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
    protected HttpHeaders buildHeaders() {
        HttpHeaders headers = super.buildHeaders();
        var attrs = (org.springframework.web.context.request.ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String cookieHeader = attrs.getRequest().getHeader(HttpHeaders.COOKIE);
            if (StringUtils.hasText(cookieHeader)) {
                headers.add(HttpHeaders.COOKIE, cookieHeader);
                String jwt = Arrays.stream(cookieHeader.split(";"))
                        .map(String::trim)
                        .filter(cookie -> cookie.startsWith("jwt="))
                        .map(cookie -> cookie.substring("jwt=".length()))
                        .findFirst()
                        .orElse(null);
                if (StringUtils.hasText(jwt)) {
                    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                }
            }
        }
        return headers;
    }

    @Override
    public PromocionDTO create(PromocionDTO payload) throws ErrorServiceException {
        try {
            ResponseEntity<PromocionDTO> response = restTemplate.exchange(
                    collectionUrl() + "/dto",
                    HttpMethod.POST,
                    buildRequestEntity(payload),
                    PromocionDTO.class
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
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
