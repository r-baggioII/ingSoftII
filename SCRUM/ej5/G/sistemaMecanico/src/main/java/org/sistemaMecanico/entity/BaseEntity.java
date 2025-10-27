package org.sistemaMecanico.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;

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






