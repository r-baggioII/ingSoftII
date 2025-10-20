package org.example.service;

import org.example.entity.Categoria;
import org.example.repository.RepositorioCategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioCategoria extends BaseService<Categoria, RepositorioCategoria> {
    
    @Autowired
    public ServicioCategoria(RepositorioCategoria repositorio) {
        super(repositorio);
    }

    // Aquí puedes agregar métodos específicos de Categoria si los necesitas
    // O sobrescribir los métodos de validación:
    
    /*
    @Override
    protected void validar(Categoria entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la categoría es obligatorio");
        }
        // Otras validaciones específicas...
    }
    */
}