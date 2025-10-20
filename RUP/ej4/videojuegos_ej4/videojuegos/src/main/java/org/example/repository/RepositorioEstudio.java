package org.example.repository;

import org.example.entity.Categoria;
import org.example.entity.Estudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RepositorioEstudio  extends JpaRepository<Estudio, Long> {
}
