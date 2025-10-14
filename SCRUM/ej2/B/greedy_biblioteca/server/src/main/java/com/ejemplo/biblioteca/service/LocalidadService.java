package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.repository.LocalidadRepository;
import com.ejemplo.biblioteca.web.dto.LocalidadDTO;
import com.ejemplo.biblioteca.web.dto.LocalidadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocalidadService {

    private final LocalidadRepository localidadRepository;

    @Transactional(readOnly = true)
    public List<LocalidadDTO> findAll() {
        return localidadRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocalidadDTO findById(Long id) {
        return localidadRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Localidad no encontrada"));
    }

    public LocalidadDTO create(LocalidadRequest request) {
        if (localidadRepository.existsByDenominacionIgnoreCase(request.denominacion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una localidad con esa denominación");
        }
        Localidad localidad = new Localidad();
        localidad.setDenominacion(request.denominacion());
        return toDto(localidadRepository.save(localidad));
    }

    private LocalidadDTO toDto(Localidad localidad) {
        return new LocalidadDTO(localidad.getId(), localidad.getDenominacion());
    }
}
