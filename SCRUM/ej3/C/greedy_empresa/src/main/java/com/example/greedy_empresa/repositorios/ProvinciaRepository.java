package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Provincia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProvinciaRepository extends BaseRepository<Provincia, String> {

    Page<Provincia> findByNombreContainingIgnoreCaseAndEliminadoFalse(String nombre, Pageable pageable);

    Page<Provincia> findByPais_IdAndEliminadoFalse(String paisId, Pageable pageable);

    Page<Provincia> findByNombreContainingIgnoreCaseAndPais_IdAndEliminadoFalse(String nombre, String paisId, Pageable pageable);

    List<Provincia> findByPais_IdAndEliminadoFalseOrderByNombreAsc(String paisId);

    List<Provincia> findByEliminadoFalseOrderByNombreAsc();

    Optional<Provincia> findByNombreIgnoreCaseAndPais_Id(String nombre, String paisId);

    Optional<Provincia> findByNombreIgnoreCaseAndPais_IdAndEliminadoFalse(String nombre, String paisId);
}