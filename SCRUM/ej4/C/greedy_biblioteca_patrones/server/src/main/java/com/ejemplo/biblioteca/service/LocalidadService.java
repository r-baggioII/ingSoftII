package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.repository.LocalidadRepository;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class LocalidadService extends AbstractCrudService<Localidad, Long> {

    private final LocalidadRepository localidadRepository;

    @Override
    protected JpaRepository<Localidad, Long> repository() {
        return localidadRepository;
    }

    @Override
    protected void validateNew(Localidad entity) {
        validateDenominacion(entity);
        ensureUnique(entity, null);
    }

    @Override
    protected void validateUpdate(Long id, Localidad incoming, Localidad current) {
        validateDenominacion(incoming);
        ensureUnique(incoming, id);
    }

    @Override
    protected Localidad mergeForUpdate(Localidad incoming, Localidad current) {
        current.setDenominacion(incoming.getDenominacion());
        return current;
    }

    private void validateDenominacion(Localidad localidad) {
        if (!StringUtils.hasText(localidad.getDenominacion())) {
            throw new IllegalArgumentException("La denominación es obligatoria");
        }
    }

    private void ensureUnique(Localidad localidad, Long currentId) {
        localidadRepository.findByDenominacionIgnoreCase(localidad.getDenominacion())
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getId().equals(currentId)) {
                        throw new IllegalArgumentException("Ya existe una localidad con esa denominación");
                    }
                });
    }
}
