package com.gredy_cars_client.gredy_cars_client.shared.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * Controlador que maneja el callback de Auth0 después de la autenticación.
 * Intercambia el código de autorización por un access token.
 */
@Controller
public class Auth0CallbackController {
    
    @Value("${auth0.domain}")
    private String auth0Domain;
    
    @Value("${auth0.clientId}")
    private String auth0ClientId;
    
    @Value("${auth0.clientSecret}")
    private String auth0ClientSecret;
    
    @Value("${auth0.audience}")
    private String auth0Audience;
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Maneja el callback de Auth0 después de la autenticación exitosa.
     * Intercambia el código por un access token y verifica si el usuario existe.
     */
    @GetMapping("/auth0/callback")
    public String handleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String error_description,
            HttpSession session,
            Model model) {
        
        // Si hay un error de Auth0
        if (error != null) {
            model.addAttribute("error", "Error de autenticación: " + error_description);
            return "redirect:/login?error=auth0";
        }
        
        // Si no hay código, redirigir al login
        if (code == null || code.isEmpty()) {
            return "redirect:/login?error=no_code";
        }
        
        try {
            // 1. Intercambiar código por token
            String redirectUri = "http://161.153.217.110:18082" + contextPath + "/auth0/callback";
            Map<String, Object> tokenResponse = exchangeCodeForToken(code, redirectUri);
            
            String accessToken = (String) tokenResponse.get("access_token");
            String idToken = (String) tokenResponse.get("id_token");
            
            // 2. Obtener información del usuario desde el id_token (decodificar)
            // Por simplicidad, vamos a llamar al endpoint /userinfo de Auth0
            Map<String, Object> userInfo = getUserInfo(accessToken);
            
            String sub = (String) userInfo.get("sub"); // externalId
            String email = (String) userInfo.get("email");
            Boolean emailVerified = (Boolean) userInfo.get("email_verified");
            
            // 3. Verificar si el usuario existe en nuestro backend
            String backendUrl = "http://161.153.217.110:18082/greedy_cars/api/auth0/post-login";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> backendResponse = restTemplate.exchange(
                backendUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            Map<String, Object> result = backendResponse.getBody();
            String status = (String) result.get("status");
            
            // 4. Decidir a dónde redirigir según el status
            if ("USER_EXISTS".equals(status)) {
                // Usuario existe - crear sesión y redirigir al dashboard
                session.setAttribute("auth0_access_token", accessToken);
                session.setAttribute("auth0_user_email", email);
                session.setAttribute("auth0_user_sub", sub);
                return "redirect:/cliente/dashboard";
            } else if ("REQUIRED_MORE_INFO".equals(status)) {
                // Usuario nuevo - redirigir a registro intermedio
                // Guardar información en la sesión HTTP (NO en la URL)
                session.setAttribute("auth0_access_token", accessToken);
                session.setAttribute("auth0_user_email", email);
                session.setAttribute("auth0_user_sub", sub);
                session.setAttribute("auth0_email_verified", emailVerified);
                
                return "redirect:/auth0/registro-intermedio";
            } else {
                model.addAttribute("error", "Estado desconocido del servidor");
                return "redirect:/login?error=unknown_status";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al procesar callback: " + e.getMessage());
            return "redirect:/login?error=callback_error";
        }
    }
    
    /**
     * Intercambia el código de autorización por un access token
     */
    private Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {
        String tokenUrl = "https://" + auth0Domain + "/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", auth0ClientId);
        body.add("client_secret", auth0ClientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return response.getBody();
    }
    
    /**
     * Obtiene la información del usuario desde Auth0
     */
    private Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = "https://" + auth0Domain + "/userinfo";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl,
            HttpMethod.GET,
            request,
            Map.class
        );
        
        return response.getBody();
    }
}
