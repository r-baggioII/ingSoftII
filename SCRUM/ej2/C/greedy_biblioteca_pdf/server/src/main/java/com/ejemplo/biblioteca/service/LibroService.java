package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.repository.AutorRepository;
import com.ejemplo.biblioteca.repository.LibroRepository;
import com.ejemplo.biblioteca.repository.PersonaRepository;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.LibroCreateRequest;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroUpdateRequest;
import com.ejemplo.biblioteca.web.dto.PersonaSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final PersonaRepository personaRepository;
    private final PdfStorageService pdfStorageService;

    @Transactional(readOnly = true)
    public Page<LibroDTO> search(Long autorId, Long personaId, String genero, Pageable pageable) {
        return libroRepository.search(
                        autorId,
                        personaId,
                        genero == null || genero.isBlank() ? null : genero,
                        pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public LibroDTO findById(Long id) {
        return libroRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
    }

    public LibroDTO create(LibroCreateRequest request, MultipartFile pdf) {
        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));
        Persona persona = personaRepository.findById(request.personaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

        Libro libro = new Libro();
        applyValues(libro, request.titulo(), request.fecha(), request.genero(), request.paginas(), autor, persona);
        libroRepository.save(libro);
        handlePdfUpload(libro, pdf, false);
        return toDto(libro);
    }

    public LibroDTO update(Long id, LibroUpdateRequest request, MultipartFile pdf) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));
        Persona persona = personaRepository.findById(request.personaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        applyValues(libro, request.titulo(), request.fecha(), request.genero(), request.paginas(), autor, persona);
        handlePdfUpload(libro, pdf, true);
        return toDto(libro);
    }

    public void delete(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        pdfStorageService.deletePdfIfExists(libro.getPdfFileName());
        libroRepository.delete(libro);
    }

    @Transactional(readOnly = true)
    public Page<LibroDTO> findByPersona(Long personaId, Pageable pageable) {
        if (!personaRepository.existsById(personaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada");
        }
        return libroRepository.search(null, personaId, null, pageable)
                .map(this::toDto);
    }

    public LibroPdfResource getLibroPdf(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        if (libro.getPdfFileName() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no tiene PDF asociado");
        }
        Resource resource = pdfStorageService.loadPdfAsResource(libro.getPdfFileName());
        return new LibroPdfResource(libro.getPdfFileName(), resource);
    }

    LibroDTO toDto(Libro libro) {
        Autor autor = libro.getAutor();
        Persona persona = libro.getPersona();
        AutorDTO autorDTO = new AutorDTO(
                autor.getId(),
                autor.getNombre(),
                autor.getApellido(),
                autor.getBiografia()
        );
        PersonaSummaryDTO personaSummaryDTO = new PersonaSummaryDTO(
                persona.getId(),
                persona.getNombre(),
                persona.getApellido()
        );
        return new LibroDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getFecha().toString(),
                libro.getGenero(),
                libro.getPaginas(),
                autorDTO,
                personaSummaryDTO,
                autor.getId(),
                persona.getId(),
                libro.getPdfFileName() != null
        );
    }

    private void applyValues(Libro libro,
                             String titulo,
                             String fecha,
                             String genero,
                             Integer paginas,
                             Autor autor,
                             Persona persona) {
        libro.setTitulo(titulo);
        LocalDate fechaPublicacion = LocalDate.parse(fecha);
        if (fechaPublicacion.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de publicación no puede ser futura");
        }
        libro.setFecha(fechaPublicacion);
        libro.setGenero(genero);
        libro.setPaginas(paginas);
        libro.setAutor(autor);
        libro.setPersona(persona);
    }

    private void handlePdfUpload(Libro libro, MultipartFile pdf, boolean allowDeleteExisting) {
        if (pdf == null || pdf.isEmpty()) {
            return;
        }
        if (libro.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El libro aún no tiene ID asignado");
        }
        if (allowDeleteExisting) {
            pdfStorageService.deletePdfIfExists(libro.getPdfFileName());
        }
        String fileName = pdfStorageService.storePdf(pdf, libro.getTitulo(), libro.getId());
        libro.setPdfFileName(fileName);
        libro.setPdfSizeBytes(pdf.getSize());
        libro.setPdfUploadedAt(Instant.now());
    }

    public record LibroPdfResource(String fileName, Resource resource) {
    }
}
