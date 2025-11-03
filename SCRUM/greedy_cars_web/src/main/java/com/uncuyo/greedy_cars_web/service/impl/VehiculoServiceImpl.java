package com.uncuyo.greedy_cars_web.service.impl;

import com.uncuyo.greedy_cars_web.dto.VehiculoDTO;
import com.uncuyo.greedy_cars_web.exception.ApiException;
import com.uncuyo.greedy_cars_web.exception.ErrorServiceException;
import com.uncuyo.greedy_cars_web.rest.VehiculoDAORest;
import com.uncuyo.greedy_cars_web.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de Vehículo
 * Este servicio actúa como capa intermedia entre el controlador y el DAO REST
 * Puede agregar validaciones, transformaciones o lógica de presentación adicional
 */
@Service
public class VehiculoServiceImpl implements VehiculoService {

    @Autowired
    private VehiculoDAORest vehiculoDAORest;

    @Override
    public List<VehiculoDTO> listarTodos() throws ErrorServiceException {
        try {
            List<VehiculoDTO> vehiculos = vehiculoDAORest.listarTodos();
            
            // Aquí podrías agregar lógica adicional, por ejemplo:
            // - Ordenar la lista
            // - Filtrar ciertos elementos
            // - Agregar información adicional para la vista
            
            return vehiculos;
        } catch (ApiException e) {
            throw new ErrorServiceException("Error al listar vehículos desde la API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ErrorServiceException("Error inesperado al listar vehículos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<VehiculoDTO> listarPorEstado(String estado) throws ErrorServiceException {
        try {
            // Validación de entrada
            if (estado == null || estado.trim().isEmpty()) {
                throw new ErrorServiceException("El estado no puede estar vacío");
            }
            
            return vehiculoDAORest.listarPorEstado(estado.toUpperCase());
        } catch (ApiException e) {
            throw new ErrorServiceException("Error al listar vehículos por estado: " + e.getMessage(), e);
        }
    }

    @Override
    public VehiculoDTO obtenerPorId(String id) throws ErrorServiceException {
        try {
            // Validación de entrada
            if (id == null || id.trim().isEmpty()) {
                throw new ErrorServiceException("El ID no puede estar vacío");
            }
            
            VehiculoDTO vehiculo = vehiculoDAORest.obtenerPorId(id);
            
            if (vehiculo == null) {
                throw new ErrorServiceException("No se encontró el vehículo con ID: " + id);
            }
            
            return vehiculo;
        } catch (ApiException e) {
            throw new ErrorServiceException("Error al obtener vehículo: " + e.getMessage(), e);
        }
    }

    @Override
    public VehiculoDTO buscarPorPatente(String patente) throws ErrorServiceException {
        try {
            // Validación y normalización de la patente
            if (patente == null || patente.trim().isEmpty()) {
                throw new ErrorServiceException("La patente no puede estar vacía");
            }
            
            String patenteNormalizada = patente.trim().toUpperCase();
            VehiculoDTO vehiculo = vehiculoDAORest.buscarPorPatente(patenteNormalizada);
            
            if (vehiculo == null) {
                throw new ErrorServiceException("No se encontró el vehículo con patente: " + patenteNormalizada);
            }
            
            return vehiculo;
        } catch (ApiException e) {
            throw new ErrorServiceException("Error al buscar vehículo por patente: " + e.getMessage(), e);
        }
    }

    @Override
    public VehiculoDTO crear(VehiculoDTO vehiculoDTO) throws ErrorServiceException {
        try {
            // Validaciones previas al envío
            validarVehiculoDTO(vehiculoDTO);
            
            // Normalizar datos
            vehiculoDTO.setPatente(vehiculoDTO.getPatente().trim().toUpperCase());
            
            return vehiculoDAORest.crear(vehiculoDTO);
        } catch (ApiException e) {
            throw new ErrorServiceException("Error al crear vehículo: " + e.getMessage(), e);
        }
    }

    @Override
    public VehiculoDTO actualizar(String id, VehiculoDTO vehiculoDTO) throws ErrorServiceException {
        try {
            // Validaciones
            if (id == null || id.trim().isEmpty()) {
                throw new ErrorServiceException("El ID no puede estar vacío");
            }
            validarVehiculoDTO(vehiculoDTO);
            
            // Normalizar datos
            vehiculoDTO.setPatente(vehiculoDTO.getPatente().trim().toUpperCase());
            
            return vehiculoDAORest.actualizar(id, vehiculoDTO);
        } catch (ApiException e) {
            throw new ErrorServiceException("Error al actualizar vehículo: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(String id) throws ErrorServiceException {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new ErrorServiceException("El ID no puede estar vacío");
            }
            
            vehiculoDAORest.eliminar(id);
        } catch (ApiException e) {
            throw new ErrorServiceException("Error al eliminar vehículo: " + e.getMessage(), e);
        }
    }

    /**
     * Método privado para validar el DTO antes de enviarlo a la API
     */
    private void validarVehiculoDTO(VehiculoDTO dto) throws ErrorServiceException {
        if (dto == null) {
            throw new ErrorServiceException("El vehículo no puede ser nulo");
        }
        if (dto.getPatente() == null || dto.getPatente().trim().isEmpty()) {
            throw new ErrorServiceException("La patente es obligatoria");
        }
        if (dto.getEstadoVehiculo() == null || dto.getEstadoVehiculo().trim().isEmpty()) {
            throw new ErrorServiceException("El estado del vehículo es obligatorio");
        }
        
        // Validar formato de patente (ejemplo)
        String patente = dto.getPatente().trim();
        if (patente.length() < 6 || patente.length() > 20) {
            throw new ErrorServiceException("La patente debe tener entre 6 y 20 caracteres");
        }
    }
}
