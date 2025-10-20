package org.example.repository;

import org.example.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RepositorioCategoria extends JpaRepository<Categoria, Long> {
}
