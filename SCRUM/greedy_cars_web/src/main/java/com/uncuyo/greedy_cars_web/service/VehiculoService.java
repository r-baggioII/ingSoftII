package com.uncuyo.greedy_cars_web.service;

import com.uncuyo.greedy_cars_web.dto.VehiculoDTO;
import com.uncuyo.greedy_cars_web.exception.ErrorServiceException;

import java.util.List;

/**
 * Interfaz de servicio para operaciones de Vehículo en el frontend
 */
public interface VehiculoService {

    /**
     * Listar todos los vehículos activos
     */
    List<VehiculoDTO> listarTodos() throws ErrorServiceException;

    /**
     * Listar vehículos por estado
     */
    List<VehiculoDTO> listarPorEstado(String estado) throws ErrorServiceException;

    /**
     * Obtener un vehículo por su ID
     */
    VehiculoDTO obtenerPorId(String id) throws ErrorServiceException;

    /**
     * Buscar un vehículo por su patente
     */
    VehiculoDTO buscarPorPatente(String patente) throws ErrorServiceException;

    /**
     * Crear un nuevo vehículo
     */
    VehiculoDTO crear(VehiculoDTO vehiculoDTO) throws ErrorServiceException;

    /**
     * Actualizar un vehículo existente
     */
    VehiculoDTO actualizar(String id, VehiculoDTO vehiculoDTO) throws ErrorServiceException;

    /**
     * Eliminar un vehículo
     */
    void eliminar(String id) throws ErrorServiceException;
}
