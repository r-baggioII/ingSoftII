package org.example.service;

import org.example.entity.Videojuego;
import org.example.repository.RepositorioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioVideojuego extends BaseService<Videojuego, RepositorioVideojuego> {
    
    @Autowired
    public ServicioVideojuego(RepositorioVideojuego repositorio) {
        super(repositorio);
    }

    // Los métodos básicos CRUD (findAll, findById, saveOne, updateOne, deleteById) 
    // son heredados de BaseService y no es necesario redefinirlos
    
    /*   Métodos específicos de Videojuego - Los adicionales que necesitas   */
    
    @Transactional(readOnly = true)
    public List<Videojuego> buscarTodosActivos() throws Exception {
        try {
            List<Videojuego> entities = this.repository.findAllByActivo();
            return entities;
        } catch (Exception e) {
            throw new Exception("Error al buscar videojuegos activos: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Videojuego buscarPorIdYActivo(long id) throws Exception {
        try {
            Optional<Videojuego> opt = this.repository.findByIdAndActivo(id);
            return opt.orElseThrow(() -> new Exception("Videojuego activo no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new Exception("Error al buscar videojuego: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Videojuego> buscarPorTitulo(String q) throws Exception {
        try {
            List<Videojuego> entities = this.repository.findByTitle(q);
            return entities;
        } catch (Exception e) {
            throw new Exception("Error al buscar videojuegos por título: " + e.getMessage());
        }
    }
    
    // Puedes agregar validaciones específicas para Videojuego:
    /*
    @Override
    protected void validar(Videojuego entidad) throws Exception {
        if (entidad.getTitulo() == null || entidad.getTitulo().trim().isEmpty()) {
            throw new Exception("El título del videojuego es obligatorio");
        }
        if (entidad.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a 0");
        }
        if (entidad.getStock() < 0) {
            throw new Exception("El stock no puede ser negativo");
        }
        // Otras validaciones...
    }
    */
}