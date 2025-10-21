package com.ejemplo.biblioteca.web.base;

import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Template Method base controller exposing the common CRUD endpoints.
 */
public abstract class AbstractCrudController<E, ID, ReqDTO, ResDTO> {

    protected abstract AbstractCrudService<E, ID> service();

    protected abstract E toEntityOnCreate(ReqDTO dto);

    protected abstract E toEntityOnUpdate(ReqDTO dto, ID id);

    protected abstract ResDTO toResponse(E entity);

    protected abstract String getBasePath();

    protected abstract Object getId(E entity);

    /* ===== Template Methods ===== */

    @PostMapping
    public ResponseEntity<ResDTO> create(@Valid @RequestBody ReqDTO dto, UriComponentsBuilder uri) {
        beforeCreateRequest(dto);
        E saved = service().create(toEntityOnCreate(dto));
        URI location = uri.path(getBasePath() + "/{id}").buildAndExpand(getId(saved)).toUri();
        return ResponseEntity.created(location).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResDTO> update(@PathVariable ID id, @Valid @RequestBody ReqDTO dto) {
        beforeUpdateRequest(id, dto);
        E saved = service().update(id, toEntityOnUpdate(dto, id));
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        service().delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResDTO> findById(@PathVariable ID id) {
        return ResponseEntity.ok(toResponse(service().findById(id)));
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> params
    ) {
        MultiValueMap<String, String> safeParams = params != null ? params : new LinkedMultiValueMap<>();
        beforeFindAll(safeParams);
        Pageable effectivePageable = preparePageable(pageable, safeParams);
        Page<ResDTO> page = doFindAll(effectivePageable, safeParams).map(this::toResponse);
        if (isPagedResponse(safeParams)) {
            return ResponseEntity.ok(page);
        }
        return ResponseEntity.ok(onListResponse(page.getContent(), safeParams));
    }

    /* ===== Hooks ===== */

    protected void beforeCreateRequest(ReqDTO dto) {
    }

    protected void beforeUpdateRequest(ID id, ReqDTO dto) {
    }

    protected void beforeFindAll(MultiValueMap<String, String> params) {
    }

    protected Pageable preparePageable(Pageable pageable, MultiValueMap<String, String> params) {
        return pageable;
    }

    protected Page<E> doFindAll(Pageable pageable, MultiValueMap<String, String> params) {
        return service().findAll(pageable);
    }

    protected boolean isPagedResponse(MultiValueMap<String, String> params) {
        return true;
    }

    protected Object onListResponse(List<ResDTO> content, MultiValueMap<String, String> params) {
        return content;
    }
}
