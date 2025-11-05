package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.CaracteristicaVehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.CaracteristicaVehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/caracteristicas-vehiculo")
public class CaracteristicaVehiculoController {

    private final CaracteristicaVehiculoService service;

    @Autowired
    public CaracteristicaVehiculoController(CaracteristicaVehiculoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<CaracteristicaVehiculoDTO> lista = service.listarActivosDTO();
            return ResponseEntity.ok(lista);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            CaracteristicaVehiculoDTO dto = service.obtenerDTO(id)
                    .orElseThrow(() -> new ErrorServiceException("Característica no encontrada con ID: " + id));
            return ResponseEntity.ok(dto);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CaracteristicaVehiculoDTO dto) {
        try {
            CaracteristicaVehiculoDTO creado = service.altaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody CaracteristicaVehiculoDTO dto) {
        try {
            CaracteristicaVehiculoDTO actualizado = service.modificarDTO(id, dto)
                    .orElseThrow(() -> new ErrorServiceException("Característica no encontrada con ID: " + id));
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
            service.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Característica eliminada correctamente");
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
