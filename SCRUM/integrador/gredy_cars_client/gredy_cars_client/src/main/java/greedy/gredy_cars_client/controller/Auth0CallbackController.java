package greedy.gredy_cars_client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador que maneja el callback de Auth0 usando el flujo de Authorization Code.
 * 
 * Flujo:
 * 1. Usuario hace clic en "Login con Google/Facebook" → Auth0ViewController redirige a Auth0
 * 2. Usuario se autentica en Auth0 (Google/Facebook)
 * 3. Auth0 redirige a /auth0/callback?code=xxx&state=xxx
 * 4. Este controlador intercambia el 'code' por un access_token
 * 5. Obtiene la información del usuario de Auth0
 * 6. Llama al backend /api/auth0/post-login para verificar si el usuario existe
 * 7. Redirige a /dashboard (si existe) o /registro-intermedio (si es nuevo)
 */
@Controller
@RequestMapping("/auth0")
public class Auth0CallbackController {

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${greedy.api.base-url}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maneja el callback de Auth0 después de que el usuario se autentica.
     * 
     * @param code El código de autorización proporcionado por Auth0
     * @param error Error si la autenticación falló
     * @param errorDescription Descripción del error
     * @return RedirectView a dashboard o registro-intermedio
     */
    @GetMapping("/callback")
    public RedirectView handleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription) {

        // Si hay error, redirigir al login con mensaje
        if (error != null) {
            System.err.println("Error en Auth0 callback: " + error + " - " + errorDescription);
            return new RedirectView("/login?error=" + error);
        }

        // Si no hay código, algo salió mal
        if (code == null || code.isEmpty()) {
            System.err.println("No se recibió código de autorización");
            return new RedirectView("/login?error=no_code");
        }

        try {
            // 1. Intercambiar el código por un access token
            String accessToken = exchangeCodeForToken(code);
            
            // 2. Obtener información del usuario
            JsonNode userInfo = getUserInfo(accessToken);
            
            // 3. Llamar al backend para verificar si el usuario existe
            String postLoginUrl = apiBaseUrl + "/auth0/post-login";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                postLoginUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            Map<String, Object> result = response.getBody();
            
            if (result != null) {
                String status = (String) result.get("status");
                
                if ("USER_EXISTS".equals(status)) {
                    // Usuario existe, redirigir al dashboard
                    // TODO: Crear sesión JWT en el cliente
                    return new RedirectView("/cliente/dashboard");
                    
                } else if ("REQUIRED_MORE_INFO".equals(status)) {
                    // Usuario nuevo, redirigir a registro intermedio
                    // Guardar el access token en la sesión para usarlo después
                    // TODO: Pasar el token de forma segura
                    return new RedirectView("/auth0/registro-intermedio");
                }
            }
            
            // Si no reconocemos el status, error
            System.err.println("Status desconocido del backend: " + result);
            return new RedirectView("/login?error=unknown_status");
            
        } catch (Exception e) {
            System.err.println("Error procesando callback de Auth0: " + e.getMessage());
            e.printStackTrace();
            return new RedirectView("/login?error=callback_failed");
        }
    }

    /**
     * Intercambia el código de autorización por un access token.
     * 
     * @param code Código de autorización de Auth0
     * @return Access token
     */
    private String exchangeCodeForToken(String code) throws Exception {
        String tokenUrl = "https://" + auth0Domain + "/oauth/token";
        
        // Construir el redirect_uri (debe coincidir con el configurado en Auth0)
        String redirectUri = "http://161.153.217.110:18082/gredy_cars_client/auth0/callback";
        
        // Preparar el body para el POST
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        
        // Hacer la petición
        ResponseEntity<String> response = restTemplate.exchange(
            tokenUrl,
            HttpMethod.POST,
            entity,
            String.class
        );
        
        // Parsear la respuesta
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        String accessToken = jsonResponse.get("access_token").asText();
        
        System.out.println("Access token obtenido exitosamente");
        return accessToken;
    }

    /**
     * Obtiene la información del usuario desde Auth0 usando el access token.
     * 
     * @param accessToken Access token de Auth0
     * @return Información del usuario
     */
    private JsonNode getUserInfo(String accessToken) throws Exception {
        String userInfoUrl = "https://" + auth0Domain + "/userinfo";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            userInfoUrl,
            HttpMethod.GET,
            entity,
            String.class
        );
        
        JsonNode userInfo = objectMapper.readTree(response.getBody());
        System.out.println("User info obtenida: " + userInfo.toString());
        
        return userInfo;
    }
}
