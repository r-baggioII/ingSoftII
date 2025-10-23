package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Departamento;
import com.example.greedy_gym.servicios.DepartamentoServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoControlador {

    private final DepartamentoServicio service;

    @Autowired
    public DepartamentoControlador(DepartamentoServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void crear(@RequestBody DepartamentoRequest request) {
        service.crearDepartamento(request.getNombre(), request.getIdProvincia());
    }

    @GetMapping("/{id}")
    public Departamento obtener(@PathVariable String id) {
        return service.buscarDepartamento(id);
    }

    @GetMapping("/por-nombre/{nombre}")
    public Departamento obtenerPorNombre(@PathVariable String nombre) {
        return service.buscarDepartamentoPorNombre(nombre);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestParam String nombre, @RequestParam String idProvincia) {
        service.modificarDepartamento(id, nombre, idProvincia);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarDepartamento(id);
    }

    @GetMapping("/por-provincia/{idProvincia}")
    public List<Departamento> listarPorProvincia(@PathVariable String idProvincia) {
        return service.listarDepartamentoActivo(idProvincia);
    }

    @GetMapping
    public List<Departamento> listar(@RequestParam(required = false) String idProvincia) {
        if (idProvincia != null && !idProvincia.trim().isEmpty()) {
            return service.listarDepartamento(idProvincia);
        }
        // Return all departments for admin panel
        return service.listarTodosLosDepartamentos();
    }

    // Clase interna para el request
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
