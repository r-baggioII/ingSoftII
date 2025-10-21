package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.service.AutorService;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import com.ejemplo.biblioteca.web.base.AbstractCrudController;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.AutorRequest;
import com.ejemplo.biblioteca.web.dto.mapper.AutorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autores")
@RequiredArgsConstructor
public class AutorController extends AbstractCrudController<Autor, Long, AutorRequest, AutorDTO> {

    private final AutorService autorService;
    private final AutorMapper autorMapper;

    @Override
    protected AbstractCrudService<Autor, Long> service() {
        return autorService;
    }

    @Override
    protected String getBasePath() {
        return "/api/autores";
    }

    @Override
    protected Object getId(Autor entity) {
        return entity.getId();
    }

    @Override
    protected Autor toEntityOnCreate(AutorRequest dto) {
        return autorMapper.toEntity(dto);
    }

    @Override
    protected Autor toEntityOnUpdate(AutorRequest dto, Long id) {
        return autorMapper.toEntity(dto, id);
    }

    @Override
    protected AutorDTO toResponse(Autor entity) {
        return autorMapper.toDto(entity);
    }

    @Override
    protected Page<Autor> doFindAll(Pageable pageable, MultiValueMap<String, String> params) {
        return autorService.findAll(Pageable.unpaged());
    }

    @Override
    protected boolean isPagedResponse(MultiValueMap<String, String> params) {
        return false;
    }

    @PostMapping("/{id}/clonar")
    public ResponseEntity<AutorDTO> clonarAutor(@PathVariable Long id) {
        Autor clon = autorService.clonarAutor(id);
        return ResponseEntity.ok(autorMapper.toDto(clon));
    }
}
