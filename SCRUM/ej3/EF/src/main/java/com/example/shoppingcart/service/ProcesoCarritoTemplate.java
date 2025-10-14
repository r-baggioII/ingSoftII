package com.example.shoppingcart.service;

import com.example.shoppingcart.Carrito;
import com.example.shoppingcart.Detalle;

public abstract class ProcesoCarritoTemplate {

    /**
     * El método plantilla. Define el esqueleto del algoritmo.
     * Es 'final' para que las subclases no puedan alterarlo.
     */
    public final void procesarCompra(Carrito carrito) {
        validarStock(carrito);
        double subtotal = calcularSubtotal(carrito);
        double descuentos = aplicarDescuentos(subtotal, carrito); // Paso variable
        double total = subtotal - descuentos;
        actualizarTotalCarrito(carrito, total);
        generarFactura(carrito); // Hook (paso opcional)
        System.out.println("Proceso de compra finalizado. Total: $" + total);
    }

    // Pasos comunes con implementación definida
    private void validarStock(Carrito carrito) {
        System.out.println("Validando stock para " + carrito.getDetalles().size() + " artículos...");
        // Lógica para verificar si hay stock de cada artículo...
    }

    private double calcularSubtotal(Carrito carrito) {
        return carrito.getDetalles().stream()
            .mapToDouble(detalle -> detalle.getArticulo().getPrecio() * detalle.getCantidad())
            .sum();
    }
    
    private void actualizarTotalCarrito(Carrito carrito, double total) {
        carrito.setTotal(total);
        // Lógica para guardar el carrito en la base de datos...
        System.out.println("Carrito actualizado con el total.");
    }

    /**
     * Paso primitivo que DEBE ser implementado por las subclases.
     * Cada tipo de proceso tendrá su propia lógica de descuentos.
     */
    protected abstract double aplicarDescuentos(double subtotal, Carrito carrito);
    
    /**
     * Hook: un paso opcional que las subclases pueden sobreescribir si lo necesitan.
     * Por defecto, no hace nada.
     */
    protected void generarFactura(Carrito carrito) {
        // Implementación vacía por defecto
    }
}