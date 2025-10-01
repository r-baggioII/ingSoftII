package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Departamento;
import com.example.greedy_empresa.entidades.Localidad;
import com.example.greedy_empresa.repositorios.DepartamentoRepository;
import com.example.greedy_empresa.repositorios.LocalidadRepository;
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
public class LocalidadService {

    private final LocalidadRepository localidadRepository;
    private final DepartamentoRepository departamentoRepository;

    public Page<Localidad> buscar(String filtro, String paisId, String provinciaId, String departamentoId,
            Pageable pageable) {
        boolean tieneNombre = filtro != null && !filtro.isBlank();
        boolean tieneDepartamento = departamentoId != null && !departamentoId.isBlank();
        boolean tieneProvincia = provinciaId != null && !provinciaId.isBlank();
        boolean tienePais = paisId != null && !paisId.isBlank();

        if (tieneNombre && tieneDepartamento) {
            return localidadRepository.findByNombreContainingIgnoreCaseAndDepartamento_IdAndEliminadoFalse(
                    filtro.trim(), departamentoId, pageable);
        }
        if (tieneDepartamento) {
            return localidadRepository.findByDepartamento_IdAndEliminadoFalse(departamentoId, pageable);
        }
        if (tieneNombre && tieneProvincia) {
            return localidadRepository.findByNombreContainingIgnoreCaseAndDepartamento_Provincia_IdAndEliminadoFalse(
                    filtro.trim(), provinciaId, pageable);
        }
        if (tieneProvincia) {
            return localidadRepository.findByDepartamento_Provincia_IdAndEliminadoFalse(provinciaId, pageable);
        }
        if (tieneNombre && tienePais) {
            return localidadRepository.findByNombreContainingIgnoreCaseAndDepartamento_Provincia_Pais_IdAndEliminadoFalse(
                    filtro.trim(), paisId, pageable);
        }
        if (tienePais) {
            return localidadRepository.findByDepartamento_Provincia_Pais_IdAndEliminadoFalse(paisId, pageable);
        }
        if (tieneNombre) {
            return localidadRepository.findByNombreContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
        }
        return localidadRepository.findByEliminadoFalse(pageable);
    }

    public List<Localidad> listarPorDepartamento(String departamentoId) {
        return localidadRepository.findByDepartamento_IdAndEliminadoFalseOrderByNombreAsc(departamentoId);
    }

    public Localidad buscarPorId(String id) {
        Localidad localidad = localidadRepository.findById(id)
                .filter(loc -> !loc.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Localidad no encontrada"));
        if (localidad.getDepartamento() != null) {
            localidad.setDepartamentoId(localidad.getDepartamento().getId());
            if (localidad.getDepartamento().getProvincia() != null) {
                localidad.setProvinciaId(localidad.getDepartamento().getProvincia().getId());
                if (localidad.getDepartamento().getProvincia().getPais() != null) {
                    localidad.setPaisId(localidad.getDepartamento().getProvincia().getPais().getId());
                }
            }
        }
        return localidad;
    }

    @Transactional
    public Localidad guardar(Localidad localidad, String departamentoId) {
        if (departamentoId == null || departamentoId.isBlank()) {
            throw new IllegalArgumentException("El departamento es obligatorio");
        }
        Departamento departamento = departamentoRepository.findById(departamentoId)
                .filter(dep -> !dep.isEliminado())
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));

        String nombreNormalizado = localidad.getNombre() != null ? localidad.getNombre().trim() : "";
        if (nombreNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la localidad es obligatorio");
        }
        String codigoPostal = localidad.getCodigoPostal() != null ? localidad.getCodigoPostal().trim() : "";
        if (codigoPostal.isEmpty()) {
            throw new IllegalArgumentException("El código postal es obligatorio");
        }

        localidadRepository.findByNombreIgnoreCaseAndDepartamento_IdAndEliminadoFalse(nombreNormalizado,
                departamento.getId())
                .filter(existente -> !existente.getId().equals(localidad.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe una localidad con ese nombre en el departamento");
                });

        if (localidad.getId() != null && !localidad.getId().isBlank()) {
            Localidad existente = buscarPorId(localidad.getId());
            existente.setNombre(nombreNormalizado);
            existente.setCodigoPostal(codigoPostal);
            existente.setDepartamento(departamento);
            return existente;
        }

        localidad.setNombre(nombreNormalizado);
        localidad.setCodigoPostal(codigoPostal);
        localidad.setDepartamento(departamento);
        return localidadRepository.save(localidad);
    }

    @Transactional
    public void eliminar(String id) {
        Localidad localidad = buscarPorId(id);
        localidad.setEliminado(true);
    }
}
