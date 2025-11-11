package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService extends BaseService<Vehiculo, String> {

    private AlquilerService alquilerService;

    @Autowired
    public VehiculoService(VehiculoRepository repository) {
        super(repository);
    }
    
    @Autowired
    @Lazy
    public void setAlquilerService(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }
    
    /**
     * Lists all active vehicles and ensures their states are synchronized.
     */
    @Override
    public List<Vehiculo> listarActivos() throws ErrorServiceException {
        // Synchronize all vehicle states before listing
        if (alquilerService != null) {
            try {
                alquilerService.sincronizarTodosLosEstadosVehiculos();
            } catch (Exception e) {
                // Log but don't fail - return vehicles with possibly stale states
                System.err.println("Warning: Could not synchronize vehicle states: " + e.getMessage());
            }
        }
        return super.listarActivos();
    }

    /**
     * Actualiza los campos de un vehículo existente con los datos de un vehículo nuevo.
     * No sobrescribe el ID ni el campo eliminado.
     */
    @Override
    protected void actualizarEntidad(Vehiculo entidadExistente, Vehiculo entidadNueva) {
        if (entidadNueva.getPatente() != null) {
            entidadExistente.setPatente(entidadNueva.getPatente());
        }
        if (entidadNueva.getEstadoVehiculo() != null) {
            entidadExistente.setEstadoVehiculo(entidadNueva.getEstadoVehiculo());
        }
        if (entidadNueva.getCaracteristicaVehiculo() != null) {
            entidadExistente.setCaracteristicaVehiculo(entidadNueva.getCaracteristicaVehiculo());
        }
    }
}
