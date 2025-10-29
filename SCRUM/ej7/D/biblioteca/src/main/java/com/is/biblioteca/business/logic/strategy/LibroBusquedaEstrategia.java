package com.is.biblioteca.business.logic.strategy;

import java.util.List;

import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.logic.error.ErrorServiceException;

public interface LibroBusquedaEstrategia {
    List<Libro> buscar(String valor) throws ErrorServiceException;
}
