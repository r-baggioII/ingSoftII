package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.DetalleFacturaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.DetalleFactura;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.DetalleFacturaService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles-factura")
public class DetalleFacturaController extends BaseRestController<DetalleFactura, String> {

    private final DetalleFacturaService detalleFacturaService;

    public DetalleFacturaController(DetalleFacturaService detalleFacturaService) {
        super(detalleFacturaService);
        this.detalleFacturaService = detalleFacturaService;
        initController(new DetalleFactura());
    }

    @GetMapping
    @Override
    public ResponseEntity<?> listar() {
        try {
            List<DetalleFacturaDTO> detalles = detalleFacturaService.listarActivosDTO();
            return ResponseEntity.ok(detalles);
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
            DetalleFacturaDTO dto = detalleFacturaService.obtenerDTO(id)
                    .orElseThrow(() -> new ErrorServiceException("Detalle de factura no encontrado con ID: " + id));
            return ResponseEntity.ok(dto);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/dto")
    public ResponseEntity<?> crearDTO(@Valid @RequestBody DetalleFacturaDTO dto) {
        try {
            DetalleFacturaDTO creado = detalleFacturaService.altaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/dto/{id}")
    public ResponseEntity<?> actualizarDTO(@PathVariable String id, @Valid @RequestBody DetalleFacturaDTO dto) {
        try {
            DetalleFacturaDTO actualizado = detalleFacturaService.modificarDTO(id, dto)
                    .orElseThrow(() -> new ErrorServiceException("Detalle de factura no encontrado con ID: " + id));
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
            detalleFacturaService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Detalle de factura eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<?> listarPorFactura(@PathVariable String facturaId) {
        try {
            List<DetalleFacturaDTO> detalles = detalleFacturaService.listarPorFactura(facturaId);
            return ResponseEntity.ok(detalles);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
