package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.ContactoCorreoElectronicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contactos-correos")
public class ContactoCorreoElectronicoController {

    private final ContactoCorreoElectronicoService contactoCorreoElectronicoService;

    @Autowired
    public ContactoCorreoElectronicoController(ContactoCorreoElectronicoService contactoCorreoElectronicoService) {
        this.contactoCorreoElectronicoService = contactoCorreoElectronicoService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<ContactoCorreoElectronicoDTO> listaContactosDTO = contactoCorreoElectronicoService.listarActivosDTO();
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
            ContactoCorreoElectronicoDTO contactoDTO = contactoCorreoElectronicoService.obtenerDTO(id)
                .orElseThrow(() -> new ErrorServiceException("Contacto de correo electrónico no encontrado con ID: " + id));
            return ResponseEntity.ok(contactoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ContactoCorreoElectronicoDTO contactoCorreoElectronicoDTO) {
        try {
            ContactoCorreoElectronicoDTO contactoCreadoDTO = contactoCorreoElectronicoService.altaDTO(contactoCorreoElectronicoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(contactoCreadoDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody ContactoCorreoElectronicoDTO contactoCorreoElectronicoDTO) {
        try {
            ContactoCorreoElectronicoDTO contactoActualizadoDTO = contactoCorreoElectronicoService.modificarDTO(id, contactoCorreoElectronicoDTO)
                .orElseThrow(() -> new ErrorServiceException("Contacto de correo electrónico no encontrado con ID: " + id));
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
            contactoCorreoElectronicoService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Contacto de correo electrónico eliminado correctamente");
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
