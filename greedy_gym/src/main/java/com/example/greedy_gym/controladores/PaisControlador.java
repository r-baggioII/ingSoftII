package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Pais;
import com.example.greedy_gym.servicios.PaisServicio;
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
@RequestMapping("/api/paises")
public class PaisControlador {

    private final PaisServicio service;

    @Autowired
    public PaisControlador(PaisServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void crear(@RequestBody Pais body) {
        service.crearPais(body.getNombre());
    }

    @GetMapping("/{id}")
    public Pais obtener(@PathVariable String id) {
        return service.buscarPais(id);
    }

    @GetMapping("/por-nombre/{nombre}")
    public Pais obtenerPorNombre(@PathVariable String nombre) {
        return service.buscarPaisPorNombre(nombre);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestParam String nombre) {
        service.modificarPais(id, nombre);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarPais(id);
    }

    @GetMapping
    public List<Pais> listar(@RequestParam(required = false) String filtro) {
        // Si no se especifica filtro o se solicitan solo activos, devolver activos
        if (filtro == null || "activos".equals(filtro)) {
            return service.listarPaisActivo();
        }
        // Para cualquier otro filtro (incluyendo "todos"), devolver todos
        return service.listarPais();
    }
}
