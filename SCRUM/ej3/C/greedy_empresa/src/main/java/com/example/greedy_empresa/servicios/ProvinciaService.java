package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.repositorios.PaisRepository;
import com.example.greedy_empresa.repositorios.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProvinciaService extends BaseService<Provincia, ProvinciaRepository> {

    private final PaisRepository paisRepository;

    public ProvinciaService(ProvinciaRepository provinciaRepository, PaisRepository paisRepository) {
        super(provinciaRepository);
        this.paisRepository = paisRepository;
    }

    public Page<Provincia> buscar(String filtro, String paisId, Pageable pageable) {
        boolean tieneNombre = filtro != null && !filtro.isBlank();
        boolean tienePais = paisId != null && !paisId.isBlank();

        if (tieneNombre && tienePais) {
            return repositorio.findByNombreContainingIgnoreCaseAndPais_IdAndEliminadoFalse(filtro.trim(), paisId,
                    pageable);
        }
        if (tienePais) {
            return repositorio.findByPais_IdAndEliminadoFalse(paisId, pageable);
        }
        if (tieneNombre) {
            return repositorio.findByNombreContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
        }
        return repositorio.findByEliminadoFalse(pageable);
    }

    public List<Provincia> listarPorPais(String paisId) {
        return repositorio.findByPais_IdAndEliminadoFalseOrderByNombreAsc(paisId);
    }

    public List<Provincia> listarTodas() {
        return repositorio.findByEliminadoFalseOrderByNombreAsc();
    }

    @Override
    public Provincia buscarPorId(String id) {
        Provincia provincia = super.buscarPorId(id);
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

        repositorio.findByNombreIgnoreCaseAndPais_IdAndEliminadoFalse(nombreNormalizado, pais.getId())
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
        return repositorio.save(provincia);
    }

    @Override
    public Class<Provincia> getEntityClass() {
        return Provincia.class;
    }

    @Override
    protected String getEntityName() {
        return "Provincia";
    }
}