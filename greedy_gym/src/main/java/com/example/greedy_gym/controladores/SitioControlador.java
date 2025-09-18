package com.example.greedy_gym.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SitioControlador {

    @GetMapping({"/", "/inicio"})
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/panel/entidades")
    public String panelEntidades() {
        return "panel-entidades";
    }
}
