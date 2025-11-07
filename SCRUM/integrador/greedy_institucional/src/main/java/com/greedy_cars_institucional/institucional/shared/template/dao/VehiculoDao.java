package com.greedy_cars_institucional.institucional.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.greedy_cars_institucional.institucional.config.GreedyApiProperties;
import com.greedy_cars_institucional.institucional.shared.template.dto.VehiculoDTO;
import com.greedy_cars_institucional.institucional.shared.template.exception.ErrorServiceException;

@Repository
public class VehiculoDao extends BaseApiDao<VehiculoDTO, String> {

    public VehiculoDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/vehiculos";
    }

    @Override
    protected Class<VehiculoDTO> getEntityClass() {
        return VehiculoDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<VehiculoDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<VehiculoDTO>>() {};
    }

    public long countByCaracteristicaId(String caracteristicaId) throws ErrorServiceException {
        try {
            List<VehiculoDTO> vehicles = findAll();

            return vehicles.stream()
                .filter(v -> v.getCaracteristicaVehiculo() != null)
                .filter(v -> caracteristicaId.equals(v.getCaracteristicaVehiculo().getId()))
                .count();

        } catch (Exception e) {
            throw new ErrorServiceException("Error counting vehicles for characteristic: " + e.getMessage(), e);
        }
    }

    public long countByCaracteristicaIdAndEstado(String caracteristicaId, String estado) throws ErrorServiceException {
        try {
            List<VehiculoDTO> vehicles = findAll();

            return vehicles.stream()
                .filter(v -> v.getCaracteristicaVehiculo() != null)
                .filter(v -> caracteristicaId.equals(v.getCaracteristicaVehiculo().getId()))
                .filter(v -> estado.equals(v.getEstadoVehiculo()))
                .count();

        } catch (Exception e) {
            throw new ErrorServiceException("Error counting vehicles for characteristic: " + e.getMessage(), e);
        }
    }
}