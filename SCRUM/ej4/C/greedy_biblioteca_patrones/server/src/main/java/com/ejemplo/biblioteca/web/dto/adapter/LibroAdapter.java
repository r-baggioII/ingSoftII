package com.ejemplo.biblioteca.web.dto.adapter;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.domain.TipoLibro;
import com.ejemplo.biblioteca.patterns.creacional.LibroBuilder;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LibroRequest;
import com.ejemplo.biblioteca.web.dto.PersonaSummaryDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class LibroAdapter implements EntityAdapter<LibroRequest, Libro, LibroDTO> {

    @Override
    public Libro toEntity(LibroRequest request) {
        return buildFromRequest(request, null);
    }

    @Override
    public Libro toEntity(LibroRequest request, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El identificador no puede ser nulo para actualización");
        }
        return buildFromRequest(request, id);
    }

    @Override
    public LibroDTO toDto(Libro libro) {
        Autor autor = libro.getAutor();
        Persona persona = libro.getPersona();

        AutorDTO autorDTO = autor != null
                ? new AutorDTO(autor.getId(), autor.getNombre(), autor.getApellido(), autor.getBiografia())
                : null;
        PersonaSummaryDTO personaDTO = persona != null
                ? new PersonaSummaryDTO(persona.getId(), persona.getNombre(), persona.getApellido())
                : null;

        return new LibroDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getFecha() != null ? libro.getFecha().toString() : null,
                libro.getGenero(),
                libro.getPaginas(),
                libro.getTipo(),
                libro.getPesoGramos(),
                libro.getTamanoMb(),
                autorDTO,
                personaDTO
        );
    }

    private Libro buildFromRequest(LibroRequest request, Long id) {
        LibroBuilder builder = LibroBuilder.nuevo()
                .conId(id)
                .conTitulo(request.titulo())
                .conFecha(parseFecha(request.fecha()))
                .conGenero(request.genero())
                .conPaginas(request.paginas())
                .conTipo(request.tipo());

        Autor autor = new Autor();
        autor.setId(request.autorId());
        builder.conAutor(autor);

        Persona persona = new Persona();
        persona.setId(request.personaId());
        builder.conPersona(persona);

        if (request.tipo() == TipoLibro.FISICO) {
            builder.conPesoGramos(request.pesoGramos());
        } else if (request.tipo() == TipoLibro.DIGITAL) {
            builder.conTamanoMb(request.tamanoMb());
        }

        return builder.build();
    }

    private LocalDate parseFecha(String fecha) {
        if (!StringUtils.hasText(fecha)) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        try {
            return LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use yyyy-MM-dd");
        }
    }
}
