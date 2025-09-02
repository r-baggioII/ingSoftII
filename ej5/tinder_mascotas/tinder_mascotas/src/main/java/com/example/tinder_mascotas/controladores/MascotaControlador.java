package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.entidades.Mascota;
import com.example.tinder_mascotas.repositorios.MascotaRepositorio;
import com.example.tinder_mascotas.servicios.MascotaServicio;
import com.enumeraciones.Sexo;
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
@RequestMapping("/api")
public class MascotaControlador {

    @Autowired
    private MascotaServicio mascotaServicio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    /* ======================
       CREAR
       ====================== */
    @PostMapping(
            value = "/usuarios/{idUsuario}/mascotas",
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
            value = "/usuarios/{idUsuario}/mascotas/{idMascota}",
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
    @DeleteMapping("/usuarios/{idUsuario}/mascotas/{idMascota}")
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
    @GetMapping("/usuarios/{idUsuario}/mascotas")
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
    @GetMapping("/mascotas/{idMascota}")
    public ResponseEntity<MascotaDTO> obtenerPorId(@PathVariable String idMascota) {
        Mascota m = mascotaRepositorio.findById(idMascota)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        return ResponseEntity.ok(MascotaDTO.from(m));
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
