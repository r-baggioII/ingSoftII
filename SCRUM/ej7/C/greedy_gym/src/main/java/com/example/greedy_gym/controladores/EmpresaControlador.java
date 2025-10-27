package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Empresa;
import com.example.greedy_gym.servicios.EmpresaServicio;
import java.util.List;
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
@RequestMapping("/api/empresas")
public class EmpresaControlador {

    private final EmpresaServicio service;

    public EmpresaControlador(EmpresaServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empresa crear(@RequestBody Empresa body) {
        return service.crearEmpresa(body.getNombre(), body.getTelefono(), body.getCorreoElectronico());
    }

    @GetMapping("/{id}")
    public Empresa obtener(@PathVariable String id) {
        return service.buscarEmpresa(id);
    }

    @GetMapping("/por-nombre/{nombre}")
    public Empresa obtenerPorNombre(@PathVariable String nombre) {
        return service.buscarEmpresaPorNombre(nombre);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestParam String nombre) {
        service.modificarEmpresa(id, nombre);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarEmpresa(id);
    }

    @GetMapping
    public List<Empresa> listar() {
        return service.listarEmpresa();
    }

    @GetMapping("/activas")
    public List<Empresa> listarActivas() {
        return service.listarEmpresaActiva();
    }
}
