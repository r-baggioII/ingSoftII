package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Localidad;
import com.example.greedy_gym.servicios.LocalidadServicio;
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
@RequestMapping("/api/localidades")
public class LocalidadControlador {

    private final LocalidadServicio service;

    @Autowired
    public LocalidadControlador(LocalidadServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void crear(@RequestBody LocalidadRequest request) {
        service.crearLocalidad(
            request.getNombre(), 
            request.getCodigoPostal(), 
            request.getIdDepartamento()
        );
    }

    @GetMapping("/{id}")
    public Localidad obtener(@PathVariable String id) {
        return service.buscarLocalidad(id);
    }

    @GetMapping("/por-nombre/{nombre}")
    public Localidad obtenerPorNombre(@PathVariable String nombre) {
        return service.buscarLocalidadPorNombre(nombre);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, 
                         @RequestParam String nombre, 
                         @RequestParam String codigoPostal, 
                         @RequestParam String idDepartamento) {
        service.modificarLocalidad(id, nombre, codigoPostal, idDepartamento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarLocalidad(id);
    }

    @GetMapping("/por-departamento/{idDepartamento}")
    public List<Localidad> listarPorDepartamento(@PathVariable String idDepartamento) {
        return service.listarLocalidadActivo(idDepartamento);
    }

    @GetMapping
    public List<Localidad> listar(@RequestParam(required = false) String idDepartamento) {
        if (idDepartamento != null && !idDepartamento.trim().isEmpty()) {
            return service.listarLocalidad(idDepartamento);
        }
        // Return all localities for admin panel
        return service.listarTodasLasLocalidades();
    }

    // Clase interna para el request
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
