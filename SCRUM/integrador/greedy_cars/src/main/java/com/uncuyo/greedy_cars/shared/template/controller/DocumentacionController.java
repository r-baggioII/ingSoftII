package com.uncuyo.greedy_cars.shared.template.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uncuyo.greedy_cars.shared.template.dto.DocumentacionDTO;
import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumentacion;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.DocumentacionService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/documentacion")
public class DocumentacionController {
    
    private final DocumentacionService documentacionService;
    
    public DocumentacionController(DocumentacionService documentacionService) {
        this.documentacionService = documentacionService;
    }
    
    @GetMapping
    public ResponseEntity<?> listarActivos() {
        try {
            List<DocumentacionDTO> documentaciones = documentacionService.listarActivosDTO();
            return ResponseEntity.ok(documentaciones);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al listar documentaciones", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable String id) {
        try {
            Optional<DocumentacionDTO> documentacion = documentacionService.obtenerDTO(id);
            if (documentacion.isPresent()) {
                return ResponseEntity.ok(documentacion.get());
            } else {
                return buildErrorResponse("Documentación no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al obtener documentación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody DocumentacionDTO documentacionDTO) {
        try {
            DocumentacionDTO documentacionCreada = documentacionService.altaDTO(documentacionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(documentacionCreada);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al crear documentación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable String id, @Valid @RequestBody DocumentacionDTO documentacionDTO) {
        try {
            Optional<DocumentacionDTO> documentacionModificada = documentacionService.modificarDTO(id, documentacionDTO);
            if (documentacionModificada.isPresent()) {
                return ResponseEntity.ok(documentacionModificada.get());
            } else {
                return buildErrorResponse("Documentación no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al modificar documentación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            documentacionService.baja(id);
            return ResponseEntity.ok(Map.of("message", "Documentación eliminada correctamente"));
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al eliminar documentación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> buscarPorTipo(@PathVariable TipoDocumentacion tipo) {
        try {
            List<DocumentacionDTO> documentaciones = documentacionService.buscarPorTipoDTO(tipo);
            return ResponseEntity.ok(documentaciones);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al buscar documentaciones por tipo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
