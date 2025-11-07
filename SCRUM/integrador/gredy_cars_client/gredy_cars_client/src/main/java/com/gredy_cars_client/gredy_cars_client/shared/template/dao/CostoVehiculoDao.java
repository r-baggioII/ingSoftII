package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CostoVehiculoDTO;

/**
 * DAO concreto que consume los endpoints REST de CostoVehiculo.
 */
@Repository
public class CostoVehiculoDao extends BaseApiDao<CostoVehiculoDTO, String> {

    public CostoVehiculoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        System.err.println("CostoVehiculoDao.getResourcePath() -> /costos-vehiculo");
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
}

