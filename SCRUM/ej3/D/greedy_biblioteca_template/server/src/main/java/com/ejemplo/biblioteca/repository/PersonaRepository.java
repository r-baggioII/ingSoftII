package com.ejemplo.biblioteca.repository;

import com.ejemplo.biblioteca.domain.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @Query("""
            SELECT p FROM Persona p
            WHERE (:apellido IS NULL OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :apellido, '%')))
              AND (:dni IS NULL OR p.dni = :dni)
            """)
    Page<Persona> search(
            @Param("apellido") String apellido,
            @Param("dni") Integer dni,
            Pageable pageable);

    Optional<Persona> findByDni(Integer dni);
}
