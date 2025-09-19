package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Provincia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinciaRepositorio extends JpaRepository<Provincia, String> {

    Optional<Provincia> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Provincia> findByIdAndEliminadoFalse(String id);

    List<Provincia> findByPaisId(String idPais);

}
