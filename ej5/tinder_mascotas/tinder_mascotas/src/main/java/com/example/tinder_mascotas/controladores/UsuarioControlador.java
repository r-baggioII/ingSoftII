package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.entidades.Usuario;
import com.example.tinder_mascotas.repositorios.UsuarioRepositorio;
import com.example.tinder_mascotas.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    /* ======================
       REGISTRAR (CREATE)
       ====================== */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioDTO> registrar(
            @RequestPart("nombre") String nombre,
            @RequestPart("apellido") String apellido,
            @RequestPart("email") String email,
            @RequestPart("contrasena") String contrasena,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo
    ) {
        try {
            usuarioServicio.registrar(archivo, nombre, apellido, email, contrasena);

            // Recupero el recién creado (por email es determinístico en este dominio)
            Usuario creado = usuarioRepositorio.buscarPorCorreo(email);
            if (creado == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo recuperar el usuario creado");
            }

            URI location = URI.create("/api/usuarios/" + creado.getId());
            return ResponseEntity.created(location).body(UsuarioDTO.from(creado));

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar el usuario");
        }
    }

    /* ======================
       MODIFICAR (UPDATE)
       ====================== */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioDTO> modificar(
            @PathVariable String id,
            @RequestPart(value = "nombre", required = false) String nombre,
            @RequestPart(value = "apellido", required = false) String apellido,
            @RequestPart(value = "email", required = false) String email,
            @RequestPart(value = "contrasena", required = false) String contrasena,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo
    ) {
        try {
            // El servicio acepta nulls para campos no modificados
            usuarioServicio.modificar(archivo, id, nombre, apellido, email, contrasena);

            Usuario u = usuarioRepositorio.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            return ResponseEntity.ok(UsuarioDTO.from(u));

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al modificar el usuario");
        }
    }

    /* ======================
       DETALLE POR ID (READ)
       ====================== */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable String id) {
        Usuario u = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(UsuarioDTO.from(u));
    }

    /* ======================
       BUSCAR POR EMAIL (READ)
       ====================== */
    @GetMapping("/by-email")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestParam("email") String email) {
        Usuario u = usuarioRepositorio.buscarPorCorreo(email);
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado para el email: " + email);
        }
        return ResponseEntity.ok(UsuarioDTO.from(u));
    }

    /* ======================
       LISTAR (READ)
       ====================== */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar(
            @RequestParam(name = "incluir_inactivos", defaultValue = "false") boolean incluirInactivos
    ) {
        List<UsuarioDTO> dtos = usuarioRepositorio.findAll().stream()
                .filter(u -> incluirInactivos || u.getBaja() == null)
                .map(UsuarioDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /* ======================
       BAJA LÓGICA (DELETE)
       ====================== */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> baja(@PathVariable String id) {
        Usuario u = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (u.getBaja() == null) {
            u.setBaja(new Date());
            usuarioRepositorio.save(u);
        }
        return ResponseEntity.noContent().build();
    }

    /* ======================
       DTO
       ====================== */
    public record UsuarioDTO(
            String id,
            String nombre,
            String apellido,
            String email,
            Long altaEpochMs,
            Long bajaEpochMs,
            String zonaId,
            String fotoId
    ) {
        public static UsuarioDTO from(Usuario u) {
            return new UsuarioDTO(
                    u.getId(),
                    u.getNombre(),
                    u.getApellido(),
                    u.getEmail(),
                    u.getAlta() != null ? u.getAlta().getTime() : null,
                    u.getBaja() != null ? u.getBaja().getTime() : null,
                    (u.getZona() != null ? u.getZona().getId() : null),
                    (u.getFoto() != null ? u.getFoto().getId() : null)
            );
        }
    }
}
