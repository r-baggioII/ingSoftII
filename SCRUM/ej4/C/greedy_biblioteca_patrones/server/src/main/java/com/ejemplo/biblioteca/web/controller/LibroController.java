package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.patterns.comportamiento.strategy.TipoBusquedaLibro;
import com.ejemplo.biblioteca.service.LibroService;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import com.ejemplo.biblioteca.web.base.AbstractCrudController;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroRequest;
import com.ejemplo.biblioteca.web.dto.adapter.LibroAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController extends AbstractCrudController<Libro, Long, LibroRequest, LibroDTO> {

    private final LibroService libroService;
    private final LibroAdapter libroAdapter;

    @Override
    protected AbstractCrudService<Libro, Long> service() {
        return libroService;
    }

    @Override
    protected String getBasePath() {
        return "/api/libros";
    }

    @Override
    protected Object getId(Libro entity) {
        return entity.getId();
    }

    @Override
    protected Libro toEntityOnCreate(LibroRequest dto) {
        return libroAdapter.toEntity(dto);
    }

    @Override
    protected Libro toEntityOnUpdate(LibroRequest dto, Long id) {
        return libroAdapter.toEntity(dto, id);
    }

    @Override
    protected LibroDTO toResponse(Libro entity) {
        return libroAdapter.toDto(entity);
    }

    @Override
    protected Page<Libro> doFindAll(Pageable pageable, MultiValueMap<String, String> params) {
        String criterio = params.getFirst("criterio");
        String valor = params.getFirst("valor");
        if (StringUtils.hasText(criterio) && StringUtils.hasText(valor)) {
            TipoBusquedaLibro tipo = parseTipoBusqueda(criterio);
            return libroService.buscarPorCriterio(tipo, valor, pageable);
        }
        Long autorId = parseLong(params.getFirst("autorId"));
        Long personaId = parseLong(params.getFirst("personaId"));
        String genero = params.getFirst("genero");
        return libroService.search(autorId, personaId, genero, pageable);
    }

    @GetMapping("/autor/{autorId}/iterador")
    public ResponseEntity<List<LibroDTO>> librosPorAutorIterador(@PathVariable Long autorId) {
        List<LibroDTO> libros = libroService.listarPorAutorIterador(autorId).stream()
                .map(libroAdapter::toDto)
                .toList();
        return ResponseEntity.ok(libros);
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El parámetro debe ser numérico");
        }
    }

    private TipoBusquedaLibro parseTipoBusqueda(String value) {
        try {
            return TipoBusquedaLibro.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de búsqueda inválido: " + value);
        }
    }
}
