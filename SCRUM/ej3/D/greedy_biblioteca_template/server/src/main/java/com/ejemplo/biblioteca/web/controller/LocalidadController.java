package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.service.LocalidadService;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import com.ejemplo.biblioteca.web.base.AbstractCrudController;
import com.ejemplo.biblioteca.web.dto.LocalidadDTO;
import com.ejemplo.biblioteca.web.dto.LocalidadRequest;
import com.ejemplo.biblioteca.web.dto.mapper.LocalidadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/localidades")
@RequiredArgsConstructor
public class LocalidadController extends AbstractCrudController<Localidad, Long, LocalidadRequest, LocalidadDTO> {

    private final LocalidadService localidadService;
    private final LocalidadMapper localidadMapper;

    @Override
    protected AbstractCrudService<Localidad, Long> service() {
        return localidadService;
    }

    @Override
    protected String getBasePath() {
        return "/api/localidades";
    }

    @Override
    protected Object getId(Localidad entity) {
        return entity.getId();
    }

    @Override
    protected Localidad toEntityOnCreate(LocalidadRequest dto) {
        return localidadMapper.toEntity(dto);
    }

    @Override
    protected Localidad toEntityOnUpdate(LocalidadRequest dto, Long id) {
        return localidadMapper.toEntity(dto, id);
    }

    @Override
    protected LocalidadDTO toResponse(Localidad entity) {
        return localidadMapper.toDto(entity);
    }

    @Override
    protected Page<Localidad> doFindAll(Pageable pageable, MultiValueMap<String, String> params) {
        return localidadService.findAll(Pageable.unpaged());
    }

    @Override
    protected boolean isPagedResponse(MultiValueMap<String, String> params) {
        return false;
    }
}
