package org.example.controller;

import org.example.entity.Videojuego;
import org.example.service.ServicioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/videojuegos")
public class ControladorVideojuego {

    @Autowired 
    private ServicioVideojuego svcVideojuego;

    // ==================== ENDPOINTS REST ====================

    /**
     * GET /api/v1/videojuegos
     * Obtiene todos los videojuegos
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            return ResponseEntity.ok(svcVideojuego.listar());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al obtener videojuegos",
                            "mensaje", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * GET /api/v1/videojuegos/activos
     * Obtiene solo los videojuegos activos
     */
    @GetMapping("/activos")
    public ResponseEntity<?> obtenerActivos() {
        try {
            return ResponseEntity.ok(svcVideojuego.buscarTodosActivos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al obtener videojuegos activos",
                            "mensaje", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * GET /api/v1/videojuegos/{id}
     * Obtiene un videojuego por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Videojuego videojuego = svcVideojuego.obtener(id);
            if (videojuego == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Videojuego no encontrado",
                                "id", id,
                                "timestamp", System.currentTimeMillis()
                        ));
            }
            return ResponseEntity.ok(videojuego);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al obtener videojuego",
                            "mensaje", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * GET /api/v1/videojuegos/buscar?titulo=texto
     * Busca videojuegos por título
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorTitulo(@RequestParam String titulo) {
        try {
            return ResponseEntity.ok(svcVideojuego.buscarPorTitulo(titulo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al buscar videojuegos",
                            "mensaje", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * POST /api/v1/videojuegos
     * Crea un nuevo videojuego
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Videojuego videojuego) {
        try {
            svcVideojuego.alta(videojuego);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Videojuego creado exitosamente",
                            "videojuego", videojuego,
                            "timestamp", System.currentTimeMillis()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error al guardar videojuego",
                            "mensaje", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * PUT /api/v1/videojuegos/{id}
     * Actualiza un videojuego existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@RequestBody Videojuego videojuego, @PathVariable Long id) {
        try {
            svcVideojuego.modificar(videojuego, id);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Videojuego actualizado exitosamente",
                    "videojuego", videojuego,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error al actualizar videojuego",
                            "mensaje", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * DELETE /api/v1/videojuegos/{id}
     * Elimina (desactiva) un videojuego
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            svcVideojuego.baja(id);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Videojuego eliminado exitosamente",
                    "id", id,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error al eliminar videojuego",
                            "mensaje", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }
}
