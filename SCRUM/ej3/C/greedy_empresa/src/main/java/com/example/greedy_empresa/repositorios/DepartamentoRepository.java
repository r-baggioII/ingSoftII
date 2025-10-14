package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Departamento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartamentoRepository extends BaseRepository<Departamento, String> {

    Page<Departamento> findByNombreContainingIgnoreCaseAndEliminadoFalse(String nombre, Pageable pageable);

    Page<Departamento> findByProvincia_IdAndEliminadoFalse(String provinciaId, Pageable pageable);

    Page<Departamento> findByNombreContainingIgnoreCaseAndProvincia_IdAndEliminadoFalse(String nombre, String provinciaId,
            Pageable pageable);

    Page<Departamento> findByProvincia_Pais_IdAndEliminadoFalse(String paisId, Pageable pageable);

    Page<Departamento> findByNombreContainingIgnoreCaseAndProvincia_Pais_IdAndEliminadoFalse(String nombre,
            String paisId, Pageable pageable);

    List<Departamento> findByProvincia_IdAndEliminadoFalseOrderByNombreAsc(String provinciaId);

    List<Departamento> findByEliminadoFalseOrderByNombreAsc();

    Optional<Departamento> findByNombreIgnoreCaseAndProvincia_Id(String nombre, String provinciaId);

    Optional<Departamento> findByNombreIgnoreCaseAndProvincia_IdAndEliminadoFalse(String nombre, String provinciaId);
}