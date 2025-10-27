package com.ejemplo.biblioteca.repository;

import com.ejemplo.biblioteca.domain.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("""
            SELECT l FROM Libro l
            WHERE (:autorId IS NULL OR l.autor.id = :autorId)
              AND (:personaId IS NULL OR l.persona.id = :personaId)
              AND (:genero IS NULL OR LOWER(l.genero) LIKE LOWER(CONCAT('%', :genero, '%')))
            """)
    Page<Libro> search(
            @Param("autorId") Long autorId,
            @Param("personaId") Long personaId,
            @Param("genero") String genero,
            Pageable pageable);
}
