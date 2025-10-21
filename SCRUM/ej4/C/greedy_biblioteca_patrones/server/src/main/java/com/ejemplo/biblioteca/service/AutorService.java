package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.repository.AutorRepository;
import com.ejemplo.biblioteca.service.base.AbstractCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class AutorService extends AbstractCrudService<Autor, Long> {

    private final AutorRepository autorRepository;

    @Override
    protected JpaRepository<Autor, Long> repository() {
        return autorRepository;
    }

    @Override
    protected void validateNew(Autor entity) {
        validateFields(entity);
        ensureUnique(entity, null);
    }

    @Override
    protected void validateUpdate(Long id, Autor incoming, Autor current) {
        validateFields(incoming);
        ensureUnique(incoming, id);
    }

    @Override
    protected Autor mergeForUpdate(Autor incoming, Autor current) {
        current.setNombre(incoming.getNombre());
        current.setApellido(incoming.getApellido());
        current.setBiografia(incoming.getBiografia());
        return current;
    }

    private void validateFields(Autor autor) {
        if (!StringUtils.hasText(autor.getNombre())) {
            throw new IllegalArgumentException("El nombre del autor es obligatorio");
        }
        if (!StringUtils.hasText(autor.getApellido())) {
            throw new IllegalArgumentException("El apellido del autor es obligatorio");
        }
    }

    private void ensureUnique(Autor autor, Long currentId) {
        autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase(autor.getNombre(), autor.getApellido())
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getId().equals(currentId)) {
                        throw new IllegalArgumentException("El autor ya existe");
                    }
                });
    }

    @Transactional
    public Autor clonarAutor(Long id) {
        Autor original = requireOne(id);
        Autor clon = original.clonar();
        clon.setNombre(original.getNombre() + " (Clon " + original.getId() + ")");
        ensureUnique(clon, null);
        return autorRepository.save(clon);
    }
}
