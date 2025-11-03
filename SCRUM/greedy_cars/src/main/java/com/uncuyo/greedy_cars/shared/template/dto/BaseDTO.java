package com.uncuyo.greedy_cars.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clase base para todos los DTOs del sistema.
 * Proporciona propiedades comunes que heredan todas las entidades.
 * 
 * @param <ID> Tipo del identificador de la entidad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO<ID> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Identificador único de la entidad
     */
    protected ID id;
    
    /**
     * Indica si el registro está marcado como eliminado (soft delete)
     */
    protected Boolean eliminado = false;
    
    /**
     * Método abstracto que debe ser implementado por las clases hijas
     * para obtener el ID de la entidad
     */
    public abstract ID getId();
    
    /**
     * Método abstracto que debe ser implementado por las clases hijas
     * para establecer el ID de la entidad
     */
    public abstract void setId(ID id);
}
