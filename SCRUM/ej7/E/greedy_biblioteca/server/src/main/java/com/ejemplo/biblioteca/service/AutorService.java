package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.repository.AutorRepository;
import com.ejemplo.biblioteca.web.dto.AutorDTO;
import com.ejemplo.biblioteca.web.dto.AutorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AutorService {

    private final AutorRepository autorRepository;

    @Transactional(readOnly = true)
    public List<AutorDTO> findAll() {
        return autorRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutorDTO findById(Long id) {
        return toDto(requireAutor(id));
    }

    public AutorDTO create(AutorRequest request) {
        if (autorRepository.existsByNombreIgnoreCaseAndApellidoIgnoreCase(request.nombre(), request.apellido())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El autor ya existe");
        }
        Autor autor = new Autor();
        applyValues(autor, request);
        return toDto(autorRepository.save(autor));
    }

    public AutorDTO update(Long id, AutorRequest request) {
        Autor autor = requireAutor(id);
        applyValues(autor, request);
        return toDto(autorRepository.save(autor));
    }

    public void delete(Long id) {
        autorRepository.delete(requireAutor(id));
    }

    private void applyValues(Autor autor, AutorRequest request) {
        autor.setNombre(request.nombre());
        autor.setApellido(request.apellido());
        autor.setBiografia(request.biografia());
    }

    private AutorDTO toDto(Autor autor) {
        return DtoMapper.toAutorDto(autor);
    }

    private Autor requireAutor(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));
    }
}
