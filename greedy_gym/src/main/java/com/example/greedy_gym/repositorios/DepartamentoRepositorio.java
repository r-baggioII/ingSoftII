package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Departamento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepositorio extends JpaRepository<Departamento, String> {

    Optional<Departamento> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Departamento> findByIdAndEliminadoFalse(String id);

    List<Departamento> findByProvinciaId(String idProvincia);

}
