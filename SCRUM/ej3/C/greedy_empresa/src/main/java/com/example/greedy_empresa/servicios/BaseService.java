package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.BaseEntity;
import com.example.greedy_empresa.repositorios.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase base que implementa el patrón Template Method para servicios CRUD.
 * Define el esqueleto de operaciones comunes (buscar, guardar, eliminar) 
 * y delega a métodos abstractos/hooks para personalización.
 */
public abstract class BaseService<T extends BaseEntity, REPOSITORY extends BaseRepository<T, String>> {

    protected REPOSITORY repositorio;

    public BaseService(REPOSITORY repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Template Method: Define el algoritmo de búsqueda con filtro opcional
     */
    @Transactional(readOnly = true)
    public Page<T> buscar(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return repositorio.findByEliminadoFalse(pageable);
        }
        // Hook method - las subclases pueden sobrescribir para búsqueda específica
        return buscarConFiltro(filtro.trim(), pageable);
    }

    /**
     * Hook method: Permite a las subclases implementar búsqueda específica
     */
    protected Page<T> buscarConFiltro(String filtro, Pageable pageable) {
        return repositorio.findByEliminadoFalse(pageable);
    }

    /**
     * Template Method: Buscar entidad por ID
     */
    @Transactional(readOnly = true)
    public T buscarPorId(String id) {
        return repositorio.findById(id)
                .filter(entidad -> !entidad.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException(getEntityName() + " no encontrado/a"));
    }

    /**
     * Template Method: Define el algoritmo completo de guardado
     * 1. Validar entidad
     * 2. Normalizar datos
     * 3. Validar unicidad
     * 4. Procesar relaciones
     * 5. Guardar en base de datos
     */
    @Transactional
    public T guardar(T entidad) {
        // Paso 1: Validar datos básicos
        validarEntidad(entidad);
        
        // Paso 2: Normalizar datos
        normalizarDatos(entidad);
        
        // Paso 3: Validar unicidad (evitar duplicados)
        validarUnicidad(entidad);
        
        // Paso 4: Procesar relaciones con otras entidades
        procesarRelaciones(entidad);
        
        // Paso 5: Guardar (actualizar o crear nuevo)
        if (entidad.getId() != null && !entidad.getId().isBlank()) {
            return actualizarEntidad(entidad);
        } else {
            return crearNuevaEntidad(entidad);
        }
    }

    /**
     * Template Method: Eliminar entidad (soft delete)
     */
    @Transactional
    public void eliminar(String id) {
        T entidad = buscarPorId(id);
        antesDeEliminar(entidad);
        entidad.setEliminado(true);
        repositorio.save(entidad);
    }

    // ========== Hook Methods - Las subclases pueden sobrescribir ==========

    /**
     * Hook: Validar datos de la entidad antes de guardar
     */
    protected void validarEntidad(T entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
    }

    /**
     * Hook: Normalizar datos (trim, uppercase, etc.)
     */
    protected void normalizarDatos(T entidad) {
        // Por defecto no hace nada, las subclases pueden sobrescribir
    }

    /**
     * Hook: Validar que no exista duplicado
     */
    protected void validarUnicidad(T entidad) {
        // Por defecto no hace nada, las subclases pueden sobrescribir
    }

    /**
     * Hook: Procesar relaciones con otras entidades
     */
    protected void procesarRelaciones(T entidad) {
        // Por defecto no hace nada, las subclases pueden sobrescribir
    }

    /**
     * Hook: Actualizar entidad existente
     */
    protected T actualizarEntidad(T entidad) {
        T existente = buscarPorId(entidad.getId());
        actualizarCampos(existente, entidad);
        return repositorio.save(existente);
    }

    /**
     * Hook: Actualizar campos de entidad existente con datos nuevos
     */
    protected abstract void actualizarCampos(T existente, T nueva);

    /**
     * Hook: Crear nueva entidad
     */
    protected T crearNuevaEntidad(T entidad) {
        return repositorio.save(entidad);
    }

    /**
     * Hook: Ejecutar lógica antes de eliminar
     */
    protected void antesDeEliminar(T entidad) {
        // Por defecto no hace nada, las subclases pueden sobrescribir
    }

    // ========== Métodos abstractos - Las subclases DEBEN implementar ==========

    public abstract Class<T> getEntityClass();

    protected abstract String getEntityName();
}