package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Pais;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaisRepository extends BaseRepository<Pais, String> {

    List<Pais> findByEliminadoFalseOrderByNombreAsc();

    long countByEliminadoFalse();

    Page<Pais> findByNombreContainingIgnoreCaseAndEliminadoFalse(String nombre, Pageable pageable);

    Optional<Pais> findByNombreIgnoreCase(String nombre);

    Optional<Pais> findByNombreIgnoreCaseAndEliminadoFalse(String nombre);
}