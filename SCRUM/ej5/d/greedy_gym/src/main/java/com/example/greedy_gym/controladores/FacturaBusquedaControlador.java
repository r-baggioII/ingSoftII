package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.EstadoFactura;
import com.example.greedy_gym.entidades.Factura;
import com.example.greedy_gym.servicios.FacturaBusquedaServicio;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facturas2")
@CrossOrigin(origins = "*")
@Validated
public class FacturaBusquedaControlador {

    private final FacturaBusquedaServicio busquedaServicio;

    public FacturaBusquedaControlador(FacturaBusquedaServicio busquedaServicio) {
        this.busquedaServicio = busquedaServicio;
    }

    @GetMapping
    public ResponseEntity<Page<Factura>> listar(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(required = false) String sort,
                                                @RequestParam(required = false) EstadoFactura estado,
                                                @RequestParam(required = false) Long numero) {
        return ResponseEntity.ok(busquedaServicio.buscar(estado, numero, page, size, sort));
    }

    @GetMapping("/por-socio")
    public ResponseEntity<Page<Factura>> listarPorSocio(@RequestParam String idSocio,
                                                        @RequestParam(required = false) EstadoFactura estado,
                                                        @RequestParam(required = false) Long numero,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(busquedaServicio.buscarPorSocio(idSocio, estado, numero, page, size, sort));
    }
}
