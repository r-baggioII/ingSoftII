package com.example.shoppingcart.service;

import com.example.shoppingcart.Carrito;
import com.example.shoppingcart.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    public Carrito findById(String id) {
        return carritoRepository.findById(id).orElse(null);
    }

    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }
}