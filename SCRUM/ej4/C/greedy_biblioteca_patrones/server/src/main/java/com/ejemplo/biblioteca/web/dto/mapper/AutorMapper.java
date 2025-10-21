package com.ejemplo.biblioteca.web.dto.mapper;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.AutorRequest;
import org.springframework.stereotype.Component;

@Component
public class AutorMapper {

    public Autor toEntity(AutorRequest request) {
        return toEntity(request, null);
    }

    public Autor toEntity(AutorRequest request, Long id) {
        Autor autor = new Autor();
        autor.setId(id);
        autor.setNombre(request.nombre());
        autor.setApellido(request.apellido());
        autor.setBiografia(request.biografia());
        return autor;
    }

    public AutorDTO toDto(Autor autor) {
        return new AutorDTO(
                autor.getId(),
                autor.getNombre(),
                autor.getApellido(),
                autor.getBiografia()
        );
    }
}
