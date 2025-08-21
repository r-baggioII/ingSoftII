package com.minimarket.servicio.impl;

import com.minimarket.modelo.Categoria;
import com.minimarket.repositorio.CategoriaRepositorio;
import com.minimarket.servicio.CategoriaServicio;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServicioImpl implements CategoriaServicio {

    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaServicioImpl(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    @Override
    public List<Categoria> listarCategorias() {
        return categoriaRepositorio.findAll();
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepositorio.findById(id);
    }

    @Override
    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepositorio.save(categoria);
    }

    @Override
    public void eliminarCategoria(Long id) {
        categoriaRepositorio.deleteById(id);
    }

    @Override
    public List<Categoria> buscarPorDenominacion(String texto) {
        return categoriaRepositorio.findByDenominacionContainingIgnoreCase(texto);
    }
}
