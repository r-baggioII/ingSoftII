package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Localidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocalidadRepository extends BaseRepository<Localidad, String> {

    Page<Localidad> findByNombreContainingIgnoreCaseAndEliminadoFalse(String nombre, Pageable pageable);

    Page<Localidad> findByDepartamento_IdAndEliminadoFalse(String departamentoId, Pageable pageable);

    Page<Localidad> findByNombreContainingIgnoreCaseAndDepartamento_IdAndEliminadoFalse(String nombre,
            String departamentoId, Pageable pageable);

    Page<Localidad> findByDepartamento_Provincia_IdAndEliminadoFalse(String provinciaId, Pageable pageable);

    Page<Localidad> findByNombreContainingIgnoreCaseAndDepartamento_Provincia_IdAndEliminadoFalse(String nombre,
            String provinciaId, Pageable pageable);

    Page<Localidad> findByDepartamento_Provincia_Pais_IdAndEliminadoFalse(String paisId, Pageable pageable);

    Page<Localidad> findByNombreContainingIgnoreCaseAndDepartamento_Provincia_Pais_IdAndEliminadoFalse(String nombre,
            String paisId, Pageable pageable);

    List<Localidad> findByDepartamento_IdAndEliminadoFalseOrderByNombreAsc(String departamentoId);

    Optional<Localidad> findByNombreIgnoreCaseAndDepartamento_Id(String nombre, String departamentoId);

    Optional<Localidad> findByNombreIgnoreCaseAndDepartamento_IdAndEliminadoFalse(String nombre, String departamentoId);

    List<Localidad> findByCodigoPostalContainingIgnoreCaseAndEliminadoFalse(String codigoPostal);
}