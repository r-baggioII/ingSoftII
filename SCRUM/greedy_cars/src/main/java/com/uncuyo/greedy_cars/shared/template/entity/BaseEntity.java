package com.uncuyo.greedy_cars.shared.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Base entity that provides soft-delete support and forces concrete entities
 * to expose their identifier. Designed to be extended when applying template
 * hooks across the project.
 *
 * @param <ID> identifier type
 */
@MappedSuperclass
public abstract class BaseEntity<ID> {

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    protected Boolean eliminado = false;

    public abstract ID getId();

    public abstract void setId(ID id);

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}
