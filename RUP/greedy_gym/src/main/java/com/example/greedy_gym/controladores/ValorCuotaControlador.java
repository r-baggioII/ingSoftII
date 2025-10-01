package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.ValorCuota;
import com.example.greedy_gym.servicios.ValorCuotaServicio;
import java.time.LocalDate;
import java.util.Collection;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/valor-cuotas")
public class ValorCuotaControlador {

    private final ValorCuotaServicio service;

    public ValorCuotaControlador(ValorCuotaServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ValorCuota crear(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                            @RequestParam double valorCuota) {
        return service.crearValorCuota(fechaDesde, fechaHasta, valorCuota);
    }

    @GetMapping("/{id}")
    public ValorCuota obtener(@PathVariable String id) {
        return service.buscarValorCuota(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                          @RequestParam double valorCuota) {
        service.modificarValorCuota(id, fechaDesde, fechaHasta, valorCuota);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarValorCuota(id);
    }

    @GetMapping
    public Collection<ValorCuota> listar() {
        return service.listarValorCuota();
    }

    @GetMapping("/activos")
    public Collection<ValorCuota> listarActivos() {
        return service.listarValorCuotaActivo();
    }

    @GetMapping("/vigente")
    public ValorCuota obtenerVigente() {
        return service.buscarValorCuotaVigente();
    }
}

