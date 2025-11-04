package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.ContactoTelefonicoDTO;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.ContactoTelefonicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contactos-telefonicos")
public class ContactoTelefonicoController {

    private final ContactoTelefonicoService contactoTelefonicoService;

    @Autowired
    public ContactoTelefonicoController(ContactoTelefonicoService contactoTelefonicoService) {
        this.contactoTelefonicoService = contactoTelefonicoService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<ContactoTelefonicoDTO> listaContactosDTO = contactoTelefonicoService.listarActivosDTO();
            return ResponseEntity.ok(listaContactosDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            ContactoTelefonicoDTO contactoDTO = contactoTelefonicoService.obtenerDTO(id)
                .orElseThrow(() -> new ErrorServiceException("Contacto telefónico no encontrado con ID: " + id));
            return ResponseEntity.ok(contactoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ContactoTelefonicoDTO contactoTelefonicoDTO) {
        try {
            ContactoTelefonicoDTO contactoCreadoDTO = contactoTelefonicoService.altaDTO(contactoTelefonicoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(contactoCreadoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody ContactoTelefonicoDTO contactoTelefonicoDTO) {
        try {
            ContactoTelefonicoDTO contactoActualizadoDTO = contactoTelefonicoService.modificarDTO(id, contactoTelefonicoDTO)
                .orElseThrow(() -> new ErrorServiceException("Contacto telefónico no encontrado con ID: " + id));
            return ResponseEntity.ok(contactoActualizadoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            contactoTelefonicoService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Contacto telefónico eliminado correctamente");
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
