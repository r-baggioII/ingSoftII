package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Provincia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProvinciaRepositorio extends JpaRepository<Provincia, String> {

    Optional<Provincia> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCaseAndPaisIdAndEliminadoFalse(String nombre, String paisId);

    Optional<Provincia> findByIdAndEliminadoFalse(String id);

    List<Provincia> findByPaisId(String idPais);

    Optional<Provincia> findByNombreIgnoreCaseAndPaisId(String nombre, String idPais);

    @Query("SELECT p FROM Provincia p JOIN FETCH p.pais")
    List<Provincia> findAllWithPais();

}
