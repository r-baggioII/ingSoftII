package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Localidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocalidadRepositorio extends JpaRepository<Localidad, String> {

    Optional<Localidad> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCaseAndDepartamentoIdAndEliminadoFalse(String nombre, String departamentoId);

    Optional<Localidad> findByIdAndEliminadoFalse(String id);

    List<Localidad> findByDepartamentoId(String idDepartamento);

    Optional<Localidad> findByCodigoPostal(String codigoPostal);

    Optional<Localidad> findByNombreIgnoreCaseAndDepartamentoId(String nombre, String idDepartamento);

    @Query("SELECT l FROM Localidad l JOIN FETCH l.departamento d JOIN FETCH d.provincia p JOIN FETCH p.pais")
    List<Localidad> findAllWithFullHierarchy();

}
