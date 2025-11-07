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
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DetalleFacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * DAO para consumir los endpoints del recurso DetalleFactura.
 */
@Repository
public class DetalleFacturaDao extends BaseApiDao<DetalleFacturaDTO, String> {

    public DetalleFacturaDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/detalles-factura";
    }

    @Override
    protected Class<DetalleFacturaDTO> getEntityClass() {
        return DetalleFacturaDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<DetalleFacturaDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<>() {};
    }

    @Override
    public DetalleFacturaDTO create(DetalleFacturaDTO payload) throws ErrorServiceException {
        try {
            ResponseEntity<DetalleFacturaDTO> response = restTemplate.exchange(
                dtoCollectionUrl(),
                HttpMethod.POST,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw translateException("crear el detalle de factura", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al crear el detalle de factura", e);
        }
    }

    @Override
    public Optional<DetalleFacturaDTO> update(String id, DetalleFacturaDTO payload) throws ErrorServiceException {
        if (!StringUtils.hasText(id)) {
            throw new ErrorServiceException("El id del detalle de factura es obligatorio para modificar");
        }
        try {
            ResponseEntity<DetalleFacturaDTO> response = restTemplate.exchange(
                dtoEntityUrl(id),
                HttpMethod.PUT,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return Optional.ofNullable(response.getBody());
        } catch (RestClientResponseException e) {
            throw translateException("actualizar el detalle de factura con id " + id, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al actualizar el detalle de factura con id " + id, e);
        }
    }

    public List<DetalleFacturaDTO> findByFactura(String facturaId) throws ErrorServiceException {
        if (!StringUtils.hasText(facturaId)) {
            throw new ErrorServiceException("Debe indicar el id de la factura");
        }
        try {
            ResponseEntity<List<DetalleFacturaDTO>> response = restTemplate.exchange(
                collectionUrl() + "/factura/" + facturaId.trim(),
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                getListTypeReference()
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("listar detalles por factura " + facturaId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar detalles de la factura " + facturaId, e);
        }
    }

    private String dtoCollectionUrl() {
        return collectionUrl() + "/dto";
    }

    private String dtoEntityUrl(String id) {
        return dtoCollectionUrl() + "/" + id;
    }
}
