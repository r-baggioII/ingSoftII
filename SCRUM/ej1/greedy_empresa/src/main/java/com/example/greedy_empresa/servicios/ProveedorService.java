package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.repositorios.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public Page<Proveedor> buscar(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return proveedorRepository.findByEliminadoFalse(pageable);
        }
        return proveedorRepository.findByCuitContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
    }

    public Proveedor buscarPorId(String id) {
        return proveedorRepository.findById(id)
                .filter(prov -> !prov.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));
    }

    @Transactional
    public Proveedor guardar(Proveedor proveedor) {
        String cuitNormalizado = proveedor.getCuit() != null ? proveedor.getCuit().trim() : "";
        if (cuitNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El CUIT es obligatorio");
        }

        proveedorRepository.findByCuitIgnoreCaseAndEliminadoFalse(cuitNormalizado)
                .filter(existente -> !existente.getId().equals(proveedor.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un proveedor con ese CUIT");
                });

        if (proveedor.getId() != null && !proveedor.getId().isBlank()) {
            Proveedor existente = buscarPorId(proveedor.getId());
            existente.setCuit(cuitNormalizado);
            return existente;
        }

        proveedor.setCuit(cuitNormalizado);
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public void eliminar(String id) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.setEliminado(true);
    }

    public long contarActivos() {
        return proveedorRepository.countByEliminadoFalse();
    }
}
