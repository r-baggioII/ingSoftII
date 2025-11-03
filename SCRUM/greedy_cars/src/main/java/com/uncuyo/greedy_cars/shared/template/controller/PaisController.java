package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.PaisDTO;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.PaisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paises")
public class PaisController {

    private final PaisService paisService;

    @Autowired
    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<PaisDTO> listaPaisesDTO = paisService.listarActivosDTO();
            return ResponseEntity.ok(listaPaisesDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            PaisDTO paisDTO = paisService.obtenerDTO(id)
                .orElseThrow(() -> new ErrorServiceException("País no encontrado con ID: " + id));
            return ResponseEntity.ok(paisDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PaisDTO paisDTO) {
        try {
            // Crear usando el DTO
            PaisDTO paisCreadoDTO = paisService.altaDTO(paisDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(paisCreadoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody PaisDTO paisDTO) {
        try {
            // Actualizar usando el DTO
            PaisDTO paisActualizadoDTO = paisService.modificarDTO(id, paisDTO)
                .orElseThrow(() -> new ErrorServiceException("País no encontrado con ID: " + id));
            
            return ResponseEntity.ok(paisActualizadoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            paisService.baja(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "País eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Método helper para construir respuestas de error
     */
    private ResponseEntity<?> buildErrorResponse(String mensaje, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(status).body(error);
    }
}
