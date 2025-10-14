package com.ejemplo.biblioteca.web.controller;

import com.ejemplo.biblioteca.service.LibroService;
import com.ejemplo.biblioteca.web.dto.LibroCreateRequest;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public Page<LibroDTO> search(
            @RequestParam(required = false) Long autorId,
            @RequestParam(required = false) Long personaId,
            @RequestParam(required = false) String genero,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return libroService.search(autorId, personaId, genero, pageable);
    }

    @GetMapping("/{id}")
    public LibroDTO findById(@PathVariable Long id) {
        return libroService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public LibroDTO createLibro(
            @Valid @RequestPart("data") LibroCreateRequest data,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf
    ) {
        return libroService.create(data, pdf);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LibroDTO updateLibro(
            @PathVariable Long id,
            @Valid @RequestPart("data") LibroUpdateRequest data,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf
    ) {
        return libroService.update(id, data, pdf);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        libroService.delete(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getLibroPdf(@PathVariable Long id) {
        LibroService.LibroPdfResource pdf = libroService.getLibroPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdf.fileName() + "\"")
                .body(pdf.resource());
    }
}
