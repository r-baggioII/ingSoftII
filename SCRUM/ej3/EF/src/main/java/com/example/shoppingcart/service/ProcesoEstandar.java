package com.example.shoppingcart.service;

import com.example.shoppingcart.Carrito;

public class ProcesoEstandar extends ProcesoCarritoTemplate {
    @Override
    protected double aplicarDescuentos(double subtotal, Carrito carrito) {
        System.out.println("Aplicando descuentos estándar...");
        // Los usuarios estándar no tienen descuento.
        if (subtotal > 5000) {
            System.out.println("Descuento del 5% por superar los $5000.");
            return subtotal * 0.05;
        }
        return 0; // Sin descuento
    }
}