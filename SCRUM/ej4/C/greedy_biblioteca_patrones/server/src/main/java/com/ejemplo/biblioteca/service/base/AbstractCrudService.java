package com.ejemplo.biblioteca.service.base;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Template Method base class providing standard CRUD operations with lifecycle hooks.
 */
public abstract class AbstractCrudService<E, ID> {

    /**
     * @return the repository to operate against.
     */
    protected abstract JpaRepository<E, ID> repository();

    /* ===== Template Methods ===== */

    @Transactional
    public E create(E entity) {
        validateNew(entity);
        beforeCreate(entity);
        E toPersist = transformOnCreate(entity);
        E saved = repository().save(toPersist);
        afterCreate(saved);
        return saved;
    }

    @Transactional
    public E update(ID id, E incoming) {
        E current = requireOne(id);
        validateUpdate(id, incoming, current);
        beforeUpdate(incoming, current);
        E merged = mergeForUpdate(incoming, current);
        E saved = repository().save(merged);
        afterUpdate(saved, current);
        return saved;
    }

    @Transactional
    public void delete(ID id) {
        E current = requireOne(id);
        validateDelete(current);
        beforeDelete(current);
        repository().delete(current);
        afterDelete(current);
    }

    @Transactional(readOnly = true)
    public E findById(ID id) {
        return requireOne(id);
    }

    @Transactional(readOnly = true)
    public Page<E> findAll(Pageable pageable) {
        return repository().findAll(pageable);
    }

    /* ===== Hooks (override when needed) ===== */

    protected void validateNew(E entity) {
    }

    protected void beforeCreate(E entity) {
    }

    protected E transformOnCreate(E entity) {
        return entity;
    }

    protected void afterCreate(E saved) {
    }

    protected void validateUpdate(ID id, E incoming, E current) {
    }

    protected void beforeUpdate(E incoming, E current) {
    }

    protected E mergeForUpdate(E incoming, E current) {
        return incoming;
    }

    protected void afterUpdate(E saved, E previous) {
    }

    protected void validateDelete(E current) {
    }

    protected void beforeDelete(E current) {
    }

    protected void afterDelete(E current) {
    }

    /* ===== Utilities ===== */

    protected E requireOne(ID id) {
        return repository()
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe id=" + id));
    }
}
