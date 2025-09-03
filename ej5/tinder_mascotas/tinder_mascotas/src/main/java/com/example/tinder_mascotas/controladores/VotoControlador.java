package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.entidades.Voto;
import com.example.tinder_mascotas.repositorios.VotoRepositorio;
import com.example.tinder_mascotas.servicios.VotoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class VotoControlador {

    @Autowired
    private VotoServicio votoServicio;

    @Autowired
    private VotoRepositorio votoRepositorio;

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
