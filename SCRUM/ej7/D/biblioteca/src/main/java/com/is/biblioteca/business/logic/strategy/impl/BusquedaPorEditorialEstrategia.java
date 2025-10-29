package com.is.biblioteca.business.logic.strategy.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.strategy.LibroBusquedaEstrategia;
import com.is.biblioteca.business.persistence.repository.LibroRepository;

@Component("busquedaPorEditorialEstrategia")
public class BusquedaPorEditorialEstrategia implements LibroBusquedaEstrategia {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Libro> buscar(String valor) throws ErrorServiceException {
        try {
            if (valor == null || valor.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar la editorial");
            }
            return libroRepository.listarLibroPorEditorial(valor);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
