package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.entidades.Foto;
import com.example.tinder_mascotas.entidades.Usuario;
import com.example.tinder_mascotas.entidades.Zona;
import com.example.tinder_mascotas.repositorios.UsuarioRepositorio;
import com.example.tinder_mascotas.repositorios.ZonaRepositorio;
import com.example.tinder_mascotas.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UsuarioControlador {
    private static final Logger log = LoggerFactory.getLogger(UsuarioControlador.class);

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    /* ======================
       VISTAS (MVC)
       ====================== */
    @GetMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    // Handle legacy path used by some clients
    @GetMapping("/usuario/registrar")
    public String registroLegacyRedirect() {
        return "redirect:/registro";
    }

    /** Zonas disponibles para selects en las vistas */
    @ModelAttribute("zonas")
    public List<Zona> zonas() {
        try {
            List<Zona> all = zonaRepositorio.findAll();
            if (all.isEmpty()) {
                // Fallback: seed if empty at request time (in case initializer didn't run)
                log.warn("Zonas list empty; seeding default values on-demand");
                Zona capital = new Zona();
                capital.setId(java.util.UUID.randomUUID().toString());
                capital.setNombre("Capital");
                capital.setDescripcion("Capital");

                Zona godoyCruz = new Zona();
                godoyCruz.setId(java.util.UUID.randomUUID().toString());
                godoyCruz.setNombre("Godoy Cruz");
                godoyCruz.setDescripcion("Godoy Cruz");

                zonaRepositorio.save(capital);
                zonaRepositorio.save(godoyCruz);
                all = zonaRepositorio.findAll();
            }
            log.info("[MVC] zonas provided to model: {}", all.size());
            return all;
        } catch (Exception e) {
            log.error("Error loading zonas for model: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/usuario/editar-perfil")
    public String editarPerfil(@RequestParam(name = "id", required = false) String id, Model model, HttpSession session) {
        Usuario actual = (Usuario) session.getAttribute("usuariosession");
        if (actual == null) {
            return "redirect:/login";
        }
        Usuario perfil = actual;
        if (id != null && !id.isBlank()) {
            perfil = usuarioRepositorio.findById(id).orElse(actual);
        }
        model.addAttribute("perfil", perfil);
        return "perfil";
    }

    @PostMapping(value = "/usuario/actualizar-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String actualizarPerfil(
            @RequestParam("id") String id,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("mail") String email,
            @RequestParam(value = "clave1", required = false) String clave1,
            @RequestParam(value = "clave2", required = false) String clave2,
            @RequestParam(value = "idZona", required = false) String idZona,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            Model model,
            HttpSession session
    ) {
        Usuario actual = (Usuario) session.getAttribute("usuariosession");
        if (actual == null) { return "redirect:/login"; }

        if (clave1 != null && !clave1.isBlank()) {
            if (!clave1.equals(clave2)) {
                model.addAttribute("error", "Las contraseñas no coinciden");
                model.addAttribute("perfil", actual);
                return "perfil";
            }
        }

        try {
            // si el email cambia y ya existe, error
            Usuario existente = usuarioRepositorio.buscarPorCorreo(email);
            if (existente != null && !existente.getId().equals(id)) {
                model.addAttribute("error", "El email ya está registrado por otro usuario");
                // refrescar perfil visible
                Usuario p = usuarioRepositorio.findById(id).orElse(actual);
                model.addAttribute("perfil", p);
                return "perfil";
            }

            String contrasena = (clave1 != null && !clave1.isBlank()) ? clave1 : null;
            usuarioServicio.modificar(archivo, id, nombre, apellido, email, contrasena);

            // actualizar zona si corresponde
            if (idZona != null && !idZona.isBlank()) {
                zonaRepositorio.findById(idZona).ifPresent(z -> {
                    Usuario u = usuarioRepositorio.findById(id).orElse(null);
                    if (u != null) {
                        u.setZona(z);
                        usuarioRepositorio.save(u);
                    }
                });
            }

            // refrescar session
            Usuario actualizado = usuarioRepositorio.findById(id).orElse(actual);
            session.setAttribute("usuariosession", actualizado);
            return "redirect:/inicio";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Usuario p = usuarioRepositorio.findById(id).orElse(actual);
            model.addAttribute("perfil", p);
            return "perfil";
        } catch (Exception e) {
            log.error("[MVC] Error actualizando perfil de usuario {}", id, e);
            model.addAttribute("error", "Ocurrió un error al actualizar el perfil");
            Usuario p = usuarioRepositorio.findById(id).orElse(actual);
            model.addAttribute("perfil", p);
            return "perfil";
        }
    }

    @GetMapping("/exito")
    public String exito() {
        return "exito";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session != null) {
            try { session.invalidate(); } catch (Exception ignored) {}
        }
        redirectAttributes.addFlashAttribute("logout", "Sesión cerrada correctamente");
        return "redirect:/login";
    }

    /* ======================
       LOGIN (MVC)
       ====================== */
    @PostMapping("/usuario/loginUsuario")
    public String loginUsuario(@RequestParam("email") String email,
                               @RequestParam("clave") String clave,
                               Model model,
                               HttpSession session) {
        Usuario u = usuarioRepositorio.buscarPorCorreo(email);
        if (u == null || u.getContrasena() == null || !u.getContrasena().equals(clave)) {
            model.addAttribute("error", "Usuario o clave inválidos");
            return "login";
        }
        if (u.getBaja() != null) {
            model.addAttribute("error", "El usuario se encuentra dado de baja");
            return "login";
        }
        session.setAttribute("usuariosession", u);
        return "redirect:/inicio";
    }

    /* ======================
       REGISTRO (MVC)
       ====================== */
    @PostMapping(value = {"/registrar", "/usuario/registrar"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registrarFormulario(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("mail") String email,
            @RequestParam("clave1") String clave1,
            @RequestParam("clave2") String clave2,
            @RequestParam(value = "idZona", required = false) String idZona,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            Model model
    ) {
        log.info("[MVC] POST /registrar nombre='{}' apellido='{}' email='{}' idZona='{}'", nombre, apellido, email, idZona);
        // Validaciones básicas del formulario
        if (!clave1.equals(clave2)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("mail", email);
            return "registro";
        }

        if (usuarioRepositorio.buscarPorCorreo(email) != null) {
            model.addAttribute("error", "El email ya está registrado");
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("mail", email);
            return "registro";
        }

        try {
            usuarioServicio.registrar(archivo, nombre, apellido, email, clave1);

            // Asignar zona si fue seleccionada
            if (idZona != null && !idZona.isBlank()) {
                zonaRepositorio.findById(idZona).ifPresent(z -> {
                    Usuario recienCreado = usuarioRepositorio.buscarPorCorreo(email);
                    if (recienCreado != null) {
                        recienCreado.setZona(z);
                        usuarioRepositorio.save(recienCreado);
                    }
                });
            }

            return "redirect:/exito";

        } catch (IllegalArgumentException e) {
            log.warn("[MVC] Validación de registro fallida para email={}: {}", email, e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("mail", email);
            return "registro";
        } catch (Exception e) {
            log.error("[MVC] Error inesperado registrando usuario email={}", email, e);
            model.addAttribute("error", "Ocurrió un error al registrar el usuario");
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("mail", email);
            return "registro";
        }
    }

    /** Foto de usuario por ID de usuario (para las vistas) */
    @GetMapping("/foto/usuario/{id}")
    public ResponseEntity<byte[]> fotoUsuario(@PathVariable String id) {
        Usuario u = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        Foto f = u.getFoto();
        if (f == null || f.getContenido() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no tiene foto");
        }
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            if (f.getMime() != null && !f.getMime().isBlank()) {
                mediaType = MediaType.parseMediaType(f.getMime());
            }
        } catch (Exception ignored) { }
        return ResponseEntity.ok().contentType(mediaType).body(f.getContenido());
    }

    /* ======================
       REGISTRAR (CREATE)
       ====================== */
    @PostMapping(value = "/api/usuarios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @PutMapping(value = "/api/usuarios/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @GetMapping("/api/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable String id) {
        Usuario u = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(UsuarioDTO.from(u));
    }

    /* ======================
       BUSCAR POR EMAIL (READ)
       ====================== */
    @GetMapping("/api/usuarios/by-email")
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
    @GetMapping("/api/usuarios")
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
    @DeleteMapping("/api/usuarios/{id}")
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
