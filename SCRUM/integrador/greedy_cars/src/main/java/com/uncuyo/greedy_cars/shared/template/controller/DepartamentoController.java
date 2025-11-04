package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.DepartamentoDTO;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @Autowired
    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<DepartamentoDTO> listaDepartamentosDTO = departamentoService.listarActivosDTO();
            return ResponseEntity.ok(listaDepartamentosDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            DepartamentoDTO departamentoDTO = departamentoService.obtenerDTO(id)
                .orElseThrow(() -> new ErrorServiceException("Departamento no encontrado con ID: " + id));
            return ResponseEntity.ok(departamentoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/provincia/{provinciaId}")
    public ResponseEntity<?> listarPorProvincia(@PathVariable Long provinciaId) {
        try {
            List<DepartamentoDTO> listaDepartamentosDTO = departamentoService.listarDepartamentoPorProvinciaActivoDTO(provinciaId);
            return ResponseEntity.ok(listaDepartamentosDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody DepartamentoDTO departamentoDTO) {
        try {
            // Crear usando el DTO
            DepartamentoDTO departamentoCreadoDTO = departamentoService.altaDTO(departamentoDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(departamentoCreadoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody DepartamentoDTO departamentoDTO) {
        try {
            // Actualizar usando el DTO
            DepartamentoDTO departamentoActualizadoDTO = departamentoService.modificarDTO(id, departamentoDTO)
                .orElseThrow(() -> new ErrorServiceException("Departamento no encontrado con ID: " + id));
            
            return ResponseEntity.ok(departamentoActualizadoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            departamentoService.baja(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Departamento eliminado correctamente");
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
