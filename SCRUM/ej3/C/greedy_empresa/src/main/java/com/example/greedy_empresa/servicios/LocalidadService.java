package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Departamento;
import com.example.greedy_empresa.entidades.Localidad;
import com.example.greedy_empresa.repositorios.DepartamentoRepository;
import com.example.greedy_empresa.repositorios.LocalidadRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LocalidadService extends BaseService<Localidad, LocalidadRepository> {

    private final DepartamentoRepository departamentoRepository;

    public LocalidadService(LocalidadRepository localidadRepository, DepartamentoRepository departamentoRepository) {
        super(localidadRepository);
        this.departamentoRepository = departamentoRepository;
    }

    public Page<Localidad> buscar(String filtro, String paisId, String provinciaId, String departamentoId,
            Pageable pageable) {
        boolean tieneNombre = filtro != null && !filtro.isBlank();
        boolean tieneDepartamento = departamentoId != null && !departamentoId.isBlank();
        boolean tieneProvincia = provinciaId != null && !provinciaId.isBlank();
        boolean tienePais = paisId != null && !paisId.isBlank();

        if (tieneNombre && tieneDepartamento) {
            return repositorio.findByNombreContainingIgnoreCaseAndDepartamento_IdAndEliminadoFalse(
                    filtro.trim(), departamentoId, pageable);
        }
        if (tieneDepartamento) {
            return repositorio.findByDepartamento_IdAndEliminadoFalse(departamentoId, pageable);
        }
        if (tieneNombre && tieneProvincia) {
            return repositorio.findByNombreContainingIgnoreCaseAndDepartamento_Provincia_IdAndEliminadoFalse(
                    filtro.trim(), provinciaId, pageable);
        }
        if (tieneProvincia) {
            return repositorio.findByDepartamento_Provincia_IdAndEliminadoFalse(provinciaId, pageable);
        }
        if (tieneNombre && tienePais) {
            return repositorio.findByNombreContainingIgnoreCaseAndDepartamento_Provincia_Pais_IdAndEliminadoFalse(
                    filtro.trim(), paisId, pageable);
        }
        if (tienePais) {
            return repositorio.findByDepartamento_Provincia_Pais_IdAndEliminadoFalse(paisId, pageable);
        }
        if (tieneNombre) {
            return repositorio.findByNombreContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
        }
        return repositorio.findByEliminadoFalse(pageable);
    }

    public List<Localidad> listarPorDepartamento(String departamentoId) {
        return repositorio.findByDepartamento_IdAndEliminadoFalseOrderByNombreAsc(departamentoId);
    }

    @Override
    public Localidad buscarPorId(String id) {
        Localidad localidad = super.buscarPorId(id);
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

        repositorio.findByNombreIgnoreCaseAndDepartamento_IdAndEliminadoFalse(nombreNormalizado,
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
        return repositorio.save(localidad);
    }

    @Override
    public Class<Localidad> getEntityClass() {
        return Localidad.class;
    }

    @Override
    protected String getEntityName() {
        return "Localidad";
    }
}