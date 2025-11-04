package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gestion")
public class GestionGeograficaController {

    @GetMapping("/direcciones")
    public String gestionarDirecciones() {
        return "gestion/gestion-direcciones";
    }
}

