package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Provincia;
import com.example.greedy_gym.servicios.ProvinciaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/provincias")
@RequiredArgsConstructor
public class ProvinciaControladorV1 {

    private final ProvinciaServicio provinciaServicio;

    @PostMapping
    public ResponseEntity<Provincia> crear(@RequestBody ProvinciaRequest request) {
        Provincia creada = provinciaServicio.crearProvincia(request.getNombre(), request.getIdPais());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public List<Provincia> listar(@RequestParam(required = false) String paisId) {
        if (paisId != null && !paisId.isEmpty()) {
            return provinciaServicio.listarProvicniaActiva(paisId);
        }
        return provinciaServicio.listarTodasLasProvincias().stream()
                .filter(p -> !p.isEliminado())
                .toList();
    }

    @GetMapping("/activos")
    public List<Provincia> listarActivos(@RequestParam(required = false) String paisId) {
        if (paisId != null && !paisId.isEmpty()) {
            return provinciaServicio.listarProvicniaActiva(paisId);
        }
        return provinciaServicio.listarTodasLasProvincias().stream()
                .filter(p -> !p.isEliminado())
                .toList();
    }

    @GetMapping("/{id}")
    public Provincia buscarPorId(@PathVariable String id) {
        return provinciaServicio.buscarProvincia(id);
    }

    @GetMapping("/buscar")
    public Provincia buscarPorNombre(@RequestParam String nombre, 
                                   @RequestParam(required = false) String paisId) {
        if (paisId != null && !paisId.isEmpty()) {
            return provinciaServicio.buscarPorNombreYPais(nombre, paisId);
        }
        return provinciaServicio.buscarProvinciaPorNombre(nombre);
    }

    @PutMapping("/{id}")
    public Provincia actualizar(@PathVariable String id, @RequestBody ProvinciaRequest request) {
        provinciaServicio.modificarProvincia(id, request.getNombre(), request.getIdPais());
        return provinciaServicio.buscarProvincia(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        provinciaServicio.eliminarProvincia(id);
        return ResponseEntity.noContent().build();
    }

    // Clase para el request
    public static class ProvinciaRequest {
        private String nombre;
        private String idPais;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getIdPais() {
            return idPais;
        }

        public void setIdPais(String idPais) {
            this.idPais = idPais;
        }
    }
}
