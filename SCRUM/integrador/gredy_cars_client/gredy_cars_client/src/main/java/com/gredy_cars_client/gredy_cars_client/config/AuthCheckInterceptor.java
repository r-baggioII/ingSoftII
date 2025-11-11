package com.gredy_cars_client.gredy_cars_client.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.Map;

/**
 * Interceptor que verifica la autenticación antes de permitir acceso a rutas protegidas.
 * Llama al endpoint /api/auth/verificar del backend enviando la cookie JWT.
 */
public class AuthCheckInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthCheckInterceptor.class);

    private final RestTemplate restTemplate;
    private final String backendVerifyUrl;

    public AuthCheckInterceptor(RestTemplate restTemplate, String backendVerifyUrl) {
        this.restTemplate = restTemplate;
        this.backendVerifyUrl = backendVerifyUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info(">>> AuthCheckInterceptor: Verificando acceso a: {}", uri);

        // Rutas públicas que no requieren autenticación
        if (isPublicPath(uri)) {
            log.info(">>> Ruta pública, permitiendo acceso: {}", uri);
            return true;
        }

        // Extraer cookie del request del navegador
        String cookieHeader = request.getHeader("Cookie");
        log.info(">>> Cookie header recibida: {}", cookieHeader != null ? cookieHeader.substring(0, Math.min(100, cookieHeader.length())) : "NULL");
        
        if (cookieHeader == null || !cookieHeader.contains("jwt=")) {
            log.warn(">>> No se encontró cookie JWT para la ruta protegida: {}", uri);
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Verificar con el backend
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.COOKIE, cookieHeader);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            log.info(">>> Verificando JWT con backend: {}", backendVerifyUrl);
            ResponseEntity<Map> resp = restTemplate.exchange(
                backendVerifyUrl, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            log.info(">>> Backend respondió con status: {}", resp.getStatusCode());
            
            if (resp.getStatusCode().is2xxSuccessful()) {
                log.info(">>> Autenticación exitosa para ruta: {}", uri);
                
                // Extraer información del usuario y configurar Spring Security
                Map<String, Object> body = resp.getBody();
                if (body != null && body.containsKey("usuario")) {
                    Map<String, Object> usuario = (Map<String, Object>) body.get("usuario");
                    String nombreUsuario = (String) usuario.get("nombreUsuario");
                    String rol = (String) usuario.get("rol");
                    String usuarioId = (String) usuario.get("id");
                    
                    // Crear un UserDetails personalizado con la información del usuario
                    UserDetailsWithRole userDetails = new UserDetailsWithRole(
                        nombreUsuario, 
                        rol,
                        usuarioId
                    );
                    
                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
                        );
                    
                    // Establecer en el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.info(">>> Información del usuario guardada en SecurityContext: {} con rol: {}", 
                            nombreUsuario, rol);
                }
                
                return true; // Usuario autenticado
            } else {
                log.warn(">>> Backend rechazó autenticación con status: {}", resp.getStatusCode());
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        } catch (Exception e) {
            log.error(">>> Error al verificar autenticación con backend para ruta: {} - Error: {}", uri, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
    }

    /**
     * Determina si una ruta es pública (no requiere autenticación)
     */
    private boolean isPublicPath(String uri) {
        return uri.startsWith("/login") 
            || uri.startsWith("/css/") 
            || uri.startsWith("/js/") 
            || uri.startsWith("/images/") 
            || uri.startsWith("/public/")
            || uri.startsWith("/vendor/")
            || uri.startsWith("/fonts/")
            || uri.startsWith("/style.css")
            || uri.equals("/")
            || uri.equals("/index")
            || uri.startsWith("/registro")
            || uri.startsWith("/error")
            || uri.startsWith("/webjars/")
            || uri.startsWith("/favicon.ico");
    }
    
    /**
     * Clase interna para almacenar información del usuario en Spring Security
     */
    public static class UserDetailsWithRole {
        private final String nombreUsuario;
        private final String rol;
        private final String usuarioId;
        
        public UserDetailsWithRole(String nombreUsuario, String rol, String usuarioId) {
            this.nombreUsuario = nombreUsuario;
            this.rol = rol;
            this.usuarioId = usuarioId;
        }
        
        public String getNombreUsuario() {
            return nombreUsuario;
        }
        
        public String getRol() {
            return rol;
        }
        
        public String getUsuarioId() {
            return usuarioId;
        }
    }
}
