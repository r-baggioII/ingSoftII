package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FormaDePagoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * DAO para consumir los endpoints de forma de pago.
 */
@Repository
public class FormaDePagoDao extends BaseApiDao<FormaDePagoDTO, String> {

    public FormaDePagoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/formas-pago";
    }

    @Override
    protected Class<FormaDePagoDTO> getEntityClass() {
        return FormaDePagoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<FormaDePagoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<>() {};
    }

    @Override
    public FormaDePagoDTO create(FormaDePagoDTO payload) throws ErrorServiceException {
        try {
            ResponseEntity<FormaDePagoDTO> response = restTemplate.exchange(
                dtoCollectionUrl(),
                HttpMethod.POST,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw translateException("crear la forma de pago", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al crear la forma de pago", e);
        }
    }

    @Override
    public Optional<FormaDePagoDTO> update(String id, FormaDePagoDTO payload) throws ErrorServiceException {
        if (!StringUtils.hasText(id)) {
            throw new ErrorServiceException("El id de la forma de pago es obligatorio para modificar");
        }
        try {
            ResponseEntity<FormaDePagoDTO> response = restTemplate.exchange(
                dtoEntityUrl(id),
                HttpMethod.PUT,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return Optional.ofNullable(response.getBody());
        } catch (RestClientResponseException e) {
            throw translateException("actualizar la forma de pago con id " + id, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al actualizar la forma de pago con id " + id, e);
        }
    }

    public List<FormaDePagoDTO> findByFactura(String facturaId) throws ErrorServiceException {
        if (!StringUtils.hasText(facturaId)) {
            throw new ErrorServiceException("Debe indicar el id de la factura");
        }
        try {
            ResponseEntity<List<FormaDePagoDTO>> response = restTemplate.exchange(
                collectionUrl() + "/factura/" + facturaId.trim(),
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                getListTypeReference()
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("listar formas de pago de la factura " + facturaId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar formas de pago de la factura " + facturaId, e);
        }
    }

    private String dtoCollectionUrl() {
        return collectionUrl() + "/dto";
    }

    private String dtoEntityUrl(String id) {
        return dtoCollectionUrl() + "/" + id;
    }
}
