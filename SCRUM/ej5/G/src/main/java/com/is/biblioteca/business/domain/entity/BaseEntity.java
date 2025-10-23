package com.is.biblioteca.business.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity<ID> {

    @Column(nullable = false, columnDefinition = "BIT(1) DEFAULT 0")
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
