package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.EstadoCuota;
import com.example.greedy_gym.entidades.Mes;
import com.example.greedy_gym.servicios.CuotaMensualServicio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import org.springframework.web.server.ResponseStatusException;
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
@RequestMapping("/api/cuotas-mensuales")
public class CuotaMensualControlador {

    private final CuotaMensualServicio service;
    private final SocioRepositorio socioRepositorio;

    public CuotaMensualControlador(CuotaMensualServicio service, SocioRepositorio socioRepositorio) {
        this.service = service;
        this.socioRepositorio = socioRepositorio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuotaMensual crear(@RequestParam(required = false) String idSocio,
                              @RequestParam(required = false) String numeroDocumento,
                              @RequestParam Mes mes,
                              @RequestParam Long anio,
                              @RequestParam String idValorCuota) {
        String socioId = idSocio;
        if ((socioId == null || socioId.isBlank()) && numeroDocumento != null && !numeroDocumento.isBlank()) {
            socioId = socioRepositorio.findByNumeroDocumentoAndEliminadoFalse(numeroDocumento)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un socio con ese número de documento"))
                    .getId();
        }
        if (socioId == null || socioId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar idSocio o numeroDocumento");
        }
        return service.crearCuota(socioId, mes, anio, idValorCuota);
    }

    @GetMapping("/{id}")
    public CuotaMensual obtener(@PathVariable String id) {
        return service.buscarCuotaMensual(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id,
                          @RequestParam String idSocio,
                          @RequestParam Mes mes,
                          @RequestParam Long anio,
                          @RequestParam String idValorCuota,
                          @RequestParam EstadoCuota estado) {
        service.modificarCuota(id, idSocio, mes, anio, idValorCuota, estado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarCuotaMensual(id);
    }

    @GetMapping
    public Collection<CuotaMensual> listar() {
        return service.listarCuotaMensual();
    }

    @GetMapping("/activos")
    public Collection<CuotaMensual> listarActivos() {
        return service.listarCuotaMensualActivo();
    }

    @GetMapping("/por-estado/{estado}")
    public Collection<CuotaMensual> listarPorEstado(@PathVariable EstadoCuota estado) {
        return service.listarCuotaMensualPorEstado(estado);
    }

    @GetMapping("/por-fecha")
    public Collection<CuotaMensual> listarPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return service.listarCuotaMensualPorFecha(fechaDesde, fechaHasta);
    }
}
