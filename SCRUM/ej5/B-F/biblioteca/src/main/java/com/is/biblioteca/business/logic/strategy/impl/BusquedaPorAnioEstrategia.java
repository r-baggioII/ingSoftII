package com.is.biblioteca.business.logic.strategy.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.strategy.LibroBusquedaEstrategia;
import com.is.biblioteca.business.persistence.repository.LibroRepository;

@Component("busquedaPorAnioEstrategia")
public class BusquedaPorAnioEstrategia implements LibroBusquedaEstrategia {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Libro> buscar(String valor) throws ErrorServiceException {
        try {
            if (valor == null || valor.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el año");
            }
            Integer anio = Integer.valueOf(valor);
            return libroRepository.listarLibroPorAnio(anio);
        } catch (NumberFormatException ex) {
            throw new ErrorServiceException("El año debe ser un número válido");
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
