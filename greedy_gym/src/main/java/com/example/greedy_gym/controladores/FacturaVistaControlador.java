package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Factura;
import com.example.greedy_gym.servicios.FacturaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FacturaVistaControlador {

    private final FacturaServicio facturaServicio;

    public FacturaVistaControlador(FacturaServicio facturaServicio) {
        this.facturaServicio = facturaServicio;
    }

    @GetMapping("/facturas/{id}/ver")
    public String verFactura(@PathVariable String id, Model model) {
        Factura factura = facturaServicio.buscar(id);
        model.addAttribute("factura", factura);
        return "factura";
    }
}

