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
        return autorRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));
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
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));
        applyValues(autor, request);
        return toDto(autorRepository.save(autor));
    }

    public void delete(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado");
        }
        autorRepository.deleteById(id);
    }

    private void applyValues(Autor autor, AutorRequest request) {
        autor.setNombre(request.nombre());
        autor.setApellido(request.apellido());
        autor.setBiografia(request.biografia());
    }

    private AutorDTO toDto(Autor autor) {
        return new AutorDTO(
                autor.getId(),
                autor.getNombre(),
                autor.getApellido(),
                autor.getBiografia()
        );
    }
}
