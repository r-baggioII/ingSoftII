package com.gredy_cars_client.gredy_cars_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para servir las páginas principales de la aplicación.
 */
@Controller
public class PageController {

    /**
     * Página de inicio
     */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
