package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Departamento;
import com.example.greedy_gym.servicios.DepartamentoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departamentos")
@RequiredArgsConstructor
public class DepartamentoControladorV1 {

    private final DepartamentoServicio departamentoServicio;

    @PostMapping
    public ResponseEntity<Departamento> crear(@RequestBody DepartamentoRequest request) {
        Departamento creado = departamentoServicio.crearDepartamento(request.getNombre(), request.getIdProvincia());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<Departamento> listar(@RequestParam(required = false) String provinciaId) {
        if (provinciaId != null && !provinciaId.isEmpty()) {
            return departamentoServicio.listarDepartamentoActivo(provinciaId);
        }
        return departamentoServicio.listarTodosLosDepartamentos().stream()
                .filter(d -> !d.isEliminado())
                .toList();
    }

    @GetMapping("/activos")
    public List<Departamento> listarActivos(@RequestParam(required = false) String provinciaId) {
        if (provinciaId != null && !provinciaId.isEmpty()) {
            return departamentoServicio.listarDepartamentoActivo(provinciaId);
        }
        return departamentoServicio.listarTodosLosDepartamentos().stream()
                .filter(d -> !d.isEliminado())
                .toList();
    }

    @GetMapping("/{id}")
    public Departamento buscarPorId(@PathVariable String id) {
        return departamentoServicio.buscarDepartamento(id);
    }

    @GetMapping("/buscar")
    public Departamento buscarPorNombre(@RequestParam String nombre,
                                      @RequestParam(required = false) String provinciaId) {
        if (provinciaId != null && !provinciaId.isEmpty()) {
            return departamentoServicio.buscarPorNombreYProvincia(nombre, provinciaId);
        }
        return departamentoServicio.buscarDepartamentoPorNombre(nombre);
    }

    @PutMapping("/{id}")
    public Departamento actualizar(@PathVariable String id, @RequestBody DepartamentoRequest request) {
        departamentoServicio.modificarDepartamento(id, request.getNombre(), request.getIdProvincia());
        return departamentoServicio.buscarDepartamento(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        departamentoServicio.eliminarDepartamento(id);
        return ResponseEntity.noContent().build();
    }

    // Clase para el request
    public static class DepartamentoRequest {
        private String nombre;
        private String idProvincia;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getIdProvincia() {
            return idProvincia;
        }

        public void setIdProvincia(String idProvincia) {
            this.idProvincia = idProvincia;
        }
    }
}
