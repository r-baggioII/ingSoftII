package com.uncuyo.greedy_cars_web.rest;

import com.uncuyo.greedy_cars_web.dto.VehiculoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO REST para consumir endpoints de Vehículo del backend
 * Hace peticiones HTTP a http://localhost:9000/api/v1/vehiculos
 */
@Repository
public class VehiculoDAORest extends BaseDAORest<VehiculoDTO> {

    @Override
    protected String getResourcePath() {
        // Ruta base del recurso en la API backend
        return "/api/v1/vehiculos";
    }

    /**
     * Obtener todos los vehículos activos
     * GET http://localhost:9000/api/v1/vehiculos
     */
    public List<VehiculoDTO> listarTodos() {
        ResponseEntity<List<VehiculoDTO>> response = getList(
                "", 
                new ParameterizedTypeReference<List<VehiculoDTO>>() {}
        );
        return response.getBody();
    }

    /**
     * Obtener vehículos por estado
     * GET http://localhost:9000/api/v1/vehiculos/estado/{estado}
     */
    public List<VehiculoDTO> listarPorEstado(String estado) {
        ResponseEntity<List<VehiculoDTO>> response = getList(
                "/estado/" + estado,
                new ParameterizedTypeReference<List<VehiculoDTO>>() {}
        );
        return response.getBody();
    }

    /**
     * Obtener un vehículo por ID
     * GET http://localhost:9000/api/v1/vehiculos/{id}
     */
    public VehiculoDTO obtenerPorId(String id) {
        ResponseEntity<VehiculoDTO> response = getOne(id, VehiculoDTO.class);
        return response.getBody();
    }

    /**
     * Buscar vehículo por patente
     * GET http://localhost:9000/api/v1/vehiculos/patente/{patente}
     */
    public VehiculoDTO buscarPorPatente(String patente) {
        try {
            String url = getFullUrl("/patente/" + patente);
            ResponseEntity<VehiculoDTO> response = restTemplate.getForEntity(url, VehiculoDTO.class);
            return response.getBody();
        } catch (Exception e) {
            throw new com.uncuyo.greedy_cars_web.exception.ApiException(
                "Error al buscar vehículo por patente: " + e.getMessage(), e
            );
        }
    }

    /**
     * Crear un nuevo vehículo
     * POST http://localhost:9000/api/v1/vehiculos
     */
    public VehiculoDTO crear(VehiculoDTO vehiculoDTO) {
        ResponseEntity<VehiculoDTO> response = post(vehiculoDTO, VehiculoDTO.class);
        return response.getBody();
    }

    /**
     * Actualizar un vehículo existente
     * PUT http://localhost:9000/api/v1/vehiculos/{id}
     */
    public VehiculoDTO actualizar(String id, VehiculoDTO vehiculoDTO) {
        ResponseEntity<VehiculoDTO> response = put(id, vehiculoDTO, VehiculoDTO.class);
        return response.getBody();
    }

    /**
     * Eliminar (lógicamente) un vehículo
     * DELETE http://localhost:9000/api/v1/vehiculos/{id}
     */
    public void eliminar(String id) {
        delete(id);
    }
}
