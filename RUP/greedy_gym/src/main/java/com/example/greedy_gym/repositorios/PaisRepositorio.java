package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Pais;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaisRepositorio extends JpaRepository<Pais, String> {

    Optional<Pais> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCaseAndEliminadoFalse(String nombre);

    Optional<Pais> findByIdAndEliminadoFalse(String id);
}
