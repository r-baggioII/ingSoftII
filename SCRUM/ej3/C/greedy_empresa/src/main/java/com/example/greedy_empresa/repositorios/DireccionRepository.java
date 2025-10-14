package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Direccion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DireccionRepository extends JpaRepository<Direccion, String> {

    Optional<Direccion> findByIdAndEliminadoFalse(String id);

    Page<Direccion> findByEmpresa_IdAndEliminadoFalse(String empresaId, Pageable pageable);

    List<Direccion> findByEmpresa_IdAndEliminadoFalseOrderByCalleAsc(String empresaId);

    Page<Direccion> findByProveedor_IdAndEliminadoFalse(String proveedorId, Pageable pageable);

    List<Direccion> findByProveedor_IdAndEliminadoFalseOrderByCalleAsc(String proveedorId);

    Page<Direccion> findByPersona_IdAndEliminadoFalse(String personaId, Pageable pageable);

    List<Direccion> findByPersona_IdAndEliminadoFalseOrderByCalleAsc(String personaId);

    Page<Direccion> findByLocalidad_CodigoPostalContainingIgnoreCaseAndEliminadoFalse(String codigoPostal,
            Pageable pageable);
}
