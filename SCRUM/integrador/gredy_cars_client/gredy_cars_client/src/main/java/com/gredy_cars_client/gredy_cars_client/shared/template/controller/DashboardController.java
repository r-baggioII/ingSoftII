package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.config.AuthCheckInterceptor.UserDetailsWithRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página principal/dashboard (protegida por AuthCheckInterceptor)
 * Muestra un dashboard unificado con visibilidad basada en roles
 */
@Controller
public class DashboardController {

    @Value("${greedy.api.base-url}")
    private String backendBase;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Obtener información del usuario desde Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        String usuarioNombre = "Usuario";
        String usuarioRol = "CLIENTE";
        String usuarioId = "";
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsWithRole) {
            UserDetailsWithRole userDetails = (UserDetailsWithRole) authentication.getPrincipal();
            usuarioNombre = userDetails.getNombreUsuario();
            usuarioRol = userDetails.getRol();
            usuarioId = userDetails.getUsuarioId();
        }
        
        model.addAttribute("backendUrl", backendBase);
        model.addAttribute("usuarioNombre", usuarioNombre);
        model.addAttribute("usuarioRol", usuarioRol);
        model.addAttribute("usuarioId", usuarioId);
        
        // Retornar el dashboard unificado
        return "dashboard";
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("backendUrl", backendBase);
        return "index";
    }
}

