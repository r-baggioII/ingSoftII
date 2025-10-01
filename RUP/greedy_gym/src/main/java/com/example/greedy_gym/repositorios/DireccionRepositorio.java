package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Direccion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DireccionRepositorio extends JpaRepository<Direccion, String> {

    Optional<Direccion> findByCalleAndNumeroAndEliminadoFalse(String calle, String numero);
    
    Optional<Direccion> findByIdAndEliminadoFalse(String id);
    
    List<Direccion> findByEliminadoFalse();
    
    boolean existsByCalleAndNumeroAndEliminadoFalse(String calle, String numero);
}
