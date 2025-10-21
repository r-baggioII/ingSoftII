package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.domain.TipoLibro;
import com.ejemplo.biblioteca.patterns.comportamiento.iterator.LibroCollection;
import com.ejemplo.biblioteca.patterns.comportamiento.strategy.LibroBusquedaStrategy;
import com.ejemplo.biblioteca.patterns.comportamiento.strategy.TipoBusquedaLibro;
import com.ejemplo.biblioteca.repository.AutorRepository;
import com.ejemplo.biblioteca.repository.LibroRepository;
import com.ejemplo.biblioteca.repository.PersonaRepository;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LibroService extends AbstractCrudService<Libro, Long> {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final PersonaRepository personaRepository;
    private final List<LibroBusquedaStrategy> libroBusquedaStrategies;

    @Override
    protected JpaRepository<Libro, Long> repository() {
        return libroRepository;
    }

    @Transactional(readOnly = true)
    public Page<Libro> search(Long autorId, Long personaId, String genero, Pageable pageable) {
        String generoFilter = genero == null || genero.isBlank() ? null : genero;
        return libroRepository.search(autorId, personaId, generoFilter, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Libro> findByPersona(Long personaId, Pageable pageable) {
        if (!personaRepository.existsById(personaId)) {
            throw new EntityNotFoundException("Persona no existe id=" + personaId);
        }
        return libroRepository.search(null, personaId, null, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Libro> buscarPorCriterio(TipoBusquedaLibro tipo, String valor, Pageable pageable) {
        LibroBusquedaStrategy strategy = libroBusquedaStrategies.stream()
                .filter(s -> s.getTipo() == tipo)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe estrategia para: " + tipo));
        return strategy.buscar(valor, pageable);
    }

    @Transactional(readOnly = true)
    public List<Libro> listarPorAutorIterador(Long autorId) {
        if (autorId == null) {
            throw new IllegalArgumentException("autorId requerido");
        }
        List<Libro> libros = libroRepository.findAll();
        LibroCollection collection = new LibroCollection(libros);
        Iterator<Libro> iterator = collection.iteratorPorAutor(autorId);
        List<Libro> resultado = new ArrayList<>();
        iterator.forEachRemaining(resultado::add);
        return resultado;
    }

    @Override
    protected void validateNew(Libro entity) {
        validateCommon(entity);
    }

    @Override
    protected void validateUpdate(Long id, Libro incoming, Libro current) {
        validateCommon(incoming);
    }

    @Override
    protected Libro transformOnCreate(Libro entity) {
        resolveRelations(entity);
        return entity;
    }

    @Override
    protected Libro mergeForUpdate(Libro incoming, Libro current) {
        resolveRelations(incoming);
        current.setTitulo(incoming.getTitulo());
        current.setFecha(incoming.getFecha());
        current.setGenero(incoming.getGenero());
        current.setPaginas(incoming.getPaginas());
        current.setTipo(incoming.getTipo());
        current.setPesoGramos(incoming.getPesoGramos());
        current.setTamanoMb(incoming.getTamanoMb());
        if (incoming.getAutor() != null) {
            current.setAutor(incoming.getAutor());
        }
        if (incoming.getPersona() != null) {
            current.setPersona(incoming.getPersona());
        }
        return current;
    }

    private void validateCommon(Libro libro) {
        if (!StringUtils.hasText(libro.getTitulo())) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (libro.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de publicación es obligatoria");
        }
        if (libro.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de publicación no puede ser futura");
        }
        if (!StringUtils.hasText(libro.getGenero())) {
            throw new IllegalArgumentException("El género es obligatorio");
        }
        if (libro.getPaginas() == null || libro.getPaginas() <= 0) {
            throw new IllegalArgumentException("Las páginas deben ser positivas");
        }
        if (libro.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de libro es obligatorio");
        }
        if (libro.getTipo() == TipoLibro.FISICO) {
            if (libro.getPesoGramos() == null || libro.getPesoGramos() <= 0) {
                throw new IllegalArgumentException("El peso en gramos es obligatorio para libros físicos");
            }
            libro.setTamanoMb(null);
        } else if (libro.getTipo() == TipoLibro.DIGITAL) {
            if (libro.getTamanoMb() == null || libro.getTamanoMb() <= 0) {
                throw new IllegalArgumentException("El tamaño en MB es obligatorio para libros digitales");
            }
            libro.setPesoGramos(null);
        }
        if (libro.getAutor() == null || libro.getAutor().getId() == null) {
            throw new IllegalArgumentException("autorId requerido");
        }
        if (libro.getPersona() == null || libro.getPersona().getId() == null) {
            throw new IllegalArgumentException("personaId requerido");
        }
    }

    private void resolveRelations(Libro libro) {
        Long autorId = libro.getAutor() != null ? libro.getAutor().getId() : null;
        Long personaId = libro.getPersona() != null ? libro.getPersona().getId() : null;

        if (autorId == null) {
            throw new IllegalArgumentException("autorId requerido");
        }
        if (personaId == null) {
            throw new IllegalArgumentException("personaId requerido");
        }

        Autor autor = autorRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Autor no existe id=" + autorId));
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no existe id=" + personaId));

        libro.setAutor(autor);
        libro.setPersona(persona);
    }
}
