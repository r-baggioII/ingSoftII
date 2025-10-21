package com.ejemplo.biblioteca.repository;

import com.ejemplo.biblioteca.domain.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    boolean existsByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);

    Optional<Autor> findByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);
}
