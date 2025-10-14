package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.repository.AutorRepository;
import com.ejemplo.biblioteca.repository.LibroRepository;
import com.ejemplo.biblioteca.repository.PersonaRepository;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroRequest;
import com.ejemplo.biblioteca.web.dto.PersonaSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final PersonaRepository personaRepository;

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

    public LibroDTO create(LibroRequest request) {
        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));
        Persona persona = personaRepository.findById(request.personaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

        Libro libro = new Libro();
        applyValues(libro, request, autor, persona);
        return toDto(libroRepository.save(libro));
    }

    public LibroDTO update(Long id, LibroRequest request) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));
        Persona persona = personaRepository.findById(request.personaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        applyValues(libro, request, autor, persona);
        return toDto(libroRepository.save(libro));
    }

    public void delete(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado");
        }
        libroRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<LibroDTO> findByPersona(Long personaId, Pageable pageable) {
        if (!personaRepository.existsById(personaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada");
        }
        return libroRepository.search(null, personaId, null, pageable)
                .map(this::toDto);
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
                personaSummaryDTO
        );
    }

    private void applyValues(Libro libro, LibroRequest request, Autor autor, Persona persona) {
        libro.setTitulo(request.titulo());
        LocalDate fechaPublicacion = LocalDate.parse(request.fecha());
        if (fechaPublicacion.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de publicación no puede ser futura");
        }
        libro.setFecha(fechaPublicacion);
        libro.setGenero(request.genero());
        libro.setPaginas(request.paginas());
        libro.setAutor(autor);
        libro.setPersona(persona);
    }
}
