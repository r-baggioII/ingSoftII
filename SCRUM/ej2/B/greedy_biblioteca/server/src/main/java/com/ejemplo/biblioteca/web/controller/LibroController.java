package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.service.LibroService;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public Page<LibroDTO> search(
            @RequestParam(required = false) Long autorId,
            @RequestParam(required = false) Long personaId,
            @RequestParam(required = false) String genero,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return libroService.search(autorId, personaId, genero, pageable);
    }

    @GetMapping("/{id}")
    public LibroDTO findById(@PathVariable Long id) {
        return libroService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibroDTO create(@Valid @RequestBody LibroRequest request) {
        return libroService.create(request);
    }

    @PutMapping("/{id}")
    public LibroDTO update(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return libroService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        libroService.delete(id);
    }
}
