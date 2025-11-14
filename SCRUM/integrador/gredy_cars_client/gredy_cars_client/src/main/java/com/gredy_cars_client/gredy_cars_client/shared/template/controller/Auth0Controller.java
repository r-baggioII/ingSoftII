package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PostLoginResponse;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.RegistroIntermedioDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.Auth0Service;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para manejar endpoints relacionados con Auth0
 * Este cliente NO valida JWT, solo actúa como proxy hacia el backend
 */
@RestController
@RequestMapping("/api/auth0")
@RequiredArgsConstructor
@Slf4j
public class Auth0Controller {
    
    private final Auth0Service auth0Service;
    
    /**
     * Endpoint post-login después de autenticación exitosa con Auth0
     * Recibe el access token desde el frontend y lo envía al backend
     * 
     * El token viene en el header Authorization: Bearer <token>
     */
    @PostMapping("/post-login")
    public ResponseEntity<PostLoginResponse> postLogin(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        try {
            // Extraer el token del header (formato: "Bearer <token>")
            String accessToken = authorizationHeader.replace("Bearer ", "");
            
            log.info("POST /api/auth0/post-login - Procesando autenticación");
            
            PostLoginResponse response = auth0Service.handlePostLogin(accessToken);
            
            if ("USER_EXISTS".equals(response.getStatus())) {
                return ResponseEntity.ok(response);
            } else if ("REQUIRED_MORE_INFO".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            log.error("Error en post-login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PostLoginResponse.builder()
                    .status("ERROR")
                    .message("Error interno del servidor")
                    .build());
        }
    }
    
    /**
     * Endpoint para completar registro intermedio
     * Toma el access token de la sesión HTTP (guardado durante el callback)
     */
    @PostMapping("/registro-intermedio")
    public ResponseEntity<Map<String, Object>> registroIntermedio(
            @RequestBody RegistroIntermedioDTO registroDTO,
            HttpSession session) {
        
        try {
            // Obtener el access token de la sesión
            String accessToken = (String) session.getAttribute("auth0_access_token");
            
            if (accessToken == null) {
                log.error("No se encontró access token en la sesión");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "success", false,
                        "message", "Sesión expirada. Por favor inicie sesión nuevamente."
                    ));
            }
            
            log.info("POST /api/auth0/registro-intermedio - Procesando registro para: {}", registroDTO.getEmail());
            log.debug("Access token (primeros 50 chars): {}", accessToken.substring(0, Math.min(50, accessToken.length())));
            
            Map<String, Object> result = auth0Service.completarRegistroIntermedio(accessToken, registroDTO);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                // Limpiar datos de Auth0 de la sesión ya que el registro se completó
                session.removeAttribute("auth0_access_token");
                session.removeAttribute("auth0_user_email");
                session.removeAttribute("auth0_user_sub");
                session.removeAttribute("auth0_email_verified");
                
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            log.error("Error en registro-intermedio: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor: " + e.getMessage()
                ));
        }
    }
}
