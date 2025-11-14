package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.PromocionDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Promocion;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.PromocionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/promociones")
public class PromocionController extends BaseRestController<Promocion, String> {

    private static final Logger log = LoggerFactory.getLogger(PromocionController.class);

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        super(promocionService);
        this.promocionService = promocionService;
        initController(new Promocion());
    }

    @Override
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<PromocionDTO> promociones = promocionService.listarActivosDTO();
            return ResponseEntity.ok(promociones);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            PromocionDTO dto = promocionService.obtenerDTO(id)
                    .orElseThrow(() -> new ErrorServiceException("Promoción no encontrada con ID: " + id));
            return ResponseEntity.ok(dto);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/dto")
    public ResponseEntity<?> crearDTO(@Valid @RequestBody PromocionDTO dto) {
        try {
            PromocionDTO creada = promocionService.altaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (ErrorServiceException e) {
            log.warn("Error de negocio al dar de alta promoción: {}", e.getMessage(), e);
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error de sistema al dar de alta promoción", e);
            return buildErrorResponse("Error inesperado al dar de alta la promoción", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/dto/{id}")
    public ResponseEntity<?> actualizarDTO(@PathVariable String id, @Valid @RequestBody PromocionDTO dto) {
        try {
            PromocionDTO actualizada = promocionService.modificarDTO(id, dto)
                    .orElseThrow(() -> new ErrorServiceException("Promoción no encontrada con ID: " + id));
            return ResponseEntity.ok(actualizada);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            promocionService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Promoción eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            PromocionDTO dto = promocionService.obtenerPorCodigoDTO(codigo);
            return ResponseEntity.ok(dto);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/vigentes")
    public ResponseEntity<?> listarVigentes() {
        try {
            List<PromocionDTO> promociones = promocionService.listarVigentesDTO(LocalDate.now());
            return ResponseEntity.ok(promociones);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/vigentes/cliente/{clienteId}")
    public ResponseEntity<?> listarVigentesPorCliente(@PathVariable String clienteId) {
        try {
            List<PromocionDTO> promociones = promocionService.listarVigentesParaCliente(clienteId, LocalDate.now());
            return ResponseEntity.ok(promociones);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
