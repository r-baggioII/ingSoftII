package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;

@Controller
@RequestMapping("/imagenes")
public class ImagenController {

    private final RestTemplate restTemplate;
    private final GreedyApiProperties apiProperties;

    @Autowired
    public ImagenController(RestTemplate restTemplate, GreedyApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    @GetMapping("/{id}/contenido")
    public ResponseEntity<?> obtenerContenido(@PathVariable String id) {
        try {
            String url = apiProperties.getBaseUrl() + "/api/imagenes/" + id + "/contenido";

            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                HttpHeaders headers = new HttpHeaders();

                // Try to get image metadata to determine content type
                try {
                    String metadataUrl = apiProperties.getBaseUrl() + "/api/imagenes/" + id;
                    ResponseEntity<Object> metadataResponse = restTemplate.getForEntity(metadataUrl, Object.class);

                    if (metadataResponse.getBody() != null) {
                        // Parse the metadata to get mime type
                        // For now, default to common image types
                        headers.setContentType(MediaType.IMAGE_JPEG);
                    }
                } catch (Exception e) {
                    // If we can't get metadata, default to JPEG
                    headers.setContentType(MediaType.IMAGE_JPEG);
                }

                headers.setContentLength(response.getBody().length);
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image_" + id + "\"");

                return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
            }

            // Return fallback SVG if no image found
            return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(getFallbackImageBytes());

        } catch (HttpClientErrorException.NotFound e) {
            // Return fallback SVG if image not found
            return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(getFallbackImageBytes());
        } catch (Exception e) {
            // Return fallback SVG on any error
            return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(getFallbackImageBytes());
        }
    }

    private byte[] getFallbackImageBytes() {
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='50' height='40'>" +
                     "<rect width='50' height='40' fill='%23f8f9fa'/>" +
                     "<text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle' fill='%23999' font-size='10' font-family='Arial'>Sin imagen</text>" +
                     "</svg>";
        return svg.getBytes();
    }

    @GetMapping("/vehiculos/{caracteristicaId}")
    public ResponseEntity<?> obtenerImagenesVehiculo(@PathVariable String caracteristicaId) {
        try {
            String url = apiProperties.getBaseUrl() + "/imagenes?caracteristicaId=" + caracteristicaId;
            ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching vehicle images: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("imagen") org.springframework.web.multipart.MultipartFile file,
                                        @RequestParam("nombre") String nombre,
                                        @RequestParam("tipoImagen") String tipoImagen) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: No se ha seleccionado ningún archivo");
            }

            // Validate file size (5MB max)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Error: El archivo excede el tamaño máximo de 5MB");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Error: El archivo no es una imagen válida");
            }

            // Convert image to base64 string (this is what the API expects)
            byte[] imageBytes = file.getBytes();
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

            // Create ImagenDTO as JSON
            Map<String, Object> imagenDTO = new HashMap<>();
            imagenDTO.put("nombre", nombre);
            imagenDTO.put("mime", contentType);
            imagenDTO.put("contenido", base64Image);
            imagenDTO.put("tipoImagen", tipoImagen);
            imagenDTO.put("eliminado", false);

            // Send as JSON
            String url = apiProperties.getBaseUrl() + "/imagenes";

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            org.springframework.http.HttpEntity<Map<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(imagenDTO, headers);

            ResponseEntity<Object> response = restTemplate.postForEntity(url, requestEntity, Object.class);

            System.out.println("Response status: " + response.getStatusCode());
            System.out.println("Response body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                    .body(Map.of(
                        "error", true,
                        "message", "Error del servidor: " + response.getStatusCode(),
                        "status", response.getStatusCode().value()
                    ));
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Error del servidor al subir imagen (Status " + e.getStatusCode() + "): " + errorBody);

            // Return user-friendly error messages
            String errorMessage = "Error del servidor";
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                errorMessage = "Error: Servicio de imágenes no disponible. Por favor, intente más tarde.";
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                errorMessage = "Error: Los datos de la imagen son inválidos. Verifique el formato y tamaño.";
            } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                errorMessage = "Error: El servidor tuvo un problema al procesar la imagen. Por favor, intente de nuevo.";
            }

            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "error", true,
                "message", errorMessage,
                "status", e.getStatusCode().value()
            ));
        } catch (Exception e) {
            System.err.println("Error detallado al subir imagen: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", true,
                    "message", "Error interno al procesar la imagen. Por favor, intente de nuevo.",
                    "status", 500
                ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable String id) {
        try {
            String url = apiProperties.getBaseUrl() + "/imagenes/" + id;
            restTemplate.delete(url);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Imagen eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting image: " + e.getMessage());
        }
    }
}