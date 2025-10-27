package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Empresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepositorio extends JpaRepository<Empresa, String> {

    Optional<Empresa> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Empresa> findByIdAndEliminadoFalse(String id);
}
