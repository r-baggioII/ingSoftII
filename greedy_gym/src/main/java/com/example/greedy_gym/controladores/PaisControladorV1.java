package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Pais;
import com.example.greedy_gym.servicios.PaisServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paises")
@RequiredArgsConstructor
public class PaisControladorV1 {

    private final PaisServicio paisServicio;

    @PostMapping
    public ResponseEntity<Pais> crear(@RequestBody PaisRequest request) {
        Pais creado = paisServicio.crearPais(request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<Pais> listar() {
        return paisServicio.listarPais();
    }

    @GetMapping("/activos")
    public List<Pais> listarActivos() {
        return paisServicio.listarPaisActivo();
    }

    @GetMapping("/{id}")
    public Pais buscarPorId(@PathVariable String id) {
        return paisServicio.buscarPais(id);
    }

    @GetMapping("/buscar")
    public Pais buscarPorNombre(@RequestParam String nombre) {
        return paisServicio.buscarPaisPorNombre(nombre);
    }

    @PutMapping("/{id}")
    public Pais actualizar(@PathVariable String id, @RequestBody PaisRequest request) {
        paisServicio.modificarPais(id, request.getNombre());
        return paisServicio.buscarPais(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        paisServicio.eliminarPais(id);
        return ResponseEntity.noContent().build();
    }

    // Clase para el request
    public static class PaisRequest {
        private String nombre;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}
