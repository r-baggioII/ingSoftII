package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.service.PersonaService;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.PersonaDTO;
import com.ejemplo.biblioteca.web.dto.PersonaRequest;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    public Page<PersonaDTO> search(
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) Integer dni,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return personaService.search(apellido, dni, pageable);
    }

    @GetMapping("/{id}")
    public PersonaDTO findById(@PathVariable Long id) {
        return personaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaDTO create(@Valid @RequestBody PersonaRequest request) {
        return personaService.create(request);
    }

    @PutMapping("/{id}")
    public PersonaDTO update(@PathVariable Long id, @Valid @RequestBody PersonaRequest request) {
        return personaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        personaService.delete(id);
    }

    @GetMapping("/{id}/libros")
    public Page<LibroDTO> librosPorPersona(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        return personaService.findLibros(id, pageable);
    }
}
