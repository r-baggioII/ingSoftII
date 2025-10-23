package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Factura;
import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.DetalleFactura;
import com.example.greedy_gym.servicios.FacturaServicio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FacturaVistaControlador {

    private final FacturaServicio facturaServicio;
    private final SocioRepositorio socioRepositorio;

    public FacturaVistaControlador(FacturaServicio facturaServicio, SocioRepositorio socioRepositorio) {
        this.facturaServicio = facturaServicio;
        this.socioRepositorio = socioRepositorio;
    }

    @GetMapping("/facturas/{id}/ver")
    public String verFactura(@PathVariable String id, Model model) {
        Factura factura = facturaServicio.buscar(id);
        model.addAttribute("factura", factura);
        // Derivar datos del socio a partir del primer detalle de la factura
        if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
            DetalleFactura primero = factura.getDetalles().get(0);
            if (primero != null && primero.getCuotaMensual() != null) {
                String socioId = primero.getCuotaMensual().getIdSocio();
                model.addAttribute("cuotaSocio", primero.getCuotaMensual());
                if (socioId != null && !socioId.isBlank()) {
                    socioRepositorio.findByIdAndEliminadoFalse(socioId)
                            .ifPresent(s -> model.addAttribute("socio", s));
                }
            }
        }
        return "factura";
    }
}

