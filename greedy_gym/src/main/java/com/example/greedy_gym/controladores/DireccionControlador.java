package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Direccion;
import com.example.greedy_gym.servicios.DireccionServicio;
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
@RequestMapping("/api/direcciones")
public class DireccionControlador {

    private final DireccionServicio service;

    @Autowired
    public DireccionControlador(DireccionServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Direccion crear(@RequestBody DireccionRequest request) {
        return service.crearDireccion(
            request.getCalle(),
            request.getNumeracion(),
            request.getBarrio(),
            request.getManzanaPiso(),
            request.getCasaDepartamento(),
            request.getReferencia(),
            request.getIdLocalidad()
        );
    }

    @GetMapping("/{id}")
    public Direccion obtener(@PathVariable String id) {
        return service.buscarDireccion(id);
    }

    @GetMapping("/buscar")
    public Direccion buscarPorCalleNumeracion(
            @RequestParam String calle, 
            @RequestParam String numeracion) {
        return service.buscarDireccionPorCalleNumeracion(calle, numeracion);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestBody DireccionRequest request) {
        service.modificarDireccion(
            id,
            request.getCalle(),
            request.getNumeracion(),
            request.getBarrio(),
            request.getManzanaPiso(),
            request.getCasaDepartamento(),
            request.getReferencia(),
            request.getIdLocalidad()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarDireccion(id);
    }

    // Clase interna para el request
    public static class DireccionRequest {
        private String calle;
        private String numeracion;
        private String barrio;
        private String manzanaPiso;
        private String casaDepartamento;
        private String referencia;
        private String idLocalidad;

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getNumeracion() {
            return numeracion;
        }

        public void setNumeracion(String numeracion) {
            this.numeracion = numeracion;
        }

        public String getBarrio() {
            return barrio;
        }

        public void setBarrio(String barrio) {
            this.barrio = barrio;
        }

        public String getManzanaPiso() {
            return manzanaPiso;
        }

        public void setManzanaPiso(String manzanaPiso) {
            this.manzanaPiso = manzanaPiso;
        }

        public String getCasaDepartamento() {
            return casaDepartamento;
        }

        public void setCasaDepartamento(String casaDepartamento) {
            this.casaDepartamento = casaDepartamento;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }

        public String getIdLocalidad() {
            return idLocalidad;
        }

        public void setIdLocalidad(String idLocalidad) {
            this.idLocalidad = idLocalidad;
        }
    }
}
