package com.gredy_cars_client.gredy_cars_client.shared.template.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base DTO carrying the common soft-delete contract used across the client.
 * Concrete DTOs should extend this class and override the id accessors so that
 * template services can interact with them in a type-safe way.
 *
 * @param <ID> identifier type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO<ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected ID id;
    protected Boolean eliminado = false;

    public abstract ID getId();

    public abstract void setId(ID id);
}

