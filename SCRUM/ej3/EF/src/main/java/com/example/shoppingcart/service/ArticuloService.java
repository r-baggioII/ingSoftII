package com.example.shoppingcart.service;

import com.example.shoppingcart.Articulo;
import com.example.shoppingcart.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;

    public List<Articulo> findAll() {
        List<Articulo> articulos = articuloRepository.findAll();
        // Force loading of proveedores to avoid lazy loading issues
        articulos.forEach(articulo -> {
            if (articulo.getProveedor() != null) {
                articulo.getProveedor().getNombre(); // Access to trigger loading
            }
        });
        return articulos;
    }

    public Articulo findById(String id) {
        return articuloRepository.findById(id).orElse(null);
    }
}