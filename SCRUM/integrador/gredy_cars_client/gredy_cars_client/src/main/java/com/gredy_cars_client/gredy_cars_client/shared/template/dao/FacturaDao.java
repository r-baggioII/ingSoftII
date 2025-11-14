package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DetalleFacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FormaDePagoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * DAO encargado de consumir los endpoints REST de factura.
 */
@Repository
public class FacturaDao extends BaseApiDao<FacturaDTO, String> {

    private static final ParameterizedTypeReference<List<DetalleFacturaDTO>> DETALLE_LIST_TYPE =
        new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<FormaDePagoDTO>> FORMA_PAGO_LIST_TYPE =
        new ParameterizedTypeReference<>() {};

    public FacturaDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/facturas";
    }

    @Override
    protected Class<FacturaDTO> getEntityClass() {
        return FacturaDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<FacturaDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<>() {};
    }

    @Override
    public FacturaDTO create(FacturaDTO payload) throws ErrorServiceException {
        try {
            ResponseEntity<FacturaDTO> response = restTemplate.exchange(
                dtoCollectionUrl(),
                HttpMethod.POST,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw translateException("crear la factura", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al crear la factura", e);
        }
    }

    @Override
    public Optional<FacturaDTO> update(String id, FacturaDTO payload) throws ErrorServiceException {
        if (!StringUtils.hasText(id)) {
            throw new ErrorServiceException("El id de la factura es obligatorio para modificar");
        }
        try {
            ResponseEntity<FacturaDTO> response = restTemplate.exchange(
                dtoEntityUrl(id),
                HttpMethod.PUT,
                buildRequestEntity(payload),
                getEntityClass()
            );
            return Optional.ofNullable(response.getBody());
        } catch (RestClientResponseException e) {
            throw translateException("actualizar la factura con id " + id, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al actualizar la factura con id " + id, e);
        }
    }

    public List<FacturaDTO> findByEstado(String estado) throws ErrorServiceException {
        if (!StringUtils.hasText(estado)) {
            return findAll();
        }
        String normalized = estado.trim().toUpperCase();
        try {
            ResponseEntity<List<FacturaDTO>> response = restTemplate.exchange(
                collectionUrl() + "/estado/" + normalized,
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                getListTypeReference()
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("listar facturas por estado " + normalized, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar facturas por estado " + normalized, e);
        }
    }

    public List<DetalleFacturaDTO> findDetallesByFactura(String facturaId) throws ErrorServiceException {
        validateFacturaId(facturaId);
        try {
            ResponseEntity<List<DetalleFacturaDTO>> response = restTemplate.exchange(
                collectionUrl() + "/" + facturaId + "/detalles",
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                DETALLE_LIST_TYPE
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("obtener los detalles para la factura " + facturaId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al obtener los detalles de la factura " + facturaId, e);
        }
    }

    public List<FormaDePagoDTO> findFormasPagoByFactura(String facturaId) throws ErrorServiceException {
        validateFacturaId(facturaId);
        try {
            ResponseEntity<List<FormaDePagoDTO>> response = restTemplate.exchange(
                collectionUrl() + "/" + facturaId + "/formas-pago",
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                FORMA_PAGO_LIST_TYPE
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("obtener formas de pago para la factura " + facturaId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al obtener formas de pago de la factura " + facturaId, e);
        }
    }

    public List<FacturaDTO> findByCliente(String clienteId) throws ErrorServiceException {
        if (!StringUtils.hasText(clienteId)) {
            return Collections.emptyList();
        }
        String url = UriComponentsBuilder.fromHttpUrl(collectionUrl())
                .queryParam("clienteId", clienteId.trim())
                .toUriString();
        try {
            ResponseEntity<List<FacturaDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                getListTypeReference()
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("listar facturas del cliente " + clienteId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar facturas del cliente " + clienteId, e);
        }
    }

    public List<FacturaDTO> findByUsuario(String usuarioId) throws ErrorServiceException {
        if (!StringUtils.hasText(usuarioId)) {
            return Collections.emptyList();
        }
        String url = UriComponentsBuilder.fromHttpUrl(collectionUrl())
                .queryParam("usuarioId", usuarioId.trim())
                .toUriString();
        try {
            ResponseEntity<List<FacturaDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                getListTypeReference()
            );
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        } catch (RestClientResponseException e) {
            throw translateException("listar facturas del usuario " + usuarioId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al listar facturas del usuario " + usuarioId, e);
        }
    }

    public byte[] descargarPdf(String facturaId) throws ErrorServiceException {
        validateFacturaId(facturaId);
        HttpHeaders headers = buildHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                collectionUrl() + "/" + facturaId + "/pdf",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                byte[].class
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw translateException("descargar el PDF de la factura " + facturaId, e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al descargar el PDF de la factura " + facturaId, e);
        }
    }

    private String dtoCollectionUrl() {
        return collectionUrl() + "/dto";
    }

    private String dtoEntityUrl(String id) {
        return dtoCollectionUrl() + "/" + id;
    }

    private void validateFacturaId(String facturaId) throws ErrorServiceException {
        if (!StringUtils.hasText(facturaId)) {
            throw new ErrorServiceException("Debe indicar el id de la factura");
        }
    }
}
