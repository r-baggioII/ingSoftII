package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Domicilio;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.repository.LocalidadRepository;
import com.ejemplo.biblioteca.repository.PersonaRepository;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaService extends AbstractCrudService<Persona, Long> {

    private final PersonaRepository personaRepository;
    private final LocalidadRepository localidadRepository;
    private final LibroService libroService;

    @Override
    protected JpaRepository<Persona, Long> repository() {
        return personaRepository;
    }

    @Transactional(readOnly = true)
    public Page<Persona> search(String apellido, Integer dni, Pageable pageable) {
        String apellidoFilter = apellido == null || apellido.isBlank() ? null : apellido;
        return personaRepository.search(apellidoFilter, dni, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Libro> findLibros(Long personaId, Pageable pageable) {
        return libroService.findByPersona(personaId, pageable);
    }

    @Override
    protected void validateNew(Persona entity) {
        validateCommon(entity);
        validateUniqueDni(entity.getDni(), null);
    }

    @Override
    protected void validateUpdate(Long id, Persona incoming, Persona current) {
        validateCommon(incoming);
        validateUniqueDni(incoming.getDni(), id);
    }

    @Override
    protected Persona transformOnCreate(Persona entity) {
        resolveLocalidad(entity);
        return entity;
    }

    @Override
    protected Persona mergeForUpdate(Persona incoming, Persona current) {
        resolveLocalidad(incoming);
        current.setNombre(incoming.getNombre());
        current.setApellido(incoming.getApellido());
        current.setDni(incoming.getDni());
        applyDomicilio(current, incoming.getDomicilio());
        return current;
    }

    private void validateCommon(Persona persona) {
        if (!StringUtils.hasText(persona.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (!StringUtils.hasText(persona.getApellido())) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (persona.getDni() == null || persona.getDni() <= 0) {
            throw new IllegalArgumentException("El DNI debe ser positivo");
        }
        Domicilio domicilio = persona.getDomicilio();
        if (domicilio == null) {
            throw new IllegalArgumentException("El domicilio es obligatorio");
        }
        if (!StringUtils.hasText(domicilio.getCalle())) {
            throw new IllegalArgumentException("La calle es obligatoria");
        }
        if (domicilio.getNumero() == null || domicilio.getNumero() <= 0) {
            throw new IllegalArgumentException("El número de domicilio debe ser positivo");
        }
        if (domicilio.getLocalidad() == null || domicilio.getLocalidad().getId() == null) {
            throw new IllegalArgumentException("localidadId requerido");
        }
    }

    private void validateUniqueDni(Integer dni, Long currentId) {
        personaRepository.findByDni(dni).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new IllegalArgumentException("Ya existe una persona con DNI " + dni);
            }
        });
    }

    private void resolveLocalidad(Persona persona) {
        Domicilio domicilio = persona.getDomicilio();
        Long localidadId = domicilio != null && domicilio.getLocalidad() != null ? domicilio.getLocalidad().getId() : null;
        if (localidadId == null) {
            throw new IllegalArgumentException("localidadId requerido");
        }
        Localidad localidad = localidadRepository.findById(localidadId)
                .orElseThrow(() -> new EntityNotFoundException("Localidad no existe id=" + localidadId));
        domicilio.setLocalidad(localidad);
    }

    private void applyDomicilio(Persona current, Domicilio origen) {
        Domicilio destino = current.getDomicilio();
        if (destino == null) {
            destino = new Domicilio();
            current.setDomicilio(destino);
        }
        destino.setCalle(origen.getCalle());
        destino.setNumero(origen.getNumero());
        destino.setLocalidad(origen.getLocalidad());
    }
}
