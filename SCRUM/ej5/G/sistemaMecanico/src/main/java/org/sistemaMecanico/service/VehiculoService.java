package org.sistemaMecanico.service;

import org.sistemaMecanico.entity.Vehiculo;
import org.sistemaMecanico.repository.VehiculoRepository;
import org.sistemaMecanico.exception.ErrorServiceException;
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
        
        if (entidadNueva.getMarca() != null) {
            entidadExistente.setMarca(entidadNueva.getMarca());
        }
        
        if (entidadNueva.getModelo() != null) {
            entidadExistente.setModelo(entidadNueva.getModelo());
        }
        
        // Las relaciones (historiales y clientes) deberían manejarse con métodos específicos
    }
}
