package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Localidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalidadRepositorio extends JpaRepository<Localidad, String> {

    Optional<Localidad> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Localidad> findByIdAndEliminadoFalse(String id);

    List<Localidad> findByDepartamentoId(String idDepartamento);

    Optional<Localidad> findByCodigoPostal(String codigoPostal);

}
