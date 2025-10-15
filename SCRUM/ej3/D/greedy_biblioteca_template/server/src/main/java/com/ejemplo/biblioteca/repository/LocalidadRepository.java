package com.ejemplo.biblioteca.repository;

import com.ejemplo.biblioteca.domain.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalidadRepository extends JpaRepository<Localidad, Long> {

    boolean existsByDenominacionIgnoreCase(String denominacion);

    Optional<Localidad> findByDenominacionIgnoreCase(String denominacion);
}
