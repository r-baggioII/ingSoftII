package com.is.biblioteca.business.logic.strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.domain.enumeration.TipoBusquedaLibro;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.strategy.impl.BusquedaPorAnioEstrategia;
import com.is.biblioteca.business.logic.strategy.impl.BusquedaPorAutorEstrategia;
import com.is.biblioteca.business.logic.strategy.impl.BusquedaPorEditorialEstrategia;

@Service
public class LibroBusquedaService {

    private final Map<TipoBusquedaLibro, LibroBusquedaEstrategia> estrategias = new EnumMap<>(TipoBusquedaLibro.class);

    @Autowired
    public LibroBusquedaService(BusquedaPorAnioEstrategia porAnio,
                                BusquedaPorEditorialEstrategia porEditorial,
                                BusquedaPorAutorEstrategia porAutor) {
        estrategias.put(TipoBusquedaLibro.ANIO, porAnio);
        estrategias.put(TipoBusquedaLibro.EDITORIAL, porEditorial);
        estrategias.put(TipoBusquedaLibro.AUTOR, porAutor);
    }

    public List<Libro> buscar(TipoBusquedaLibro tipo, String valor) throws ErrorServiceException {
        LibroBusquedaEstrategia estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new ErrorServiceException("Tipo de búsqueda no soportado");
        }
        return estrategia.buscar(valor);
    }
}
