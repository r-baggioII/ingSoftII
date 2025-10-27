package org.sistemaMecanico.service;

import org.sistemaMecanico.entity.HistorialArreglo;
import org.sistemaMecanico.repository.HistorialArregloRepository;
import org.sistemaMecanico.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistorialArregloService extends BaseService<HistorialArreglo, String> {

    @Autowired
    public HistorialArregloService(HistorialArregloRepository repository) {
        super(repository);
    }

    /**
     * Actualiza los campos de un historial de arreglo existente con los datos de uno nuevo.
     * No sobrescribe el ID ni el campo eliminado.
     */
    @Override
    protected void actualizarEntidad(HistorialArreglo entidadExistente, HistorialArreglo entidadNueva) {
        if (entidadNueva.getFechaArreglo() != null) {
            entidadExistente.setFechaArreglo(entidadNueva.getFechaArreglo());
        }
        
        if (entidadNueva.getDetalleArreglo() != null) {
            entidadExistente.setDetalleArreglo(entidadNueva.getDetalleArreglo());
        }
        
        // Actualizar la referencia al vehículo si se proporciona
        if (entidadNueva.getVehiculo() != null) {
            entidadExistente.setVehiculo(entidadNueva.getVehiculo());
        }
        
        // Actualizar la relación con mecánicos
        if (entidadNueva.getMecanicos() != null) {
            entidadExistente.getMecanicos().clear();
            entidadExistente.getMecanicos().addAll(entidadNueva.getMecanicos());
        }
    }
}
