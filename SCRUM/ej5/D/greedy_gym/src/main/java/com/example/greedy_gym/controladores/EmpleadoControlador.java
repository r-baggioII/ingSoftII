package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Empleado;
import com.example.greedy_gym.servicios.EmpleadoServicio;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
public class EmpleadoControlador {

    private final EmpleadoServicio empleadoServicio;

    @PostMapping
    public ResponseEntity<Empleado> crear(@RequestBody Empleado empleado) {
        Empleado creado = empleadoServicio.crearEmpleado(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<Empleado> listar() {
        return empleadoServicio.listarEmpleado();
    }

    @GetMapping("/activos")
    public List<Empleado> listarActivos() {
        return empleadoServicio.listarEmpleadoActivo();
    }

    @GetMapping("/{id}")
    public Empleado buscarPorId(@PathVariable String id) {
        return empleadoServicio.buscarPersona(id);
    }

    @PutMapping("/{id}")
    public Empleado actualizar(@PathVariable String id, @RequestBody Empleado empleado) {
        return empleadoServicio.modificarEmpleado(id, empleado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        empleadoServicio.eliminarPersona(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/usuario")
    public Empleado asociarUsuario(@PathVariable String id, @RequestBody AsociarUsuarioRequest request) {
        String usuarioId = request != null ? request.getUsuarioId() : null;
        return empleadoServicio.asociarEmpleadoUsuario(id, usuarioId);
    }

    public static class AsociarUsuarioRequest {

        private String usuarioId;

        public String getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(String usuarioId) {
            this.usuarioId = usuarioId;
        }
    }
}
