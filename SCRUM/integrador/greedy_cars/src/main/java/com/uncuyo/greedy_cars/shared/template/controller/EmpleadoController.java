package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.EmpleadoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Empleado;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController extends BaseRestController<Empleado, String> {

    private final EmpleadoService empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService) {
        super(empleadoService);
        this.empleadoService = empleadoService;
        initController(new Empleado());
    }

    @GetMapping
    @Override
    public ResponseEntity<?> listar() {
        try {
            List<EmpleadoDTO> lista = empleadoService.listarActivosDTO();
            return ResponseEntity.ok(lista);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            EmpleadoDTO dto = empleadoService.obtenerDTO(id)
                    .orElseThrow(() -> new ErrorServiceException("Empleado no encontrado con ID: " + id));
            return ResponseEntity.ok(dto);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new Empleado. 
     * Override to prevent direct entity creation - must use DTO
     */
    @PostMapping
    @Override
    public ResponseEntity<?> crear(@Valid @RequestBody Empleado entity) {
        // Since we can't distinguish between Empleado and EmpleadoDTO at compile time,
        // we treat all POST requests as DTO format
        try {
            // The incoming JSON with direccionIds, contactoIds, etc will be handled by
            // a custom converter or we redirect to the service that expects a DTO
            return buildErrorResponse("Please use the correct DTO format with relationship IDs", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new Empleado using explicit DTO endpoint
     */
    @PostMapping("/new")
    public ResponseEntity<?> crearDTO(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
        try {
            EmpleadoDTO creado = empleadoService.altaDTO(empleadoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * Update an existing Empleado. 
     * Override to prevent direct entity update - must use DTO
     */
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody Empleado entity) {
        return buildErrorResponse("Please use the correct DTO format with relationship IDs", HttpStatus.BAD_REQUEST);
    }

    /**
     * Update an existing Empleado using explicit DTO endpoint
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> actualizarDTO(@PathVariable String id, @Valid @RequestBody EmpleadoDTO empleadoDTO) {
        try {
            EmpleadoDTO actualizado = empleadoService.modificarDTO(id, empleadoDTO)
                    .orElseThrow(() -> new ErrorServiceException("Empleado no encontrado con ID: " + id));
            return ResponseEntity.ok(actualizado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            empleadoService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Empleado eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
