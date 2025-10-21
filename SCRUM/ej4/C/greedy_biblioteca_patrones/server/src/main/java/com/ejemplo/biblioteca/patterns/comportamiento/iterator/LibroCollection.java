package com.ejemplo.biblioteca.patterns.comportamiento.iterator;

import com.ejemplo.biblioteca.domain.Libro;

import java.util.Iterator;
import java.util.List;

/**
 * Agregado que expone iteradores especializados sobre un conjunto de libros.
 */
public class LibroCollection implements Iterable<Libro> {

    private final List<Libro> libros;

    public LibroCollection(List<Libro> libros) {
        this.libros = libros;
    }

    public Iterator<Libro> iteratorPorAutor(Long autorId) {
        return new LibroPorAutorIterator(libros.iterator(), autorId);
    }

    @Override
    public Iterator<Libro> iterator() {
        return libros.iterator();
    }
}
