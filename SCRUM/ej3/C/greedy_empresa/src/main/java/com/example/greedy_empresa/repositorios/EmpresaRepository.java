package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Empresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, String> {

    Page<Empresa> findByEliminadoFalse(Pageable pageable);

    Page<Empresa> findByRazonSocialContainingIgnoreCaseAndEliminadoFalse(String razonSocial, Pageable pageable);

    Optional<Empresa> findByRazonSocialIgnoreCase(String razonSocial);

    Optional<Empresa> findByRazonSocialIgnoreCaseAndEliminadoFalse(String razonSocial);

    long countByEliminadoFalse();

    List<Empresa> findByEliminadoFalseOrderByRazonSocial();
}
