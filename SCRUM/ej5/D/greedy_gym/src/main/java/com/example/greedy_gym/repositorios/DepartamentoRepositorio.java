package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Departamento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartamentoRepositorio extends JpaRepository<Departamento, String> {

    Optional<Departamento> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCaseAndProvinciaIdAndEliminadoFalse(String nombre, String provinciaId);

    Optional<Departamento> findByIdAndEliminadoFalse(String id);

    List<Departamento> findByProvinciaId(String idProvincia);

    Optional<Departamento> findByNombreIgnoreCaseAndProvinciaId(String nombre, String idProvincia);

    @Query("SELECT d FROM Departamento d JOIN FETCH d.provincia p JOIN FETCH p.pais")
    List<Departamento> findAllWithProvinciasAndPaises();

}
