package com.greedy_cars_institucional.institucional.shared.template.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.greedy_cars_institucional.institucional.config.GreedyApiProperties;
import com.greedy_cars_institucional.institucional.shared.template.dto.CostoVehiculoDTO;
import com.greedy_cars_institucional.institucional.shared.template.exception.ErrorServiceException;

@Repository
public class CostoVehiculoDao extends BaseApiDao<CostoVehiculoDTO, String> {

    private final RestTemplate restTemplate;
    private final GreedyApiProperties properties;

    public CostoVehiculoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    protected String getResourcePath() {
        return "/costos-vehiculo";
    }

    @Override
    protected Class<CostoVehiculoDTO> getEntityClass() {
        return CostoVehiculoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<CostoVehiculoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<CostoVehiculoDTO>>() {};
    }

    public Optional<CostoVehiculoDTO> buscarCostoVigente(String caracteristicaId) throws ErrorServiceException {
        String url = properties.buildUrl(getResourcePath() + "/vigente/" + caracteristicaId);
        try {
            ResponseEntity<CostoVehiculoDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, buildHeaders()),
                CostoVehiculoDTO.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientResponseException e) {
            throw new ErrorServiceException("Error del servidor al consultar costo vigente", e);
        } catch (RestClientException e) {
            throw new ErrorServiceException("Error de comunicación al consultar costo vigente", e);
        }
    }
}
