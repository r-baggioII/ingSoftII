package org.sistemaMecanico.service;

import org.sistemaMecanico.entity.Mecanico;
import org.sistemaMecanico.repository.MecanicoRepository;
import org.sistemaMecanico.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MecanicoService extends BaseService<Mecanico, String> {

    @Autowired
    public MecanicoService(MecanicoRepository repository) {
        super(repository);
    }

    /**
     * Actualiza los campos de un mecánico existente con los datos de un mecánico nuevo.
     * No sobrescribe el ID ni el campo eliminado.
     */
    @Override
    protected void actualizarEntidad(Mecanico entidadExistente, Mecanico entidadNueva) {
        // Actualizar campos heredados de Persona
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
        if (entidadNueva.getApellido() != null) {
            entidadExistente.setApellido(entidadNueva.getApellido());
        }
        
        // Actualizar campos propios de Mecánico
        if (entidadNueva.getLegajo() != null) {
            entidadExistente.setLegajo(entidadNueva.getLegajo());
        }
        
        // No actualizamos la relación de arreglos aquí
        // Eso debería manejarse con métodos específicos
    }
}
