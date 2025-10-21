package com.ejemplo.biblioteca.patterns.comportamiento.iterator;

import com.ejemplo.biblioteca.domain.Libro;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Iterador concreto que filtra libros por autor.
 */
public class LibroPorAutorIterator implements Iterator<Libro> {

    private final Iterator<Libro> delegate;
    private final Long autorId;
    private Libro siguiente;

    public LibroPorAutorIterator(Iterator<Libro> delegate, Long autorId) {
        this.delegate = delegate;
        this.autorId = autorId;
        avanzar();
    }

    @Override
    public boolean hasNext() {
        return siguiente != null;
    }

    @Override
    public Libro next() {
        if (siguiente == null) {
            throw new NoSuchElementException("No hay más libros para el autor indicado");
        }
        Libro actual = siguiente;
        avanzar();
        return actual;
    }

    private void avanzar() {
        siguiente = null;
        while (delegate.hasNext()) {
            Libro candidato = delegate.next();
            if (candidato.getAutor() != null && Objects.equals(candidato.getAutor().getId(), autorId)) {
                siguiente = candidato;
                break;
            }
        }
    }
}
