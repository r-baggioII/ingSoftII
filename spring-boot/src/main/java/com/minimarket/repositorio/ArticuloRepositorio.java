package com.minimarket.repositorio;

import com.minimarket.modelo.Articulo;
import com.minimarket.modelo.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticuloRepositorio extends JpaRepository<Articulo, Long> {
    Page<Articulo> findByDenominacionContainingIgnoreCase(String texto, Pageable pageable);
    Page<Articulo> findByCategoria(Categoria categoria, Pageable pageable);
}
