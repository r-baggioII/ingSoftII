package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.entidades.Foto;
import com.example.tinder_mascotas.entidades.Mascota;
import com.example.tinder_mascotas.repositorios.MascotaRepositorio;
import com.example.tinder_mascotas.servicios.MascotaServicio;
import com.enumeraciones.Sexo;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class MascotaControlador {
    private static final Logger log = LoggerFactory.getLogger(MascotaControlador.class);

    @Autowired
    private MascotaServicio mascotaServicio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    /** Valores disponibles del enum Sexo para selects en vistas */
    @ModelAttribute("sexos")
    public Sexo[] sexos() {
        return Sexo.values();
    }

    /* ======================
       CREAR
       ====================== */
    @PostMapping(
            value = "/api/usuarios/{idUsuario}/mascotas",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MascotaDTO> crearMascota(
            @PathVariable String idUsuario,
            @RequestPart("nombre") String nombre,
            @RequestPart("sexo") Sexo sexo,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo
    ) {
        try {
            mascotaServicio.agregarMascota(archivo, idUsuario, nombre, sexo);

            // Recupero la última creada del usuario (simple) o podría devolver desde el servicio.
            // Para mantenerlo simple: busco todas y me quedo con la de alta más reciente.
            Mascota creada = mascotaRepositorio.buscarPorUsuario(idUsuario).stream()
                    .sorted((a, b) -> {
                        Date da = a.getAlta();
                        Date db = b.getAlta();
                        if (da == null && db == null) return 0;
                        if (da == null) return 1;
                        if (db == null) return -1;
                        return db.compareTo(da);
                    })
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo recuperar la mascota creada"));

            URI location = URI.create("/api/mascotas/" + creada.getId());
            return ResponseEntity.created(location).body(MascotaDTO.from(creada));

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear la mascota");
        }
    }

    /* ======================
       MODIFICAR
       ====================== */
    @PutMapping(
            value = "/api/usuarios/{idUsuario}/mascotas/{idMascota}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MascotaDTO> modificarMascota(
            @PathVariable String idUsuario,
            @PathVariable String idMascota,
            @RequestPart("nombre") String nombre,
            @RequestPart("sexo") Sexo sexo,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo
    ) {
        try {
            mascotaServicio.modificar(archivo, idUsuario, idMascota, nombre, sexo);
            Mascota m = mascotaRepositorio.findById(idMascota)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
            return ResponseEntity.ok(MascotaDTO.from(m));
        } catch (IllegalArgumentException e) {
            // incluye “No tienes permiso…” y “La mascota no existe”
            // Si querés distinguir 403/404, movemos esas validaciones al controlador
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al modificar la mascota");
        }
    }

    /* ======================
       BAJA LÓGICA
       ====================== */
    @DeleteMapping("/api/usuarios/{idUsuario}/mascotas/{idMascota}")
    public ResponseEntity<Void> eliminarMascota(
            @PathVariable String idUsuario,
            @PathVariable String idMascota
    ) {
        try {
            mascotaServicio.eliminar(idUsuario, idMascota);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la mascota");
        }
    }

    /* ======================
       LISTAR POR USUARIO
       ====================== */
    @GetMapping("/api/usuarios/{idUsuario}/mascotas")
    public ResponseEntity<List<MascotaDTO>> listarPorUsuario(
            @PathVariable String idUsuario,
            @RequestParam(name = "incluir_inactivas", defaultValue = "false") boolean incluirInactivas
    ) {
        List<MascotaDTO> dtos = mascotaRepositorio.buscarPorUsuario(idUsuario).stream()
                .filter(m -> incluirInactivas || m.getBaja() == null)
                .map(MascotaDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /* ======================
       DETALLE POR ID
       ====================== */
    @GetMapping("/api/mascotas/{idMascota}")
    public ResponseEntity<MascotaDTO> obtenerPorId(@PathVariable String idMascota) {
        Mascota m = mascotaRepositorio.findById(idMascota)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        return ResponseEntity.ok(MascotaDTO.from(m));
    }

    /* ======================
       VISTAS (MVC)
       ====================== */
    @GetMapping("/mascota/mis-mascotas")
    public String misMascotas(Model model, HttpSession session) {
        try {
            com.example.tinder_mascotas.entidades.Usuario u = (com.example.tinder_mascotas.entidades.Usuario) session.getAttribute("usuariosession");
            if (u == null) {
                return "redirect:/login";
            }
            List<Mascota> lista = mascotaRepositorio.buscarPorUsuario(u.getId()).stream()
                    .filter(m -> m.getBaja() == null)
                    .collect(Collectors.toList());
            model.addAttribute("mascotas", lista);
        } catch (Exception e) {
            log.error("[MVC] Error cargando mis mascotas", e);
            model.addAttribute("error", "No se pudieron cargar tus mascotas");
        }
        return "mascotas";
    }

    @GetMapping("/mascota/editar-perfil")
    public String editarMascota(@RequestParam(name = "id", required = false) String id,
                                @RequestParam(name = "accion", required = false) String accion,
                                Model model,
                                HttpSession session) {
        com.example.tinder_mascotas.entidades.Usuario u = (com.example.tinder_mascotas.entidades.Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }

        Mascota perfil;
        String resolvedAccion = (accion != null && !accion.isBlank()) ? accion : (id != null ? "Actualizar" : "Crear");
        if (id != null && !id.isBlank()) {
            perfil = mascotaRepositorio.findById(id).orElseGet(Mascota::new);
        } else {
            perfil = new Mascota();
        }
        model.addAttribute("perfil", perfil);
        model.addAttribute("accion", resolvedAccion);
        return "mascota";
    }

    @GetMapping("/mascota/debaja-mascotas")
    public String mascotasDeBaja(Model model, HttpSession session) {
        try {
            com.example.tinder_mascotas.entidades.Usuario u = (com.example.tinder_mascotas.entidades.Usuario) session.getAttribute("usuariosession");
            if (u == null) { return "redirect:/login"; }
            List<Mascota> lista = mascotaRepositorio.buscarPorUsuario(u.getId()).stream()
                    .filter(m -> m.getBaja() != null)
                    .collect(Collectors.toList());
            model.addAttribute("mascotas", lista);
        } catch (Exception e) {
            log.error("[MVC] Error cargando mascotas de baja", e);
            model.addAttribute("error", "No se pudieron cargar las mascotas dadas de baja");
        }
        return "mascotasdebaja";
    }

    /* ======================
       VISTAS (MVC) - Acciones
       ====================== */
    @PostMapping(value = "/mascota/actualizar-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String actualizarPerfil(
            @RequestParam(name = "id", required = false) String idMascota,
            @RequestParam("nombre") String nombre,
            @RequestParam("sexo") Sexo sexo,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            HttpSession session,
            Model model
    ) {
        com.example.tinder_mascotas.entidades.Usuario u = (com.example.tinder_mascotas.entidades.Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }
        try {
            if (idMascota == null || idMascota.isBlank()) {
                mascotaServicio.agregarMascota(archivo, u.getId(), nombre, sexo);
            } else {
                mascotaServicio.modificar(archivo, u.getId(), idMascota, nombre, sexo);
            }
            return "redirect:/mascota/mis-mascotas";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            // Repintar el formulario con lo ingresado
            Mascota perfil = new Mascota();
            perfil.setId(idMascota);
            perfil.setNombre(nombre);
            perfil.setSexo(sexo);
            model.addAttribute("perfil", perfil);
            model.addAttribute("accion", (idMascota == null || idMascota.isBlank()) ? "Crear" : "Actualizar");
            return "mascota";
        } catch (Exception e) {
            log.error("[MVC] Error actualizando/creando mascota", e);
            model.addAttribute("error", "Ocurrió un error al guardar la mascota");
            Mascota perfil = new Mascota();
            perfil.setId(idMascota);
            perfil.setNombre(nombre);
            perfil.setSexo(sexo);
            model.addAttribute("perfil", perfil);
            model.addAttribute("accion", (idMascota == null || idMascota.isBlank()) ? "Crear" : "Actualizar");
            return "mascota";
        }
    }

    @PostMapping("/mascota/eliminar-perfil")
    public String eliminarPerfil(@RequestParam("id") String idMascota, HttpSession session, Model model) {
        com.example.tinder_mascotas.entidades.Usuario u = (com.example.tinder_mascotas.entidades.Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }
        try {
            mascotaServicio.eliminar(u.getId(), idMascota);
            return "redirect:/mascota/mis-mascotas";
        } catch (Exception e) {
            log.error("[MVC] Error eliminando mascota {}", idMascota, e);
            model.addAttribute("error", "No se pudo eliminar la mascota");
            return "redirect:/mascota/mis-mascotas";
        }
    }

    @PostMapping("/mascota/alta-perfil")
    public String altaPerfil(@RequestParam("id") String idMascota, HttpSession session, Model model) {
        com.example.tinder_mascotas.entidades.Usuario u = (com.example.tinder_mascotas.entidades.Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }
        try {
            // restaurar la mascota (baja = null)
            Mascota m = mascotaRepositorio.findById(idMascota)
                    .orElseThrow(() -> new IllegalArgumentException("La mascota no existe"));
            if (!m.getUsuario().getId().equals(u.getId())) {
                throw new IllegalArgumentException("No tienes permiso para dar de alta esta mascota");
            }
            m.setBaja(null);
            mascotaRepositorio.save(m);
            return "redirect:/mascota/mis-mascotas";
        } catch (Exception e) {
            log.error("[MVC] Error dando de alta mascota {}", idMascota, e);
            model.addAttribute("error", "No se pudo dar de alta la mascota");
            return "redirect:/mascota/mis-mascotas";
        }
    }

    /** Foto de mascota por ID de mascota (para las vistas) */
    @GetMapping("/foto/mascota/{id}")
    public ResponseEntity<byte[]> fotoMascota(@PathVariable String id) {
        Mascota m = mascotaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        Foto f = m.getFoto();
        if (f == null || f.getContenido() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La mascota no tiene foto");
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
       DTO
       ====================== */
    public record MascotaDTO(
            String id,
            String nombre,
            String sexo,
            Long altaEpochMs,
            Long bajaEpochMs,
            String usuarioId,
            String fotoId
    ) {
        public static MascotaDTO from(Mascota m) {
            return new MascotaDTO(
                    m.getId(),
                    m.getNombre(),
                    m.getSexo() != null ? m.getSexo().name() : null,
                    m.getAlta() != null ? m.getAlta().getTime() : null,
                    m.getBaja() != null ? m.getBaja().getTime() : null,
                    (m.getUsuario() != null ? m.getUsuario().getId() : null),
                    (m.getFoto() != null ? m.getFoto().getId() : null)
            );
        }
    }
}
