package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.FormaDePagoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.FormaDePago;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.FormaDePagoService;
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
@RequestMapping("/api/formas-pago")
public class FormaDePagoController extends BaseRestController<FormaDePago, String> {

    private final FormaDePagoService formaDePagoService;

    public FormaDePagoController(FormaDePagoService formaDePagoService) {
        super(formaDePagoService);
        this.formaDePagoService = formaDePagoService;
        initController(new FormaDePago());
    }

    @GetMapping
    @Override
    public ResponseEntity<?> listar() {
        try {
            List<FormaDePagoDTO> formas = formaDePagoService.listarActivosDTO();
            return ResponseEntity.ok(formas);
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
            FormaDePagoDTO dto = formaDePagoService.obtenerDTO(id)
                    .orElseThrow(() -> new ErrorServiceException("Forma de pago no encontrada con ID: " + id));
            return ResponseEntity.ok(dto);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/dto")
    public ResponseEntity<?> crearDTO(@Valid @RequestBody FormaDePagoDTO dto) {
        try {
            FormaDePagoDTO creada = formaDePagoService.altaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/dto/{id}")
    public ResponseEntity<?> actualizarDTO(@PathVariable String id, @Valid @RequestBody FormaDePagoDTO dto) {
        try {
            FormaDePagoDTO actualizada = formaDePagoService.modificarDTO(id, dto)
                    .orElseThrow(() -> new ErrorServiceException("Forma de pago no encontrada con ID: " + id));
            return ResponseEntity.ok(actualizada);
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
            formaDePagoService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Forma de pago eliminada correctamente");
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
            List<FormaDePagoDTO> formas = formaDePagoService.listarPorFactura(facturaId);
            return ResponseEntity.ok(formas);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
