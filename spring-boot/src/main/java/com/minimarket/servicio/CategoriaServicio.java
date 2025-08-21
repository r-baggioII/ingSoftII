package com.minimarket.servicio;

import com.minimarket.modelo.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaServicio {
    List<Categoria> listarCategorias();
    Optional<Categoria> buscarPorId(Long id);
    Categoria guardarCategoria(Categoria categoria);
    void eliminarCategoria(Long id);
    List<Categoria> buscarPorDenominacion(String texto);
}
