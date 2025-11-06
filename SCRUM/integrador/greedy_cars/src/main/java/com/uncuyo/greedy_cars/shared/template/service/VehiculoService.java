package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService extends BaseService<Vehiculo, String> {

    @Autowired
    public VehiculoService(VehiculoRepository repository) {
        super(repository);
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
