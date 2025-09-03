package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.entidades.Voto;
import com.example.tinder_mascotas.entidades.Mascota;
import com.example.tinder_mascotas.entidades.Usuario;
import com.example.tinder_mascotas.repositorios.MascotaRepositorio;
import com.example.tinder_mascotas.repositorios.VotoRepositorio;
import com.example.tinder_mascotas.servicios.VotoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpSession;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class VotoControlador {

    @Autowired
    private VotoServicio votoServicio;

    @Autowired
    private VotoRepositorio votoRepositorio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    /* ======================
       CREAR VOTO
       ====================== */
    @PostMapping("/api/usuarios/{idUsuario}/mascotas/{idMascota1}/votos")
    public ResponseEntity<VotoDTO> crearVoto(
            @PathVariable String idUsuario,
            @PathVariable String idMascota1,
            @RequestParam("idMascota2") String idMascota2
    ) {
        try {
            votoServicio.agregarVoto(idUsuario, idMascota1, idMascota2);

            // Recupero el último voto creado para esa mascota1 (ordenado DESC por fecha)
            Voto creado = votoRepositorio.buscarVotosPropios(idMascota1).stream()
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo recuperar el voto creado"));

            URI location = URI.create("/api/votos/" + creado.getId());
            return ResponseEntity.created(location).body(VotoDTO.from(creado));

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el voto");
        }
    }

    /* ======================
       RESPONDER VOTO (MATCH)
       ====================== */
    @PostMapping("/api/usuarios/{idUsuario}/votos/{idVoto}/responder")
    public ResponseEntity<VotoDTO> responder(
            @PathVariable String idUsuario,
            @PathVariable String idVoto
    ) {
        try {
            votoServicio.responder(idUsuario, idVoto);
            Voto v = votoRepositorio.findById(idVoto)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voto no encontrado"));
            return ResponseEntity.ok(VotoDTO.from(v));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al responder el voto");
        }
    }

    /* ======================
       LISTAR VOTOS PROPIOS (DE UNA MASCOTA)
       ====================== */
    @GetMapping("/api/mascotas/{idMascota}/votos/propios")
    public ResponseEntity<List<VotoDTO>> listarPropios(@PathVariable String idMascota) {
        List<VotoDTO> dtos = votoRepositorio.buscarVotosPropios(idMascota)
                .stream().map(VotoDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /* ======================
       LISTAR VOTOS RECIBIDOS (DE UNA MASCOTA)
       ====================== */
    @GetMapping("/api/mascotas/{idMascota}/votos/recibidos")
    public ResponseEntity<List<VotoDTO>> listarRecibidos(@PathVariable String idMascota) {
        List<VotoDTO> dtos = votoRepositorio.buscarVotosRecibidos(idMascota)
                .stream().map(VotoDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /* ======================
       DETALLE
       ====================== */
    @GetMapping("/api/votos/{idVoto}")
    public ResponseEntity<VotoDTO> detalle(@PathVariable String idVoto) {
        Voto v = votoRepositorio.findById(idVoto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voto no encontrado"));
        return ResponseEntity.ok(VotoDTO.from(v));
    }

    /* ======================
       VISTAS (MVC)
       ====================== */
    @GetMapping("/votos/explorar")
    public String explorar(
            @RequestParam(name = "idMascota1", required = false) String idMascota1,
            Model model,
            HttpSession session
    ) {
        Usuario u = (Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }

        // Mascotas del usuario (activas)
        List<Mascota> mias = mascotaRepositorio.buscarPorUsuario(u.getId()).stream()
                .filter(m -> m.getBaja() == null)
                .collect(Collectors.toList());
        model.addAttribute("misMascotas", mias);

        // Elegir una por defecto si no viene
        String seleccionada = idMascota1;
        if ((seleccionada == null || seleccionada.isBlank()) && !mias.isEmpty()) {
            seleccionada = mias.get(0).getId();
        }
        model.addAttribute("seleccionada", seleccionada);

        // Sugerencias: otras mascotas activas que no sean del usuario
        List<Mascota> sugeridas = mascotaRepositorio.findAll().stream()
                .filter(m -> m.getBaja() == null)
                .filter(m -> m.getUsuario() != null && !m.getUsuario().getId().equals(u.getId()))
                .collect(Collectors.toList());
        model.addAttribute("sugeridas", sugeridas);

        return "votos_explorar";
    }

    @PostMapping("/votos/votar")
    public String votar(
            @RequestParam("idMascota1") String idMascota1,
            @RequestParam("idMascota2") String idMascota2,
            HttpSession session,
            Model model
    ) {
        Usuario u = (Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }
        try {
            votoServicio.agregarVoto(u.getId(), idMascota1, idMascota2);
            return "redirect:/votos/propios?idMascota=" + idMascota1;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/votos/explorar?idMascota1=" + idMascota1;
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al votar");
            return "redirect:/votos/explorar?idMascota1=" + idMascota1;
        }
    }

    @GetMapping("/votos/propios")
    public String listarVotosPropios(
            @RequestParam("idMascota") String idMascota,
            Model model,
            HttpSession session
    ) {
        Usuario u = (Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }

        Mascota m = mascotaRepositorio.findById(idMascota)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        if (m.getUsuario() == null || !m.getUsuario().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver estos votos");
        }

        model.addAttribute("mascota", m);
        model.addAttribute("votos", votoRepositorio.buscarVotosPropios(idMascota));
        // útil para cambiar de mascota desde la vista
        List<Mascota> mias = mascotaRepositorio.buscarPorUsuario(u.getId()).stream()
                .filter(mx -> mx.getBaja() == null)
                .collect(Collectors.toList());
        model.addAttribute("misMascotas", mias);

        return "votos_propios";
    }

    @GetMapping("/votos/recibidos")
    public String listarVotosRecibidos(
            @RequestParam("idMascota") String idMascota,
            Model model,
            HttpSession session
    ) {
        Usuario u = (Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }

        Mascota m = mascotaRepositorio.findById(idMascota)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        if (m.getUsuario() == null || !m.getUsuario().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver estos votos");
        }

        model.addAttribute("mascota", m);
        model.addAttribute("votos", votoRepositorio.buscarVotosRecibidos(idMascota));
        List<Mascota> mias = mascotaRepositorio.buscarPorUsuario(u.getId()).stream()
                .filter(mx -> mx.getBaja() == null)
                .collect(Collectors.toList());
        model.addAttribute("misMascotas", mias);

        return "votos_recibidos";
    }

    @PostMapping("/votos/responder")
    public String responderVoto(
            @RequestParam("idVoto") String idVoto,
            HttpSession session,
            Model model
    ) {
        Usuario u = (Usuario) session.getAttribute("usuariosession");
        if (u == null) { return "redirect:/login"; }
        String mascota2Id = null;
        try {
            Voto v = votoRepositorio.findById(idVoto)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voto no encontrado"));
            if (v.getMascota2() != null) {
                mascota2Id = v.getMascota2().getId();
            }
            votoServicio.responder(u.getId(), idVoto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al responder el voto");
        }
        if (mascota2Id == null || mascota2Id.isBlank()) {
            return "redirect:/votos/explorar";
        }
        return "redirect:/votos/recibidos?idMascota=" + mascota2Id;
    }

    /* ======================
       DTO
       ====================== */
    public record VotoDTO(
            String id,
            Long fechaEpochMs,
            Long voto1EpochMs,
            Long voto2EpochMs,
            String mascota1Id,
            String mascota2Id,
            String usuarioMascota1Id,
            String usuarioMascota2Id
    ) {
        public static VotoDTO from(Voto v) {
            return new VotoDTO(
                    v.getId(),
                    v.getFecha() != null ? v.getFecha().getTime() : null,
                    v.getVoto1() != null ? v.getVoto1().getTime() : null,
                    v.getVoto2() != null ? v.getVoto2().getTime() : null,
                    v.getMascota1() != null ? v.getMascota1().getId() : null,
                    v.getMascota2() != null ? v.getMascota2().getId() : null,
                    (v.getMascota1() != null && v.getMascota1().getUsuario() != null) ? v.getMascota1().getUsuario().getId() : null,
                    (v.getMascota2() != null && v.getMascota2().getUsuario() != null) ? v.getMascota2().getUsuario().getId() : null
            );
        }
    }
}
