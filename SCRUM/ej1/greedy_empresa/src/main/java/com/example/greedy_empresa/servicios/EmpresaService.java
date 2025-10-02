package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Empresa;
import com.example.greedy_empresa.repositorios.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public Page<Empresa> buscar(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return empresaRepository.findByEliminadoFalse(pageable);
        }
        return empresaRepository.findByRazonSocialContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
    }

    public Empresa buscarPorId(String id) {
        return empresaRepository.findById(id)
                .filter(emp -> !emp.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));
    }

    @Transactional
    public Empresa guardar(Empresa empresa) {
        String razonNormalizada = empresa.getRazonSocial() != null ? empresa.getRazonSocial().trim() : "";
        if (razonNormalizada.isEmpty()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }

        empresaRepository.findByRazonSocialIgnoreCaseAndEliminadoFalse(razonNormalizada)
                .filter(existente -> !existente.getId().equals(empresa.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe una empresa con esa razón social");
                });

        // Procesar direcciones
        if (empresa.getDirecciones() != null) {
            empresa.getDirecciones().forEach(direccion -> {
                if (direccion != null) {
                    direccion.setEmpresa(empresa);
                    direccion.setEliminado(false);
                }
            });
            // Remover direcciones nulas o vacías
            empresa.getDirecciones().removeIf(direccion -> 
                direccion == null || 
                (direccion.getCalle() == null || direccion.getCalle().isBlank()) ||
                (direccion.getNumero() == null || direccion.getNumero().isBlank()) ||
                direccion.getLocalidad() == null
            );
        }

        if (empresa.getId() != null && !empresa.getId().isBlank()) {
            Empresa existente = buscarPorId(empresa.getId());
            existente.setRazonSocial(razonNormalizada);
            existente.getDirecciones().clear();
            if (empresa.getDirecciones() != null) {
                existente.getDirecciones().addAll(empresa.getDirecciones());
            }
            return empresaRepository.save(existente);
        }

        empresa.setRazonSocial(razonNormalizada);
        return empresaRepository.save(empresa);
    }

    @Transactional
    public void eliminar(String id) {
        Empresa empresa = buscarPorId(id);
        empresa.setEliminado(true);
    }

    public long contarActivas() {
        return empresaRepository.countByEliminadoFalse();
    }

    public List<Empresa> obtenerTodasParaExcel() {
        return empresaRepository.findByEliminadoFalseOrderByRazonSocial();
    }
}
