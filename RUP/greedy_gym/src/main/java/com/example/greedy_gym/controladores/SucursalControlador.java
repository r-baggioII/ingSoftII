package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Direccion;
import com.example.greedy_gym.entidades.Sucursal;
import com.example.greedy_gym.servicios.SucursalServicio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
public class SucursalControlador {

    private final SucursalServicio sucursalServicio;

    @PostMapping
    public ResponseEntity<Sucursal> crear(@RequestBody SucursalRequest request) {
        Sucursal creada = sucursalServicio.crearSucursal(request.getNombre(), request.getIdEmpresa(),
                request.getDireccion());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public List<Sucursal> listar() {
        return sucursalServicio.listarSucursal();
    }

    @GetMapping("/activos")
    public List<Sucursal> listarActivos() {
        return sucursalServicio.listarSucursalActiva();
    }

    @GetMapping("/{id}")
    public Sucursal buscarPorId(@PathVariable String id) {
        return sucursalServicio.buscarSucursal(id);
    }

    @GetMapping("/buscar")
    public Sucursal buscarPorNombre(@RequestParam String nombre,
            @RequestParam(required = false) String empresaId) {
        if (empresaId == null || empresaId.isBlank()) {
            return sucursalServicio.buscarSucursalPorNombre(nombre);
        }
        return sucursalServicio.buscarSucursalPorNombre(nombre, empresaId);
    }

    @PutMapping("/{id}")
    public Sucursal actualizar(@PathVariable String id, @RequestBody SucursalRequest request) {
        return sucursalServicio.modificarSucursal(id, request.getNombre(), request.getIdEmpresa(), request.getDireccion());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        sucursalServicio.eliminarSucursal(id);
        return ResponseEntity.noContent().build();
    }

    public static class SucursalRequest {

        private String nombre;
        private String idEmpresa;
        private Direccion direccion;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getIdEmpresa() {
            return idEmpresa;
        }

        public void setIdEmpresa(String idEmpresa) {
            this.idEmpresa = idEmpresa;
        }

        public Direccion getDireccion() {
            return direccion;
        }

        public void setDireccion(Direccion direccion) {
            this.direccion = direccion;
        }
    }
}
