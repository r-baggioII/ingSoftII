package com.ejemplo.biblioteca.patterns.comportamiento.strategy;

import com.ejemplo.biblioteca.domain.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Estrategia de búsqueda para libros en función de distintos criterios.
 */
public interface LibroBusquedaStrategy {

    TipoBusquedaLibro getTipo();

    Page<Libro> buscar(String valor, Pageable pageable);
}
