package com.ejemplo.biblioteca.web.dto.mapper;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroRequest;
import com.ejemplo.biblioteca.web.dto.PersonaSummaryDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class LibroMapper {

    public Libro toEntity(LibroRequest request) {
        return toEntity(request, null);
    }

    public Libro toEntity(LibroRequest request, Long id) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setTitulo(request.titulo());
        libro.setFecha(parseFecha(request.fecha()));
        libro.setGenero(request.genero());
        libro.setPaginas(request.paginas());

        Autor autor = new Autor();
        autor.setId(request.autorId());
        libro.setAutor(autor);

        Persona persona = new Persona();
        persona.setId(request.personaId());
        libro.setPersona(persona);
        return libro;
    }

    public LibroDTO toDto(Libro libro) {
        Autor autor = libro.getAutor();
        Persona persona = libro.getPersona();
        AutorDTO autorDTO = new AutorDTO(
                autor.getId(),
                autor.getNombre(),
                autor.getApellido(),
                autor.getBiografia()
        );
        PersonaSummaryDTO personaDTO = new PersonaSummaryDTO(
                persona.getId(),
                persona.getNombre(),
                persona.getApellido()
        );
        return new LibroDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getFecha() != null ? libro.getFecha().toString() : null,
                libro.getGenero(),
                libro.getPaginas(),
                autorDTO,
                personaDTO
        );
    }

    private LocalDate parseFecha(String fecha) {
        try {
            return LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use yyyy-MM-dd");
        }
    }
}
