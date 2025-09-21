package com.example.greedy_gym.controladores;

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
        return service.crearUsuario(body.getNombreUsuario(), body.getClave(), body.getCorreoElectronico());
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable String id) {
        return service.buscarUsuario(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestBody Usuario body) {
        service.modificarUsuario(id, body.getNombreUsuario(), body.getClave(), body.getCorreoElectronico());
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
}
