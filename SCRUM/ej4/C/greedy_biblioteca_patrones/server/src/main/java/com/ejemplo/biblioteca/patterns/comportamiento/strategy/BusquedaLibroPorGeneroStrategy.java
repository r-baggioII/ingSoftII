package com.ejemplo.biblioteca.patterns.comportamiento.strategy;

import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class BusquedaLibroPorGeneroStrategy implements LibroBusquedaStrategy {

    private final LibroRepository libroRepository;

    @Override
    public TipoBusquedaLibro getTipo() {
        return TipoBusquedaLibro.GENERO;
    }

    @Override
    public Page<Libro> buscar(String valor, Pageable pageable) {
        if (!StringUtils.hasText(valor)) {
            throw new IllegalArgumentException("Debe especificar un género para la búsqueda");
        }
        return libroRepository.findByGeneroContainingIgnoreCase(valor, pageable);
    }
}
