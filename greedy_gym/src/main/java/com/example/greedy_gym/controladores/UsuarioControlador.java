package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.servicios.UsuarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio service;

    public UsuarioControlador(UsuarioServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crear(@RequestBody Usuario body) {
        return service.crearUsuario(body.getNombreUsuario(), body.getClave(), body.getRol());
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable String id) {
        return service.buscarUsuario(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestBody Usuario body) {
        service.modificarUsuario(id, body.getNombreUsuario(), body.getClave(), body.getRol());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarUsuario(id);
    }

    @GetMapping
    public List<Usuario> listar() {
        return service.listarUsuarios();
    }

    @GetMapping("/activos")
    public List<Usuario> listarActivos() {
        return service.listarUsuariosActivos();
    }

    @GetMapping("/buscar/{nombre}")
    public Usuario buscarPorNombre(@PathVariable String nombre) {
        return service.buscarUsuarioPorNombre(nombre);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest request) {
        return service.login(request.nombreUsuario, request.clave);
    }

    @PutMapping("/{id}/clave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificarClave(@PathVariable String id, @RequestBody ModificarClaveRequest request) {
        service.modificarClave(id, request.claveActual, request.nuevaClave, request.confirmarClave);
    }

    public static class LoginRequest {
        public String nombreUsuario;
        public String clave;
    }

    public static class ModificarClaveRequest {
        public String claveActual;
        public String nuevaClave;
        public String confirmarClave;
    }
}
