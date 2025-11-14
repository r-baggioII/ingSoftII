package com.gredy_cars_client.gredy_cars_client.shared.controller;

import com.gredy_cars_client.gredy_cars_client.shared.dto.PostLoginResponse;
import com.gredy_cars_client.gredy_cars_client.shared.dto.RegistroIntermedioDTO;
import com.gredy_cars_client.gredy_cars_client.shared.service.Auth0Service;
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
     * Recibe datos del cliente y el access token, los envía al backend
     */
    @PostMapping("/registro-intermedio")
    public ResponseEntity<Map<String, Object>> registroIntermedio(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RegistroIntermedioDTO registroDTO) {
        
        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            
            log.info("POST /api/auth0/registro-intermedio - Procesando registro");
            
            Map<String, Object> result = auth0Service.completarRegistroIntermedio(accessToken, registroDTO);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
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
