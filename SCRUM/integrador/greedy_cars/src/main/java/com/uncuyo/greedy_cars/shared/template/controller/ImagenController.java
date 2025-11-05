package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.ImagenDTO;
import com.uncuyo.greedy_cars.shared.template.enums.TipoImagen;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.ImagenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    @Autowired
    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<ImagenDTO> listaImagenesDTO = imagenService.listarActivosDTO();
            return ResponseEntity.ok(listaImagenesDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            ImagenDTO imagenDTO = imagenService.obtenerDTO(id)
                .orElseThrow(() -> new ErrorServiceException("Imagen no encontrada con ID: " + id));
            return ResponseEntity.ok(imagenDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para obtener solo el contenido binario de la imagen (para visualización directa).
     * 
     * @param id el ID de la imagen
     * @return ResponseEntity con el contenido de la imagen y headers apropiados
     */
    @GetMapping("/{id}/contenido")
    public ResponseEntity<?> obtenerContenido(@PathVariable String id) {
        try {
            ImagenDTO imagenDTO = imagenService.obtenerDTO(id)
                .orElseThrow(() -> new ErrorServiceException("Imagen no encontrada con ID: " + id));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(imagenDTO.getMime()));
            headers.setContentLength(imagenDTO.getContenido().length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imagenDTO.getNombre() + "\"");
            
            return new ResponseEntity<>(imagenDTO.getContenido(), headers, HttpStatus.OK);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para buscar imágenes por tipo.
     * 
     * @param tipo el tipo de imagen (PERSONA o VEHICULO)
     * @return ResponseEntity con la lista de imágenes del tipo especificado
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> listarPorTipo(@PathVariable String tipo) {
        try {
            TipoImagen tipoImagen = TipoImagen.valueOf(tipo.toUpperCase());
            List<ImagenDTO> listaImagenesDTO = imagenService.listarPorTipo(tipoImagen);
            return ResponseEntity.ok(listaImagenesDTO);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse("Tipo de imagen inválido: " + tipo, HttpStatus.BAD_REQUEST);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para buscar imágenes por nombre.
     * 
     * @param nombre el texto a buscar en el nombre
     * @return ResponseEntity con la lista de imágenes que coincidan
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String nombre) {
        try {
            List<ImagenDTO> listaImagenesDTO = imagenService.buscarPorNombre(nombre);
            return ResponseEntity.ok(listaImagenesDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ImagenDTO imagenDTO) {
        try {
            ImagenDTO imagenCreadaDTO = imagenService.altaDTO(imagenDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(imagenCreadaDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody ImagenDTO imagenDTO) {
        try {
            ImagenDTO imagenActualizadaDTO = imagenService.modificarDTO(id, imagenDTO)
                .orElseThrow(() -> new ErrorServiceException("Imagen no encontrada con ID: " + id));
            return ResponseEntity.ok(imagenActualizadaDTO);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            imagenService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Imagen eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> buildErrorResponse(String mensaje, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(status).body(error);
    }
}
