package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para servir las páginas principales de la aplicación.
 */
@Controller
public class PageController {

    /**
     * Página de acceso denegado
     */
    @GetMapping("/acceso-denegado")
    public String accessDenied() {
        return "acceso-denegado";
    }
}
