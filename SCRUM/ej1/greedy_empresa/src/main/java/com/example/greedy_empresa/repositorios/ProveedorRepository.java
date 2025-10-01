package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Proveedor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, String> {

    Page<Proveedor> findByEliminadoFalse(Pageable pageable);

    Page<Proveedor> findByCuitContainingIgnoreCaseAndEliminadoFalse(String cuit, Pageable pageable);

    Optional<Proveedor> findByCuitIgnoreCase(String cuit);

    Optional<Proveedor> findByCuitIgnoreCaseAndEliminadoFalse(String cuit);

    long countByEliminadoFalse();

    List<Proveedor> findByEliminadoFalseOrderByCuit();
}
