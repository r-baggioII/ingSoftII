package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.repositorios.PaisRepository;
import com.example.greedy_empresa.repositorios.ProvinciaRepository;
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
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;

    public Page<Provincia> buscar(String filtro, String paisId, Pageable pageable) {
        boolean tieneNombre = filtro != null && !filtro.isBlank();
        boolean tienePais = paisId != null && !paisId.isBlank();

        if (tieneNombre && tienePais) {
            return provinciaRepository.findByNombreContainingIgnoreCaseAndPais_IdAndEliminadoFalse(filtro.trim(), paisId,
                    pageable);
        }
        if (tienePais) {
            return provinciaRepository.findByPais_IdAndEliminadoFalse(paisId, pageable);
        }
        if (tieneNombre) {
            return provinciaRepository.findByNombreContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
        }
        return provinciaRepository.findByEliminadoFalse(pageable);
    }

    public List<Provincia> listarPorPais(String paisId) {
        return provinciaRepository.findByPais_IdAndEliminadoFalseOrderByNombreAsc(paisId);
    }

    public List<Provincia> listarTodas() {
        return provinciaRepository.findByEliminadoFalseOrderByNombreAsc();
    }

    public Provincia buscarPorId(String id) {
        Provincia provincia = provinciaRepository.findById(id)
                .filter(prov -> !prov.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Provincia no encontrada"));
        if (provincia.getPais() != null) {
            provincia.setPaisId(provincia.getPais().getId());
        }
        return provincia;
    }

    @Transactional
    public Provincia guardar(Provincia provincia, String paisId) {
        if (paisId == null || paisId.isBlank()) {
            throw new IllegalArgumentException("El país es obligatorio");
        }
        Pais pais = paisRepository.findById(paisId)
                .filter(p -> !p.isEliminado())
                .orElseThrow(() -> new IllegalArgumentException("País no encontrado"));

        String nombreNormalizado = provincia.getNombre() != null ? provincia.getNombre().trim() : "";
        if (nombreNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la provincia es obligatorio");
        }

        provinciaRepository.findByNombreIgnoreCaseAndPais_IdAndEliminadoFalse(nombreNormalizado, pais.getId())
                .filter(existente -> !existente.getId().equals(provincia.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe una provincia con ese nombre en el país");
                });

        if (provincia.getId() != null && !provincia.getId().isBlank()) {
            Provincia existente = buscarPorId(provincia.getId());
            existente.setNombre(nombreNormalizado);
            existente.setPais(pais);
            return existente;
        }

        provincia.setNombre(nombreNormalizado);
        provincia.setPais(pais);
        return provinciaRepository.save(provincia);
    }

    @Transactional
    public void eliminar(String id) {
        Provincia provincia = buscarPorId(id);
        provincia.setEliminado(true);
    }
}
