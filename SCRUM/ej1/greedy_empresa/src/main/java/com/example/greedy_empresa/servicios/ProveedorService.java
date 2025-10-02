package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.entidades.ProveedorPersona;
import com.example.greedy_empresa.repositorios.ProveedorRepository;
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
        if (proveedor == null) {
            throw new IllegalArgumentException("El proveedor no puede ser nulo");
        }
        
        String cuitNormalizado = proveedor.getCuit() != null ? proveedor.getCuit().trim() : "";
        if (cuitNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El CUIT es obligatorio");
        }

        proveedorRepository.findByCuitIgnoreCaseAndEliminadoFalse(cuitNormalizado)
                .filter(existente -> !existente.getId().equals(proveedor.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un proveedor con ese CUIT");
                });

        // Crear o actualizar persona
        if (proveedor.getPersona() != null) {
            if (proveedor.getPersona().getId() == null) {
                // Crear nueva persona concreta
                ProveedorPersona persona = new ProveedorPersona();
                persona.setNombre(proveedor.getPersona().getNombre());
                persona.setApellido(proveedor.getPersona().getApellido());
                persona.setCorreoElectronico(proveedor.getPersona().getCorreoElectronico());
                persona.setTelefono(proveedor.getPersona().getTelefono());
                persona.setEliminado(false);
                proveedor.setPersona(persona);
            }
        } else {
            throw new IllegalArgumentException("Los datos de persona son obligatorios");
        }

        // Procesar direcciones
        if (proveedor.getDirecciones() != null) {
            proveedor.getDirecciones().forEach(direccion -> {
                if (direccion != null) {
                    direccion.setProveedor(proveedor);
                    direccion.setPersona(proveedor.getPersona());
                    direccion.setEliminado(false);
                }
            });
            // Remover direcciones nulas o vacías
            proveedor.getDirecciones().removeIf(direccion -> 
                direccion == null || 
                (direccion.getCalle() == null || direccion.getCalle().isBlank()) ||
                (direccion.getNumero() == null || direccion.getNumero().isBlank()) ||
                direccion.getLocalidad() == null
            );
        }

        if (proveedor.getId() != null && !proveedor.getId().isBlank()) {
            Proveedor existente = buscarPorId(proveedor.getId());
            existente.setCuit(cuitNormalizado);
            existente.setPersona(proveedor.getPersona());
            existente.getDirecciones().clear();
            if (proveedor.getDirecciones() != null) {
                existente.getDirecciones().addAll(proveedor.getDirecciones());
            }
            return proveedorRepository.save(existente);
        }

        proveedor.setCuit(cuitNormalizado);
        if (proveedor.getPersona() != null) {
            proveedor.getPersona().setEliminado(false);
        }
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

    public List<Proveedor> obtenerTodosParaPdf() {
        return proveedorRepository.findByEliminadoFalseOrderByCuit();
    }
}
