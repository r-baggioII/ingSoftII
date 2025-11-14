package com.gredy_cars_client.gredy_cars_client.shared.service;

import com.gredy_cars_client.gredy_cars_client.shared.dto.PostLoginResponse;
import com.gredy_cars_client.gredy_cars_client.shared.dto.RegistroIntermedioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para manejar la integración con Auth0
 * Actúa como proxy: recibe access_token del frontend y lo envía al backend
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Auth0Service {
    
    private final RestTemplate restTemplate;
    
    @Value("${greedy.api.base-url}")
    private String apiBaseUrl;
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    /**
     * Maneja el post-login después de autenticación exitosa con Auth0
     * Envía el access_token al backend para verificar si el usuario existe
     * 
     * @param accessToken Token de acceso de Auth0
     * @return PostLoginResponse con instrucciones para el frontend
     */
    public PostLoginResponse handlePostLogin(String accessToken) {
        try {
            log.info("Post-login con Auth0, enviando al backend para verificación");
            
            // Llamar al backend enviando el access_token
            String postLoginUrl = apiBaseUrl + "/auth0/post-login";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<PostLoginResponse> response = restTemplate.exchange(
                postLoginUrl,
                HttpMethod.POST,
                request,
                PostLoginResponse.class
            );
            
            PostLoginResponse result = response.getBody();
            
            if (result != null) {
                // Ajustar redirectTo con el context path del cliente
                if (result.getRedirectTo() != null && !result.getRedirectTo().startsWith("http")) {
                    result.setRedirectTo(contextPath + result.getRedirectTo());
                }
                return result;
            } else {
                return PostLoginResponse.builder()
                    .status("ERROR")
                    .message("No se recibió respuesta del backend")
                    .redirectTo(contextPath + "/login")
                    .build();
            }
            
        } catch (Exception e) {
            log.error("Error en handlePostLogin: {}", e.getMessage(), e);
            
            return PostLoginResponse.builder()
                .status("ERROR")
                .message("Error al verificar el usuario: " + e.getMessage())
                .redirectTo(contextPath + "/login")
                .build();
        }
    }
    
    /**
     * Completa el registro de un usuario autenticado con Auth0
     * Envía los datos del cliente y el access_token al backend
     * 
     * @param accessToken Token de acceso de Auth0
     * @param registroDTO Datos del cliente a registrar
     * @return Mapa con resultado del registro
     */
    public Map<String, Object> completarRegistroIntermedio(String accessToken, RegistroIntermedioDTO registroDTO) {
        try {
            log.info("Completando registro intermedio, enviando al backend");
            
            // Preparar request al backend
            String registroUrl = apiBaseUrl + "/auth0/registro-intermedio";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // El payload incluye los datos del formulario
            HttpEntity<RegistroIntermedioDTO> request = new HttpEntity<>(registroDTO, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                registroUrl,
                request,
                Map.class
            );
            
            Map<String, Object> result = response.getBody();
            
            if (response.getStatusCode().is2xxSuccessful() && result != null) {
                log.info("Registro intermedio completado exitosamente");
                result.put("success", true);
                return result;
            } else {
                log.warn("Error en registro intermedio: {}", result);
                return Map.of(
                    "success", false,
                    "message", "Error al completar el registro"
                );
            }
            
        } catch (Exception e) {
            log.error("Error en completarRegistroIntermedio: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "Error al completar el registro: " + e.getMessage()
            );
        }
    }
}
