package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.RegistroClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.RegistroService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para el registro público de nuevos clientes.
 * Este endpoint es accesible sin autenticación JWT.
 */
@Slf4j
@RestController
@RequestMapping("/api/registro")
public class RegistroController {

    private final RegistroService registroService;

    @Autowired
    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    /**
     * Endpoint público para registrar un nuevo cliente.
     * Crea automáticamente el Usuario, Cliente y todas sus entidades relacionadas.
     * 
     * @param dto DTO con todos los datos del registro
     * @return ResponseEntity con el resultado del registro
     */
    @PostMapping
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody RegistroClienteDTO dto) {
        log.info("Solicitud de registro recibida para usuario: {}", dto.getNombreUsuario());
        
        try {
            Cliente cliente = registroService.registrarCliente(dto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registro completado exitosamente. Ya puede iniciar sesión.");
            response.put("clienteId", cliente.getId());
            response.put("nombreUsuario", dto.getNombreUsuario());
            
            log.info("Registro exitoso para usuario: {}", dto.getNombreUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ErrorServiceException e) {
            log.warn("Error de validación en el registro: {}", e.getMessage());
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            log.error("Error inesperado durante el registro", e);
            return buildErrorResponse("Error al procesar el registro: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verifica si un nombre de usuario ya está en uso.
     * Útil para validación en tiempo real en el formulario.
     * 
     * @param nombreUsuario el nombre de usuario a verificar
     * @return ResponseEntity indicando si está disponible
     */
    @GetMapping("/verificar-usuario/{nombreUsuario}")
    public ResponseEntity<?> verificarDisponibilidadUsuario(@PathVariable String nombreUsuario) {
        try {
            // Este método podría implementarse en el servicio
            Map<String, Object> response = new HashMap<>();
            response.put("nombreUsuario", nombreUsuario);
            response.put("disponible", true); // Placeholder - implementar lógica real
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al verificar disponibilidad de usuario", e);
            return buildErrorResponse("Error al verificar disponibilidad", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Construye una respuesta de error estandarizada
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(error);
    }
}
