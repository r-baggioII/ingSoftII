package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.EstadoCuota;
import com.example.greedy_gym.entidades.Mes;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuotaMensualRepositorio extends JpaRepository<CuotaMensual, String> {

    Collection<CuotaMensual> findByEliminadoFalse();

    Optional<CuotaMensual> findByIdAndEliminadoFalse(String id);

    boolean existsByIdSocioAndMesAndAnioAndEliminadoFalse(String idSocio, Mes mes, Long anio);

    boolean existsByIdSocioAndMesAndAnioAndEliminadoFalseAndIdNot(String idSocio, Mes mes, Long anio, String id);

    Collection<CuotaMensual> findByEstadoAndEliminadoFalse(EstadoCuota estado);
}

