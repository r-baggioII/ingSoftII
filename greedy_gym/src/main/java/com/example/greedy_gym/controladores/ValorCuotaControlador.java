package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.ValorCuota;
import com.example.greedy_gym.servicios.ValorCuotaServicio;
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
@RequestMapping("/api/v1/valor-cuotas")
@RequiredArgsConstructor
public class ValorCuotaControlador {

    private final ValorCuotaServicio valorCuotaServicio;

    @PostMapping
    public ResponseEntity<ValorCuota> crear(@RequestBody ValorCuota valorCuota) {
        ValorCuota creado = valorCuotaServicio.crear(valorCuota);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public Page<ValorCuota> listar(Pageable pageable) {
        return valorCuotaServicio.listar(pageable);
    }

    @GetMapping("/{id}")
    public ValorCuota buscarPorId(@PathVariable String id) {
        return valorCuotaServicio.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ValorCuota actualizar(@PathVariable String id, @RequestBody ValorCuota valorCuota) {
        return valorCuotaServicio.actualizar(id, valorCuota);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        valorCuotaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vigente")
    public ValorCuota vigente() {
        return valorCuotaServicio.buscarVigente();
    }
}

