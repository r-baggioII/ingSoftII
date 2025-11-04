package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.PersonaDTO;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    @Autowired
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<PersonaDTO> listaPersonasDTO = personaService.listarActivosDTO();
            return ResponseEntity.ok(listaPersonasDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            PersonaDTO personaDTO = personaService.obtenerDTO(id)
                .orElseThrow(() -> new ErrorServiceException("Persona no encontrada con ID: " + id));
            return ResponseEntity.ok(personaDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PersonaDTO personaDTO) {
        try {
            PersonaDTO personaCreadaDTO = personaService.altaDTO(personaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(personaCreadaDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody PersonaDTO personaDTO) {
        try {
            PersonaDTO personaActualizadaDTO = personaService.modificarDTO(id, personaDTO)
                .orElseThrow(() -> new ErrorServiceException("Persona no encontrada con ID: " + id));
            return ResponseEntity.ok(personaActualizadaDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            personaService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Persona eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> buildErrorResponse(String mensaje, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(status).body(error);
    }
}
