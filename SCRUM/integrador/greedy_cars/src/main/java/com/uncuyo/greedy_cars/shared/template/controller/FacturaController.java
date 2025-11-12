package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.DetalleFacturaDTO;
import com.uncuyo.greedy_cars.shared.template.dto.FacturaDTO;
import com.uncuyo.greedy_cars.shared.template.dto.FormaDePagoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoFactura;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.FacturaService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints para gestionar facturas.
 *
 * <p>
 * <strong>POST /api/facturas/dto</strong> &rarr; Endpoint recomendado para altas completas
 * (factura + detalles + formas de pago) en una única solicitud.
 * </p>
 * <p>
 * <strong>POST /api/facturas</strong> &rarr; Alta directa de la entidad, útil para integraciones
 * internas que gestionan los detalles y formas de pago en pasos separados.
 * </p>
 */
@RestController
@RequestMapping("/api/facturas")
public class FacturaController extends BaseRestController<Factura, String> {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        super(facturaService);
        this.facturaService = facturaService;
        initController(new Factura());
    }

    @GetMapping
    @Override
    public ResponseEntity<?> listar() {
        try {
            List<FacturaDTO> facturas = facturaService.listarActivosDTO();
            return ResponseEntity.ok(facturas);
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
            FacturaDTO factura = facturaService.obtenerDTO(id)
                    .orElseThrow(() -> new ErrorServiceException("Factura no encontrada con ID: " + id));
            return ResponseEntity.ok(factura);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/dto")
    public ResponseEntity<?> crearDTO(@Valid @RequestBody FacturaDTO dto) {
        try {
            FacturaDTO creada = facturaService.altaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/dto/{id}")
    public ResponseEntity<?> actualizarDTO(@PathVariable String id, @Valid @RequestBody FacturaDTO dto) {
        try {
            FacturaDTO actualizada = facturaService.modificarDTO(id, dto)
                    .orElseThrow(() -> new ErrorServiceException("Factura no encontrada con ID: " + id));
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
            facturaService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Factura eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> listarPorEstado(@PathVariable EstadoFactura estado) {
        try {
            List<FacturaDTO> facturas = facturaService.listarFacturaPorEstado(estado);
            return ResponseEntity.ok(facturas);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<?> listarDetalles(@PathVariable String id) {
        try {
            List<DetalleFacturaDTO> detalles = facturaService.listarDetalles(id);
            return ResponseEntity.ok(detalles);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/formas-pago")
    public ResponseEntity<?> listarFormasPago(@PathVariable String id) {
        try {
            List<FormaDePagoDTO> formas = facturaService.listarFormasPago(id);
            return ResponseEntity.ok(formas);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> descargarPdf(@PathVariable String id) {
        try {
            byte[] pdf = facturaService.generarFacturaPdf(id);
            String filename = "factura-" + id + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
