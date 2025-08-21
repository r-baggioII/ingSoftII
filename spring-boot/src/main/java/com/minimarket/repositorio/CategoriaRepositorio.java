package com.minimarket.repositorio;

import com.minimarket.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByDenominacion(String denominacion);
    List<Categoria> findByDenominacionContainingIgnoreCase(String texto);
}
