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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ProvinciaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ProvinciaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/gestion/api/provincias")
@Validated
public class ProvinciaGestionApiController {

    private final ProvinciaService service;

    public ProvinciaGestionApiController(ProvinciaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProvinciaDTO>> listar() throws ErrorServiceException {
        return ResponseEntity.ok(service.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvinciaDTO> obtener(@PathVariable Long id) throws ErrorServiceException {
        Optional<ProvinciaDTO> dto = service.obtener(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pais/{paisId}")
    public ResponseEntity<List<ProvinciaDTO>> listarPorPais(@PathVariable Long paisId) throws ErrorServiceException {
        List<ProvinciaDTO> todas = service.listarActivos();
        List<ProvinciaDTO> filtradas = todas.stream()
            .filter(p -> p.getPais() != null && p.getPais().getId() != null && p.getPais().getId().equals(paisId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(filtradas);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProvinciaDTO payload) {
        try {
            ProvinciaDTO creado = service.alta(payload);
            return ResponseEntity.created(URI.create("/gestion/api/provincias/" + creado.getId())).body(creado);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody ProvinciaDTO payload) {
        try {
            Optional<ProvinciaDTO> actualizado = service.modificar(id, payload);
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

