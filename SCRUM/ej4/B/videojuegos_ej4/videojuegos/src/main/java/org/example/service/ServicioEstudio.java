package org.example.service;

import org.example.entity.Estudio;
import org.example.repository.RepositorioEstudio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioEstudio extends BaseService<Estudio, RepositorioEstudio> {
    
    @Autowired
    public ServicioEstudio(RepositorioEstudio repositorio) {
        super(repositorio);
    }

    // Aquí puedes agregar métodos específicos de Estudio si los necesitas
    // O sobrescribir los métodos de validación:
    
    /*
    @Override
    protected void validar(Estudio entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del estudio es obligatorio");
        }
        // Otras validaciones específicas...
    }
    */
}