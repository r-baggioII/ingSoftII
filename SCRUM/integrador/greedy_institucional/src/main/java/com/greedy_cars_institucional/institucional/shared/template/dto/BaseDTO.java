package com.greedy_cars_institucional.institucional.shared.template.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
