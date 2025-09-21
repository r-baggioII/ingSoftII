package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Localidad;
import com.example.greedy_gym.servicios.LocalidadServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/localidades")
@RequiredArgsConstructor
public class LocalidadControladorV1 {

    private final LocalidadServicio localidadServicio;

    @PostMapping
    public ResponseEntity<Localidad> crear(@RequestBody LocalidadRequest request) {
        Localidad creada = localidadServicio.crearLocalidad(request.getNombre(), 
                                                          request.getCodigoPostal(), 
                                                          request.getIdDepartamento());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public List<Localidad> listar(@RequestParam(required = false) String departamentoId) {
        if (departamentoId != null && !departamentoId.isEmpty()) {
            return localidadServicio.listarLocalidadActivo(departamentoId);
        }
        return localidadServicio.listarTodasLasLocalidades().stream()
                .filter(l -> !l.isEliminado())
                .toList();
    }

    @GetMapping("/activos")
    public List<Localidad> listarActivos(@RequestParam(required = false) String departamentoId) {
        if (departamentoId != null && !departamentoId.isEmpty()) {
            return localidadServicio.listarLocalidadActivo(departamentoId);
        }
        return localidadServicio.listarTodasLasLocalidades().stream()
                .filter(l -> !l.isEliminado())
                .toList();
    }

    @GetMapping("/{id}")
    public Localidad buscarPorId(@PathVariable String id) {
        return localidadServicio.buscarLocalidad(id);
    }

    @GetMapping("/buscar")
    public Localidad buscarPorNombre(@RequestParam String nombre,
                                   @RequestParam(required = false) String departamentoId) {
        if (departamentoId != null && !departamentoId.isEmpty()) {
            return localidadServicio.buscarPorNombreYDepartamento(nombre, departamentoId);
        }
        return localidadServicio.buscarLocalidadPorNombre(nombre);
    }

    @GetMapping("/buscar-por-codigo")
    public Localidad buscarPorCodigoPostal(@RequestParam String codigoPostal) {
        return localidadServicio.buscarLocalidadPorCodigoPostal(codigoPostal);
    }

    @PutMapping("/{id}")
    public Localidad actualizar(@PathVariable String id, @RequestBody LocalidadRequest request) {
        localidadServicio.modificarLocalidad(id, request.getNombre(), 
                                           request.getCodigoPostal(), 
                                           request.getIdDepartamento());
        return localidadServicio.buscarLocalidad(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        localidadServicio.eliminarLocalidad(id);
        return ResponseEntity.noContent().build();
    }

    // Clase para el request
    public static class LocalidadRequest {
        private String nombre;
        private String codigoPostal;
        private String idDepartamento;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCodigoPostal() {
            return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }

        public String getIdDepartamento() {
            return idDepartamento;
        }

        public void setIdDepartamento(String idDepartamento) {
            this.idDepartamento = idDepartamento;
        }
    }
}
