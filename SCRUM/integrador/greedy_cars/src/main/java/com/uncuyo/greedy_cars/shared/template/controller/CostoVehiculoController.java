package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.CostoVehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.CostoVehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/costos-vehiculo")
public class CostoVehiculoController {

    private final CostoVehiculoService service;

    @Autowired
    public CostoVehiculoController(CostoVehiculoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(service.listarActivosDTO());
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            Optional<CostoVehiculoDTO> opt = service.obtenerDTO(id);
            if (opt.isPresent()) {
                return ResponseEntity.ok(opt.get());
            } else {
                return buildErrorResponse("Costo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CostoVehiculoDTO dto) {
        try {
            CostoVehiculoDTO creado = service.altaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody CostoVehiculoDTO dto) {
        try {
            CostoVehiculoDTO actualizado = service.modificarDTO(id, dto)
                    .orElseThrow(() -> new ErrorServiceException("Costo no encontrado con ID: " + id));
            return ResponseEntity.ok(actualizado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            service.eliminarCostoVehiculo(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Costo eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/vigente/{idCaracteristica}")
    public ResponseEntity<?> vigente(@PathVariable String idCaracteristica) {
        try {
            Optional<CostoVehiculoDTO> opt = service.buscarCostoVehiculoVigenteDTO(idCaracteristica);
            if (opt.isPresent()) {
                return ResponseEntity.ok(opt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No hay costo vigente"));
            }
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
