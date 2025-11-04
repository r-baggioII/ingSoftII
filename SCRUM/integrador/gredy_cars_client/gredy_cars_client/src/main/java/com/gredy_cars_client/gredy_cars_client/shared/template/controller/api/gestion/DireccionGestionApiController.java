package com.gredy_cars_client.gredy_cars_client.shared.template.controller.api.gestion;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/gestion/api/direcciones")
@Validated
public class DireccionGestionApiController {

    private final DireccionService service;

    public DireccionGestionApiController(DireccionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DireccionDTO>> listar() throws ErrorServiceException {
        return ResponseEntity.ok(service.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionDTO> obtener(@PathVariable Long id) throws ErrorServiceException {
        Optional<DireccionDTO> dto = service.obtener(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody DireccionDTO payload) {
        try {
            DireccionDTO creado = service.alta(payload);
            return ResponseEntity.created(URI.create("/gestion/api/direcciones/" + creado.getId())).body(creado);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody DireccionDTO payload) {
        try {
            Optional<DireccionDTO> actualizado = service.modificar(id, payload);
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

