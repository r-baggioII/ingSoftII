package com.gredy_cars_client.gredy_cars_client.shared.template.controller.api.gestion;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.LocalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.LocalidadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/gestion/api/localidades")
@Validated
public class LocalidadGestionApiController {

    private final LocalidadService service;

    public LocalidadGestionApiController(LocalidadService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<LocalidadDTO>> listar() throws ErrorServiceException {
        return ResponseEntity.ok(service.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalidadDTO> obtener(@PathVariable Long id) throws ErrorServiceException {
        Optional<LocalidadDTO> dto = service.obtener(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<LocalidadDTO>> listarPorDepartamento(@PathVariable Long departamentoId) throws ErrorServiceException {
        List<LocalidadDTO> todas = service.listarActivos();
        List<LocalidadDTO> filtradas = todas.stream()
            .filter(l -> l.getDepartamento() != null && l.getDepartamento().getId() != null && l.getDepartamento().getId().equals(departamentoId))
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(filtradas);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody LocalidadDTO payload) {
        try {
            LocalidadDTO creado = service.alta(payload);
            return ResponseEntity.created(URI.create("/gestion/api/localidades/" + creado.getId())).body(creado);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody LocalidadDTO payload) {
        try {
            Optional<LocalidadDTO> actualizado = service.modificar(id, payload);
            return actualizado.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.baja(id);
            return ResponseEntity.noContent().build();
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

