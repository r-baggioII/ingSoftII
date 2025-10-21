package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.service.PersonaService;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import com.ejemplo.biblioteca.web.base.AbstractCrudController;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.PersonaDTO;
import com.ejemplo.biblioteca.web.dto.PersonaRequest;
import com.ejemplo.biblioteca.web.dto.adapter.LibroAdapter;
import com.ejemplo.biblioteca.web.dto.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController extends AbstractCrudController<Persona, Long, PersonaRequest, PersonaDTO> {

    private final PersonaService personaService;
    private final PersonaMapper personaMapper;
    private final LibroAdapter libroAdapter;

    @Override
    protected AbstractCrudService<Persona, Long> service() {
        return personaService;
    }

    @Override
    protected String getBasePath() {
        return "/api/personas";
    }

    @Override
    protected Object getId(Persona entity) {
        return entity.getId();
    }

    @Override
    protected Persona toEntityOnCreate(PersonaRequest dto) {
        return personaMapper.toEntity(dto);
    }

    @Override
    protected Persona toEntityOnUpdate(PersonaRequest dto, Long id) {
        return personaMapper.toEntity(dto, id);
    }

    @Override
    protected PersonaDTO toResponse(Persona entity) {
        return personaMapper.toDto(entity);
    }

    @Override
    protected Page<Persona> doFindAll(Pageable pageable, MultiValueMap<String, String> params) {
        String apellido = params.getFirst("apellido");
        Integer dni = parseInteger(params.getFirst("dni"));
        return personaService.search(apellido, dni, pageable);
    }

    @GetMapping("/{id}/libros")
    public ResponseEntity<Page<LibroDTO>> librosPorPersona(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<LibroDTO> page = personaService.findLibros(id, pageable).map(libroAdapter::toDto);
        return ResponseEntity.ok(page);
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El DNI debe ser numérico");
        }
    }
}
