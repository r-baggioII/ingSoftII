package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.service.LocalidadService;
import com.ejemplo.biblioteca.web.dto.LocalidadDTO;
import com.ejemplo.biblioteca.web.dto.LocalidadRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/localidades")
@RequiredArgsConstructor
public class LocalidadController {

    private final LocalidadService localidadService;

    @GetMapping
    public List<LocalidadDTO> findAll() {
        return localidadService.findAll();
    }

    @GetMapping("/{id}")
    public LocalidadDTO findById(@PathVariable Long id) {
        return localidadService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocalidadDTO create(@Valid @RequestBody LocalidadRequest request) {
        return localidadService.create(request);
    }
}
