package org.example.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseService<T extends BaseEntity, R extends JpaRepository<T, Long>> {

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    // CREATE - Alta
    @Transactional
    public T alta(T entidad) throws Exception {
        try {
            entidad.setActivo(true);
            validar(entidad);
            preAlta(entidad);
            
            T guardado = repository.save(entidad);
            
            postAlta(guardado);
            return guardado;
            
        } catch (Exception e) {
            throw new Exception("Error al dar de alta la entidad: " + e.getMessage());
        }
    }

    // READ - Obtener por ID
    @Transactional(readOnly = true)
    public T obtener(long id) throws Exception {
        try {
            Optional<T> opt = repository.findById(id);
            return opt.orElseThrow(() -> new Exception("Entidad no encontrada con ID: " + id));
        } catch (Exception e) {
            throw new Exception("Error al buscar la entidad: " + e.getMessage());
        }
    }

    // READ - Obtener todos
    @Transactional(readOnly = true)
    public List<T> listar() throws Exception {
        try {
            return repository.findAll();
        } catch (Exception e) {
            throw new Exception("Error al listar las entidades: " + e.getMessage());
        }
    }

    // READ - Listar solo activos
    @Transactional(readOnly = true)
    public List<T> listarActivos() throws Exception {
        try {
            return repository.findAll().stream()
                    .filter(T::isActivo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al listar entidades activas: " + e.getMessage());
        }
    }

    // UPDATE - Modificar
    @Transactional
    public T modificar(T entidad, long id) throws Exception {
        try {
            Optional<T> opt = repository.findById(id);
            if (opt.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            
            entidad.setId(id);
            validar(entidad);
            preModificacion(entidad);
            
            T actualizado = repository.save(entidad);
            postModificacion(actualizado);
            
            return actualizado;
        } catch (Exception e) {
            throw new Exception("Error al modificar la entidad: " + e.getMessage());
        }
    }

    // DELETE - Baja lógica (soft delete)
    @Transactional
    public boolean baja(long id) throws Exception {
        try {
            Optional<T> opt = repository.findById(id);
            if (opt.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            
            T entidad = opt.get();
            preBaja(entidad);
            
            // Soft delete: invertir el estado activo
            entidad.setActivo(!entidad.isActivo());
            repository.save(entidad);
            
            postBaja(entidad);
            return true;
        } catch (Exception e) {
            throw new Exception("Error al dar de baja la entidad: " + e.getMessage());
        }
    }

    // ============================================
    // Métodos de hook para ser redefinidos en las clases hijas
    // Permiten agregar lógica personalizada antes/después de las operaciones
    // ============================================
    protected void validar(T entidad) throws Exception {}
    protected void preAlta(T entidad) throws Exception {}
    protected void postAlta(T entidad) throws Exception {}
    protected void preModificacion(T entidad) throws Exception {}
    protected void postModificacion(T entidad) throws Exception {}
    protected void preBaja(T entidad) throws Exception {}
    protected void postBaja(T entidad) throws Exception {}
}