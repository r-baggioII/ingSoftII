package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página principal/dashboard (protegida por AuthCheckInterceptor)
 * Redirige al index.html existente que actúa como dashboard
 */
@Controller
public class DashboardController {

    @Value("${greedy.api.base-url}")
    private String backendBase;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Mostrar el dashboard de gestión del jefe
        model.addAttribute("backendUrl", backendBase);
        return "dashboard-gestion-jefe";
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("backendUrl", backendBase);
        return "index";
    }
}
