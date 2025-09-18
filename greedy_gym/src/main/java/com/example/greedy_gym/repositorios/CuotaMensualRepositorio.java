package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.EstadoCuota;
import com.example.greedy_gym.entidades.Mes;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuotaMensualRepositorio extends JpaRepository<CuotaMensual, String> {

    Page<CuotaMensual> findByEliminadoFalse(Pageable pageable);

    Optional<CuotaMensual> findByIdAndEliminadoFalse(String id);

    boolean existsByIdSocioAndMesAndAnioAndEliminadoFalse(String idSocio, Mes mes, Long anio);

    Page<CuotaMensual> findByEstadoAndEliminadoFalse(EstadoCuota estado, Pageable pageable);
}

