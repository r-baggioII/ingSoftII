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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DepartamentoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DepartamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/gestion/api/departamentos")
@Validated
public class DepartamentoGestionApiController {

    private final DepartamentoService service;

    public DepartamentoGestionApiController(DepartamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoDTO>> listar() throws ErrorServiceException {
        return ResponseEntity.ok(service.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> obtener(@PathVariable Long id) throws ErrorServiceException {
        Optional<DepartamentoDTO> dto = service.obtener(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/provincia/{provinciaId}")
    public ResponseEntity<List<DepartamentoDTO>> listarPorProvincia(@PathVariable Long provinciaId) throws ErrorServiceException {
        List<DepartamentoDTO> todos = service.listarActivos();
        List<DepartamentoDTO> filtrados = todos.stream()
            .filter(d -> d.getProvincia() != null && d.getProvincia().getId() != null && d.getProvincia().getId().equals(provinciaId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(filtrados);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody DepartamentoDTO payload) {
        try {
            DepartamentoDTO creado = service.alta(payload);
            return ResponseEntity.created(URI.create("/gestion/api/departamentos/" + creado.getId())).body(creado);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody DepartamentoDTO payload) {
        try {
            Optional<DepartamentoDTO> actualizado = service.modificar(id, payload);
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

