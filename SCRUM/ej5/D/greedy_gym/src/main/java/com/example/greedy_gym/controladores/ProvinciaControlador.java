package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Provincia;
import com.example.greedy_gym.servicios.ProvinciaServicio;
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
@RequestMapping("/api/provincias")
public class ProvinciaControlador {

    private final ProvinciaServicio service;

    @Autowired
    public ProvinciaControlador(ProvinciaServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void crear(@RequestBody ProvinciaRequest request) {
        service.crearProvincia(request.getNombre(), request.getIdPais());
    }

    @GetMapping("/{id}")
    public Provincia obtener(@PathVariable String id) {
        return service.buscarProvincia(id);
    }

    @GetMapping("/por-nombre/{nombre}")
    public Provincia obtenerPorNombre(@PathVariable String nombre) {
        return service.buscarProvinciaPorNombre(nombre);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestParam String nombre, @RequestParam String idPais) {
        service.modificarProvincia(id, nombre, idPais);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarProvincia(id);
    }

    @GetMapping("/por-pais/{idPais}")
    public List<Provincia> listarPorPais(@PathVariable String idPais) {
        return service.listarProvicniaActiva(idPais);
    }

    @GetMapping
    public List<Provincia> listar(@RequestParam(required = false) String idPais) {
        if (idPais != null && !idPais.trim().isEmpty()) {
            return service.listarProvicnia(idPais);
        }
        // Return all provinces for admin panel
        return service.listarTodasLasProvincias();
    }

    // Clase interna para el request
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
