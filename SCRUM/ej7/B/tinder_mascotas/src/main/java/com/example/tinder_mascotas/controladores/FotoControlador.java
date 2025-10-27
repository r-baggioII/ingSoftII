package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.entidades.Foto;
import com.example.tinder_mascotas.repositorios.FotoRepositorio;
import com.example.tinder_mascotas.servicios.FotoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.constraints.NotBlank;



import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/fotos")
public class FotoControlador {

    @Autowired
    private FotoServicio fotoServicio;

    @Autowired
    private FotoRepositorio fotoRepositorio;

    /** Sube una foto nueva (multipart/form-data, campo "archivo") */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FotoMetaDTO> subir(@RequestPart("archivo") MultipartFile archivo) {
        Foto guardada = fotoServicio.guardar(archivo);
        if (guardada == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo guardar la foto (archivo vacío o error de lectura).");
        }
        URI location = URI.create("/api/fotos/" + guardada.getId());
        return ResponseEntity
                .created(location)
                .body(FotoMetaDTO.from(guardada));
    }

    /** Actualiza el contenido de una foto existente */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FotoMetaDTO> actualizar(@PathVariable @NotBlank String id,
                                                  @RequestPart("archivo") MultipartFile archivo) {
        Foto actualizada = fotoServicio.actualizar(id, archivo);
        if (actualizada == null) {
            // Puede ser porque el archivo viene vacío o porque no existe la foto
            Optional<Foto> exist = fotoRepositorio.findById(id);
            if (exist.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada: " + id);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo actualizar la foto (archivo vacío o error de lectura).");
        }
        return ResponseEntity.ok(FotoMetaDTO.from(actualizada));
    }

    /** Devuelve el binario con el Content-Type original, ideal para mostrar en <img> */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> ver(@PathVariable String id) {
        Foto foto = fotoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada: " + id));

        MediaType mediaType = parseMediaTypeOrDefault(foto.getMime());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache()) // ajustá cacheo a tu gusto
                .body(foto.getContenido());
    }

    /** Igual que ver(), pero con Content-Disposition: attachment para forzar descarga */
    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable String id) {
        Foto foto = fotoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada: " + id));

        MediaType mediaType = parseMediaTypeOrDefault(foto.getMime());
        String nombre = (foto.getNombre() != null && !foto.getNombre().isBlank()) ? foto.getNombre() : ("foto-" + id);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nombre).build().toString())
                .body(foto.getContenido());
    }

    /** Devuelve solo metadatos (id, nombre, mime), sin el binario */
    @GetMapping("/{id}/meta")
    public ResponseEntity<FotoMetaDTO> meta(@PathVariable String id) {
        Foto foto = fotoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada: " + id));
        return ResponseEntity.ok(FotoMetaDTO.from(foto));
    }

    // Utilidad: si el mime es nulo o inválido, devolver binario genérico
    private MediaType parseMediaTypeOrDefault(String mime) {
        try {
            if (mime == null || mime.isBlank()) {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
            return MediaType.parseMediaType(mime);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    /** DTO liviano para exponer metadatos de Foto */
    public record FotoMetaDTO(String id, String nombre, String mime) {
        public static FotoMetaDTO from(Foto f) {
            return new FotoMetaDTO(f.getId(), f.getNombre(), f.getMime());
        }
    }
}
