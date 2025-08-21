package com.minimarket.servicio;

import com.minimarket.modelo.Articulo;
import com.minimarket.modelo.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ArticuloServicio {

    Page<Articulo> listarArticulos(Pageable pageable);
    Page<Articulo> buscarPorDenominacion(String texto, Pageable pageable);
    Page<Articulo> buscarPorCategoria(Categoria categoria, Pageable pageable);
    Optional<Articulo> buscarPorId(Long id);
    Articulo guardarArticulo(Articulo articulo);
    void eliminarArticulo(Long id);
    void actualizarStock(Long id, int cantidad);
}
