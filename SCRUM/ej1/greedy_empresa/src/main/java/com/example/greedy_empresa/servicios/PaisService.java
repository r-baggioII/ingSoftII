package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.repositorios.PaisRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaisService {

    private final PaisRepository paisRepository;

    public Page<Pais> buscar(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return paisRepository.findByEliminadoFalse(pageable);
        }
        return paisRepository.findByNombreContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
    }

    public List<Pais> listarActivos() {
        return paisRepository.findByEliminadoFalseOrderByNombreAsc();
    }

    public Pais buscarPorId(String id) {
        return paisRepository.findById(id)
                .filter(pais -> !pais.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException("País no encontrado"));
    }

    @Transactional
    public Pais guardar(Pais pais) {
        String nombreNormalizado = pais.getNombre() != null ? pais.getNombre().trim() : "";
        if (nombreNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio");
        }

        paisRepository.findByNombreIgnoreCaseAndEliminadoFalse(nombreNormalizado)
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
        return paisRepository.save(pais);
    }

    @Transactional
    public void eliminar(String id) {
        Pais pais = buscarPorId(id);
        pais.setEliminado(true);
    }

    public long contarActivos() {
        return paisRepository.countByEliminadoFalse();
    }
}
