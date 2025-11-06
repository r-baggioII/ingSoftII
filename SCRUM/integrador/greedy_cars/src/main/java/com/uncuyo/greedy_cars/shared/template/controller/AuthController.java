package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.config.JwtUtil;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.service.CustomUserDetailsService;
import com.uncuyo.greedy_cars.shared.template.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para autenticación de usuarios con JWT.
 * Maneja login, logout y verificación de token. Entrega el JWT
 * en una cookie HttpOnly llamada "jwt" y también (opcionalmente)
 * puede aceptar tokens vía Authorization header.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://161.153.217.110:18082", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * DTO simple para login (espera JSON con {"nombreUsuario": "...", "clave":"..."}).
     */
    public static class LoginRequest {
        private String nombreUsuario;
        private String clave;

        public String getNombreUsuario() { return nombreUsuario; }
        public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

        public String getClave() { return clave; }
        public void setClave(String clave) { this.clave = clave; }
    }

    /**
     * Endpoint para login de usuarios.
     * Valida las credenciales y devuelve un JWT en cookie HttpOnly.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String nombreUsuario = request.getNombreUsuario();
        String clave = request.getClave();

        if (nombreUsuario == null || clave == null) {
            return buildErrorResponse("Usuario y contraseña son obligatorios", HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(nombreUsuario, clave)
            );

            // Extraer roles desde la autenticación
            @SuppressWarnings("unchecked")
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Generar token con JwtUtil (usa username y roles)
            String jwt = jwtUtil.generateToken(nombreUsuario, roles);

            // Buscar info del usuario para devolver datos útiles
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorNombreUsuario(nombreUsuario);
            if (usuarioOpt.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            Usuario usuario = usuarioOpt.get();

            // Crear cookie segura con SameSite controlado
            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(false) // cambiar a true en producción con HTTPS
                    .path("/")
                    .maxAge(24 * 60 * 60) // 24 horas
                    .sameSite("Lax") // Lax permite cookies en navegación normal del mismo sitio
                    .build();

            // Añadir header Set-Cookie
            response.addHeader("Set-Cookie", cookie.toString());

            // Respuesta: no incluyo el token en el body por seguridad (ya está en cookie HttpOnly)
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Login exitoso");
            responseData.put("usuario", Map.of(
                    "id", usuario.getId(),
                    "nombreUsuario", usuario.getNombreUsuario(),
                    "rol", usuario.getRol().name()
            ));

            return ResponseEntity.ok(responseData);

        } catch (AuthenticationException ex) {
            return buildErrorResponse("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return buildErrorResponse("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para logout. Elimina la cookie del JWT.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            // Invalidar cookie
            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(false) // true en producción
                    .path("/")
                    .maxAge(0)
                    .sameSite("None")
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Logout exitoso");

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return buildErrorResponse("Error al cerrar sesión: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para verificar si un token es válido.
     * Acepta token en header Authorization Bearer o en cookie "jwt".
     */
    @GetMapping("/verificar")
    public ResponseEntity<?> verificar(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                       @CookieValue(value = "jwt", required = false) String jwtFromCookie) {
        try {
            String token = null;

            // Priorizar header Authorization
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else if (jwtFromCookie != null && !jwtFromCookie.isBlank()) {
                token = jwtFromCookie;
            }

            if (token == null) {
                return buildErrorResponse("No se proporcionó token de autenticación", HttpStatus.UNAUTHORIZED);
            }

            // Validar token con JwtUtil
            if (!jwtUtil.validateToken(token)) {
                return buildErrorResponse("Token inválido o expirado", HttpStatus.UNAUTHORIZED);
            }

            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return buildErrorResponse("Token inválido: no contiene usuario", HttpStatus.UNAUTHORIZED);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Opcional: puedes revalidar más (p. ej. que roles coincidan), pero validateToken ya verificó firma/exp.
            if (userDetails == null) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            Optional<Usuario> usuarioOpt = usuarioService.buscarPorNombreUsuario(username);
            if (usuarioOpt.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            Usuario usuario = usuarioOpt.get();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("usuario", Map.of(
                    "id", usuario.getId(),
                    "nombreUsuario", usuario.getNombreUsuario(),
                    "rol", usuario.getRol().name()
            ));

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return buildErrorResponse("Error al verificar token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private ResponseEntity<?> buildErrorResponse(String mensaje, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", mensaje);
        return ResponseEntity.status(status).body(error);
    }
}
