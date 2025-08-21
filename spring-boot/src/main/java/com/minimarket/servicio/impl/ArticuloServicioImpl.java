package com.minimarket.servicio.impl;

import com.minimarket.modelo.Articulo;
import com.minimarket.modelo.Categoria;
import com.minimarket.repositorio.ArticuloRepositorio;
import com.minimarket.servicio.ArticuloServicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArticuloServicioImpl implements ArticuloServicio {

    private final ArticuloRepositorio articuloRepositorio;

    public ArticuloServicioImpl(ArticuloRepositorio articuloRepositorio) {
        this.articuloRepositorio = articuloRepositorio;
    }

    @Override
    public Page<Articulo> listarArticulos(Pageable pageable) {
        return articuloRepositorio.findAll(pageable);
    }

    @Override
    public Page<Articulo> buscarPorDenominacion(String texto, Pageable pageable) {
        return articuloRepositorio.findByDenominacionContainingIgnoreCase(texto, pageable);
    }

    @Override
    public Page<Articulo> buscarPorCategoria(Categoria categoria, Pageable pageable) {
        return articuloRepositorio.findByCategoria(categoria, pageable);
    }

    @Override
    public Optional<Articulo> buscarPorId(Long id) {
        return articuloRepositorio.findById(id);
    }

    @Override
    public Articulo guardarArticulo(Articulo articulo) {
        return articuloRepositorio.save(articulo);
    }

    @Override
    public void eliminarArticulo(Long id) {
        articuloRepositorio.deleteById(id);
    }

    @Override
    public void actualizarStock(Long id, int cantidad) {
        articuloRepositorio.findById(id).ifPresent(articulo -> {
            articulo.setCantidad(cantidad);
            articuloRepositorio.save(articulo);
        });
    }
}
