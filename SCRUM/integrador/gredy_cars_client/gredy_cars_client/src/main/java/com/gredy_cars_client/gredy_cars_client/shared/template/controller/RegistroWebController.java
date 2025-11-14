package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.RegistroDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.RegistroClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador web para la página de registro de clientes.
 * Esta página es pública y no requiere autenticación.
 */
@Controller
@RequestMapping("/registro")
public class RegistroWebController {

    private static final Logger log = LoggerFactory.getLogger(RegistroWebController.class);

    @Autowired
    private RegistroDao registroDao;

    /**
     * Muestra el formulario de registro de cliente
     * 
     * @param model el modelo de la vista
     * @return la vista de registro
     */
    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("titulo", "Registro de Cliente - Greedy Cars");
        return "registro-cliente";
    }

    /**
     * Procesa el registro de un nuevo cliente.
     * Este endpoint recibe el JSON del formulario y lo envía al backend.
     *
     * @param dto datos del registro
     * @return respuesta JSON con el resultado
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registrarCliente(@RequestBody RegistroClienteDTO dto) {
        log.info("Recibida solicitud de registro para usuario: {}", dto.getNombreUsuario());

        try {
            // Llamar al DAO que consume el endpoint REST del backend
            Map<String, Object> resultado = registroDao.registrarCliente(dto);

            log.info("Registro exitoso para usuario: {}", dto.getNombreUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);

        } catch (ErrorServiceException e) {
            log.warn("Error al registrar cliente: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            log.error("Error inesperado al registrar cliente", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error inesperado al procesar el registro: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verifica la disponibilidad de un nombre de usuario.
     *
     * @param nombreUsuario el nombre de usuario a verificar
     * @return respuesta JSON indicando disponibilidad
     */
    @GetMapping("/verificar-usuario/{nombreUsuario}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarUsuario(@PathVariable String nombreUsuario) {
        try {
            Map<String, Object> resultado = registroDao.verificarDisponibilidadUsuario(nombreUsuario);
            return ResponseEntity.ok(resultado);
        } catch (ErrorServiceException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
