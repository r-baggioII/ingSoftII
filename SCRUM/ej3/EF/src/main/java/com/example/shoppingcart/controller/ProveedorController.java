package com.example.shoppingcart.controller;

import com.example.shoppingcart.Proveedor;
import com.example.shoppingcart.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping("/proveedores")
    public List<Proveedor> obtenerProveedores() {
        return proveedorRepository.findAll();
    }
}