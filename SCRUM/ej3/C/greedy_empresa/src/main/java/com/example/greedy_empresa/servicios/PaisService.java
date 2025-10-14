package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.repositorios.PaisRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PaisService extends BaseService<Pais, PaisRepository> {

    public PaisService(PaisRepository paisRepository) {
        super(paisRepository);
    }

    @Override
    public Page<Pais> buscar(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return repositorio.findByEliminadoFalse(pageable);
        }
        return repositorio.findByNombreContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
    }

    public List<Pais> listarActivos() {
        return repositorio.findByEliminadoFalseOrderByNombreAsc();
    }

    @Override
    @Transactional
    public Pais guardar(Pais pais) {
        String nombreNormalizado = pais.getNombre() != null ? pais.getNombre().trim() : "";
        if (nombreNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio");
        }

        repositorio.findByNombreIgnoreCaseAndEliminadoFalse(nombreNormalizado)
                .filter(existente -> !existente.getId().equals(pais.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un país con ese nombre");
                });

        if (pais.getId() != null && !pais.getId().isBlank()) {
            Pais existente = buscarPorId(pais.getId());
            existente.setNombre(nombreNormalizado);
            return existente;
        }

        pais.setNombre(nombreNormalizado);
        return repositorio.save(pais);
    }

    public long contarActivos() {
        return repositorio.countByEliminadoFalse();
    }

    @Override
    public Class<Pais> getEntityClass() {
        return Pais.class;
    }

    @Override
    protected String getEntityName() {
        return "País";
    }
}