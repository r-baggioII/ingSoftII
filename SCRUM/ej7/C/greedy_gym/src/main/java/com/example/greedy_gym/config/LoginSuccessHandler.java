package com.example.greedy_gym.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.UsuarioRepositorio;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handler personalizado que se ejecuta después de un login exitoso.
 * Guarda el Usuario completo en la sesión (como lo hace tu LoginControlador)
 * y redirige según el rol.
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepositorio usuarioRepositorio;

    public LoginSuccessHandler(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       Authentication authentication) 
            throws IOException, ServletException {
        
        String nombreUsuario = authentication.getName();
        
        // Buscar el Usuario completo y guardarlo en sesión
        Usuario usuario = usuarioRepositorio.findByNombreUsuarioIgnoreCase(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después del login"));
        
        HttpSession session = request.getSession();
        session.setAttribute("usuario", usuario);
        session.setAttribute("nombreUsuario", usuario.getNombreUsuario());
        session.setAttribute("rol", usuario.getRol().toString());
        
        // Redirigir según el rol (igual que tu LoginControlador)
        String redirectUrl = switch (usuario.getRol()) {
            case ADMINISTRATIVO -> "/dashboard/admin";
            case PROFESOR -> "/dashboard/empleado";
            case SOCIO -> "/dashboard/socio";
        };
        
        response.sendRedirect(redirectUrl);
    }
}
