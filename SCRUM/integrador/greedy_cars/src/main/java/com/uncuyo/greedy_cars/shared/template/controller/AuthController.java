package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.config.security.JwtService;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.service.CustomUserDetailsService;
import com.uncuyo.greedy_cars.shared.template.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para autenticación de usuarios con JWT.
 * Maneja login y validación de credenciales.
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
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Endpoint para login de usuarios.
     * Valida las credenciales y devuelve un JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            String nombreUsuario = credentials.get("nombreUsuario");
            String clave = credentials.get("clave");

            if (nombreUsuario == null || clave == null) {
                return buildErrorResponse("Usuario y contraseña son obligatorios", HttpStatus.BAD_REQUEST);
            }

            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nombreUsuario, clave)
            );

            // Si la autenticación fue exitosa, generar JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(nombreUsuario);
            String jwt = jwtService.generateToken(userDetails);

            // Obtener información del usuario
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorNombreUsuario(nombreUsuario);
            if (usuarioOpt.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            Usuario usuario = usuarioOpt.get();

            // Crear cookie HttpOnly con el JWT (más seguro)
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // Cambiar a true en producción con HTTPS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 horas
            response.addCookie(jwtCookie);

            // Login exitoso - devolver JWT y información del usuario
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Login exitoso");
            responseData.put("token", jwt); // También enviar el token en el body por si se prefiere localStorage
            responseData.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombreUsuario", usuario.getNombreUsuario(),
                "rol", usuario.getRol().name()
            ));

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return buildErrorResponse("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Endpoint para logout.
     * Elimina la cookie del JWT.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            // Eliminar la cookie del JWT
            Cookie jwtCookie = new Cookie("jwt", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0); // Eliminar inmediatamente
            response.addCookie(jwtCookie);

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
     */
    @GetMapping("/verificar")
    public ResponseEntity<?> verificar(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                      @CookieValue(value = "jwt", required = false) String jwtFromCookie) {
        try {
            String jwt = null;

            // Intentar obtener el token del header Authorization
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            } else if (jwtFromCookie != null) {
                // Si no está en el header, usar el de la cookie
                jwt = jwtFromCookie;
            }

            if (jwt == null) {
                return buildErrorResponse("No se proporcionó token de autenticación", HttpStatus.UNAUTHORIZED);
            }

            // Validar el token
            String username = jwtService.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(jwt, userDetails)) {
                return buildErrorResponse("Token inválido o expirado", HttpStatus.UNAUTHORIZED);
            }

            // Obtener información del usuario
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
