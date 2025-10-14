package com.ejemplo.biblioteca.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

@Service
public class PdfStorageServiceImpl implements PdfStorageService {

    private static final String PDF_PREFIX = "libro_";
    private static final String PDF_EXTENSION = ".pdf";

    private final Path storageDir;

    public PdfStorageServiceImpl(@Value("${app.storage.pdf-dir}") String storageDir) {
        this.storageDir = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    @Override
    public String storePdf(MultipartFile file, String slugTitulo, Long libroId) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo PDF es obligatorio");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase(MediaType.APPLICATION_PDF_VALUE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten archivos PDF");
        }

        ensureDirectoryExists();
        String safeSlug = toSlug(slugTitulo);
        String fileName = buildFileName(safeSlug, libroId);
        Path target = storageDir.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el PDF", ex);
        }
        return fileName;
    }

    @Override
    public Resource loadPdfAsResource(String fileName) {
        try {
            Path filePath = storageDir.resolve(Objects.requireNonNull(fileName)).normalize();
            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo PDF no encontrado");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo PDF no disponible");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ruta de PDF inválida", ex);
        }
    }

    @Override
    public void deletePdfIfExists(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        Path filePath = storageDir.resolve(fileName).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo eliminar el PDF anterior", ex);
        }
    }

    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(storageDir);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo preparar el directorio de PDFs", ex);
        }
    }

    private String buildFileName(String slug, Long libroId) {
        String safeSlug = slug.isBlank() ? "sin_titulo" : slug;
        return PDF_PREFIX + safeSlug + "_" + libroId + PDF_EXTENSION;
    }

    private String toSlug(String value) {
        if (value == null || value.isBlank()) {
            return "sin_titulo";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String cleaned = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return cleaned.isBlank() ? "sin_titulo" : cleaned;
    }
}
