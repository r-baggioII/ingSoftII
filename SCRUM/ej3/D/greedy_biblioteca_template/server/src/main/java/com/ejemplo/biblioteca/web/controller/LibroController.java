package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.service.LibroService;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import com.ejemplo.biblioteca.web.base.AbstractCrudController;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroRequest;
import com.ejemplo.biblioteca.web.dto.mapper.LibroMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController extends AbstractCrudController<Libro, Long, LibroRequest, LibroDTO> {

    private final LibroService libroService;
    private final LibroMapper libroMapper;

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
        return libroMapper.toEntity(dto);
    }

    @Override
    protected Libro toEntityOnUpdate(LibroRequest dto, Long id) {
        return libroMapper.toEntity(dto, id);
    }

    @Override
    protected LibroDTO toResponse(Libro entity) {
        return libroMapper.toDto(entity);
    }

    @Override
    protected Page<Libro> doFindAll(Pageable pageable, MultiValueMap<String, String> params) {
        Long autorId = parseLong(params.getFirst("autorId"));
        Long personaId = parseLong(params.getFirst("personaId"));
        String genero = params.getFirst("genero");
        return libroService.search(autorId, personaId, genero, pageable);
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
}
