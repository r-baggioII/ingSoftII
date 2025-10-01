package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Departamento;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.repositorios.DepartamentoRepository;
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
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final ProvinciaRepository provinciaRepository;

    public Page<Departamento> buscar(String filtro, String paisId, String provinciaId, Pageable pageable) {
        boolean tieneNombre = filtro != null && !filtro.isBlank();
        boolean tieneProvincia = provinciaId != null && !provinciaId.isBlank();
        boolean tienePais = paisId != null && !paisId.isBlank();

        if (tieneNombre && tieneProvincia) {
            return departamentoRepository.findByNombreContainingIgnoreCaseAndProvincia_IdAndEliminadoFalse(
                    filtro.trim(), provinciaId, pageable);
        }
        if (tieneProvincia) {
            return departamentoRepository.findByProvincia_IdAndEliminadoFalse(provinciaId, pageable);
        }
        if (tieneNombre && tienePais) {
            return departamentoRepository.findByNombreContainingIgnoreCaseAndProvincia_Pais_IdAndEliminadoFalse(
                    filtro.trim(), paisId, pageable);
        }
        if (tienePais) {
            return departamentoRepository.findByProvincia_Pais_IdAndEliminadoFalse(paisId, pageable);
        }
        if (tieneNombre) {
            return departamentoRepository.findByNombreContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
        }
        return departamentoRepository.findByEliminadoFalse(pageable);
    }

    public List<Departamento> listarPorProvincia(String provinciaId) {
        return departamentoRepository.findByProvincia_IdAndEliminadoFalseOrderByNombreAsc(provinciaId);
    }

    public List<Departamento> listarTodos() {
        return departamentoRepository.findByEliminadoFalseOrderByNombreAsc();
    }

    public Departamento buscarPorId(String id) {
        Departamento departamento = departamentoRepository.findById(id)
                .filter(dep -> !dep.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado"));
        if (departamento.getProvincia() != null) {
            departamento.setProvinciaId(departamento.getProvincia().getId());
            if (departamento.getProvincia().getPais() != null) {
                departamento.setPaisId(departamento.getProvincia().getPais().getId());
            }
        }
        return departamento;
    }

    @Transactional
    public Departamento guardar(Departamento departamento, String provinciaId) {
        if (provinciaId == null || provinciaId.isBlank()) {
            throw new IllegalArgumentException("La provincia es obligatoria");
        }
        Provincia provincia = provinciaRepository.findById(provinciaId)
                .filter(p -> !p.isEliminado())
                .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada"));

        String nombreNormalizado = departamento.getNombre() != null ? departamento.getNombre().trim() : "";
        if (nombreNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio");
        }

        departamentoRepository.findByNombreIgnoreCaseAndProvincia_IdAndEliminadoFalse(nombreNormalizado,
                provincia.getId())
                .filter(existente -> !existente.getId().equals(departamento.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un departamento con ese nombre en la provincia");
                });

        if (departamento.getId() != null && !departamento.getId().isBlank()) {
            Departamento existente = buscarPorId(departamento.getId());
            existente.setNombre(nombreNormalizado);
            existente.setProvincia(provincia);
            return existente;
        }

        departamento.setNombre(nombreNormalizado);
        departamento.setProvincia(provincia);
        return departamentoRepository.save(departamento);
    }

    @Transactional
    public void eliminar(String id) {
        Departamento departamento = buscarPorId(id);
        departamento.setEliminado(true);
    }
}
