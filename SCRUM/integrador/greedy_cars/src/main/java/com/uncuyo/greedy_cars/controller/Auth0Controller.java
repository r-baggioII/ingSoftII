package com.uncuyo.greedy_cars.controller;

import com.uncuyo.greedy_cars.service.Auth0RegistrationService;
import com.uncuyo.greedy_cars.shared.auth0.dto.*;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para manejar el flujo de autenticación con Auth0.
 * Este controlador recibe tokens JWT ya validados por Spring Security OAuth2 Resource Server.
 */
@RestController
@RequestMapping("/api/auth0")
public class Auth0Controller {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private Auth0RegistrationService auth0RegistrationService;
    
    /**
     * Verifica si un usuario existe por externalId o email.
     * Endpoint protegido - requiere token Auth0 válido.
     * 
     * @param request CheckUserRequest con externalId y/o email
     * @return CheckUserResponse indicando si el usuario existe
     */
    @PostMapping("/check-user")
    public ResponseEntity<CheckUserResponse> checkUser(@RequestBody CheckUserRequest request) {
        boolean exists = false;
        
        // Primero buscar por externalId si está presente
        if (request.getAuth0Sub() != null && !request.getAuth0Sub().isEmpty()) {
            exists = usuarioService.findByExternalId(request.getAuth0Sub()).isPresent();
        }
        
        // Si no existe, buscar por email
        if (!exists && request.getEmail() != null && !request.getEmail().isEmpty()) {
            exists = usuarioService.findByEmail(request.getEmail()).isPresent();
        }
        
        CheckUserResponse response = new CheckUserResponse();
        response.setExists(exists);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Maneja el post-login después de autenticación exitosa con Auth0.
     * Extrae los claims del JWT y determina si el usuario necesita completar registro.
     * 
     * @param authentication Spring Security Authentication con el JWT
     * @return PostLoginResponse con status y datos del usuario/token
     */
    @PostMapping("/post-login")
    public ResponseEntity<PostLoginResponse> postLogin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        
        // Extraer claims del token Auth0
        String externalId = jwt.getSubject(); // "auth0|..." o "google-oauth2|..."
        String email = jwt.getClaim("email");
        Boolean emailVerified = jwt.getClaim("email_verified");
        String provider = extractProvider(externalId);
        
        // Buscar usuario por externalId
        Optional<Usuario> usuarioOpt = usuarioService.findByExternalId(externalId);
        
        PostLoginResponse response = new PostLoginResponse();
        
        if (usuarioOpt.isPresent()) {
            // Usuario ya existe - login exitoso
            Usuario usuario = usuarioOpt.get();
            response.setStatus("USER_EXISTS");
            response.setEmail(usuario.getEmail());
            response.setExternalId(usuario.getExternalId());
            response.setToken(jwt.getTokenValue());
            
            // Si el usuario tiene un Cliente asociado, incluir info adicional
            if (usuario.getPersona() instanceof Cliente) {
                Cliente cliente = (Cliente) usuario.getPersona();
                response.setNombre(cliente.getNombre());
                response.setApellido(cliente.getApellido());
            }
        } else {
            // Usuario no existe - requiere completar registro
            response.setStatus("REQUIRED_MORE_INFO");
            response.setEmail(email);
            response.setExternalId(externalId);
            response.setProvider(provider);
            response.setEmailVerified(emailVerified != null ? emailVerified : false);
            response.setToken(jwt.getTokenValue());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Login o creación automática de usuario con Auth0.
     * Si el usuario existe, devuelve sus datos.
     * Si no existe, lo crea automáticamente con rol CLIENTE.
     * 
     * @param requestBody Body con email, externalId, emailVerified
     * @param authentication Spring Security Authentication con el JWT
     * @return PostLoginResponse con datos del usuario y JWT interno
     */
    @PostMapping("/login-or-create")
    public ResponseEntity<PostLoginResponse> loginOrCreate(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {
        
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        
        // Extraer datos del body (porque el JWT de Auth0 no incluye email en claims)
        String externalId = (String) requestBody.get("externalId");
        String email = (String) requestBody.get("email");
        Boolean emailVerified = (Boolean) requestBody.get("emailVerified");
        
        if (externalId == null || email == null) {
            externalId = jwt.getSubject(); // Fallback al JWT
        }
        
        String provider = extractProvider(externalId);
        
        // Buscar usuario por externalId
        Optional<Usuario> usuarioOpt = usuarioService.findByExternalId(externalId);
        
        Usuario usuario;
        
        if (usuarioOpt.isPresent()) {
            // Usuario ya existe
            usuario = usuarioOpt.get();
        } else {
            // Usuario no existe - crearlo automáticamente
            try {
                usuario = auth0RegistrationService.crearUsuarioBasicoAuth0(
                    externalId, 
                    email, 
                    provider, 
                    emailVerified != null ? emailVerified : false
                );
            } catch (Exception e) {
                e.printStackTrace(); // Log completo del error
                System.err.println("ERROR creando usuario Auth0: " + e.getClass().getName() + " - " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al crear usuario: " + e.getMessage()));
            }
        }
        
        // Generar JWT interno del sistema
        try {
            String internalJwt = usuarioService.generateInternalJwt(usuario);
            
            PostLoginResponse response = new PostLoginResponse();
            response.setStatus("SUCCESS");
            response.setEmail(usuario.getEmail());
            response.setExternalId(usuario.getExternalId());
            response.setToken(internalJwt); // JWT interno, no el de Auth0
            response.setUsuarioId(usuario.getId());
            response.setRol(usuario.getRol().name());
            
            if (usuario.getPersona() instanceof Cliente) {
                Cliente cliente = (Cliente) usuario.getPersona();
                response.setNombre(cliente.getNombre() != null ? cliente.getNombre() : email);
                response.setApellido(cliente.getApellido());
                response.setClienteId(cliente.getId());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR generando JWT o respuesta: " + e.getClass().getName() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al generar JWT: " + e.getMessage()));
        }
    }
    
    /**
     * Completa el registro de un nuevo usuario autenticado con Auth0.
     * Crea el Cliente con todos los datos necesarios y asocia el externalId.
     * 
     * @param dto RegistroIntermedioDTO con todos los datos del cliente
     * @param authentication Spring Security Authentication con el JWT
     * @return PostLoginResponse con el usuario creado
     */
    @PostMapping("/registro-intermedio")
    public ResponseEntity<PostLoginResponse> registroIntermedio(
            @RequestBody RegistroIntermedioDTO dto,
            Authentication authentication) {
        
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String externalId = jwt.getSubject();
        
        // Verificar que no exista ya un usuario con este externalId
        if (usuarioService.findByExternalId(externalId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse("Usuario ya registrado"));
        }
        
        try {
            // Crear el cliente completo con asociación Auth0
            Usuario usuario = auth0RegistrationService.crearClienteConAuth0(dto, externalId);
            
            PostLoginResponse response = new PostLoginResponse();
            response.setStatus("USER_CREATED");
            response.setEmail(usuario.getEmail());
            response.setExternalId(usuario.getExternalId());
            response.setToken(jwt.getTokenValue());
            
            if (usuario.getPersona() instanceof Cliente) {
                Cliente cliente = (Cliente) usuario.getPersona();
                response.setNombre(cliente.getNombre());
                response.setApellido(cliente.getApellido());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al crear usuario: " + e.getMessage()));
        }
    }
    
    /**
     * Extrae el proveedor del externalId de Auth0.
     * Ejemplos: "auth0|123" -> "auth0", "google-oauth2|123" -> "google"
     */
    private String extractProvider(String externalId) {
        if (externalId == null) return "unknown";
        
        if (externalId.startsWith("google-oauth2|")) return "google";
        if (externalId.startsWith("facebook|")) return "facebook";
        if (externalId.startsWith("twitter|")) return "twitter";
        if (externalId.startsWith("github|")) return "github";
        if (externalId.startsWith("auth0|")) return "auth0";
        
        return externalId.split("\\|")[0];
    }
    
    /**
     * Crea una respuesta de error
     */
    private PostLoginResponse createErrorResponse(String message) {
        PostLoginResponse response = new PostLoginResponse();
        response.setStatus("ERROR");
        // Usar un campo disponible para el mensaje de error
        return response;
    }
}
