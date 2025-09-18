package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.EstadoCuota;
import com.example.greedy_gym.servicios.CuotaMensualServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cuotas")
@RequiredArgsConstructor
public class CuotaMensualControlador {

    private final CuotaMensualServicio cuotaMensualServicio;

    @PostMapping
    public ResponseEntity<CuotaMensual> crear(@RequestBody CuotaMensual cuotaMensual) {
        CuotaMensual creada = cuotaMensualServicio.crear(cuotaMensual);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public Page<CuotaMensual> listar(Pageable pageable) {
        return cuotaMensualServicio.listar(pageable);
    }

    @GetMapping("/{id}")
    public CuotaMensual buscarPorId(@PathVariable String id) {
        return cuotaMensualServicio.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public CuotaMensual actualizar(@PathVariable String id, @RequestBody CuotaMensual cuotaMensual) {
        return cuotaMensualServicio.actualizar(id, cuotaMensual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        cuotaMensualServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activos")
    public Page<CuotaMensual> listarActivos(Pageable pageable) {
        return cuotaMensualServicio.listarActivos(pageable);
    }

    @GetMapping("/estado/{estado}")
    public Page<CuotaMensual> listarPorEstado(@PathVariable EstadoCuota estado, Pageable pageable) {
        return cuotaMensualServicio.listarPorEstado(estado, pageable);
    }
}

