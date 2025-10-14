package com.example.shoppingcart.controller;

import com.example.shoppingcart.Articulo;
import com.example.shoppingcart.Carrito;
import com.example.shoppingcart.service.ArticuloService;
import com.example.shoppingcart.service.CarritoService;
import com.example.shoppingcart.service.ProcesoEstandar;
import com.example.shoppingcart.service.ProcesoCarritoTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CarritoController {

    @Autowired
    private ArticuloService articuloService;

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/articulos")
    public List<Articulo> obtenerArticulos() {
        // Devuelve una lista de artículos desde la base de datos
        return articuloService.findAll();
    }

    @PostMapping("/carrito/{id}/checkout")
    public ResponseEntity<String> procesarCompra(@PathVariable String id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Aquí se instancia y utiliza el Template Method
        ProcesoCarritoTemplate proceso = new ProcesoEstandar();
        proceso.procesarCompra(carrito);
        
        return ResponseEntity.ok("Compra procesada exitosamente.");
    }

    @PostMapping("/carrito")
    public Carrito crearCarrito() {
        Carrito carrito = new Carrito();
        carrito.setId(java.util.UUID.randomUUID().toString());
        carrito.setTotal(0.0);
        return carritoService.save(carrito);
    }

    @GetMapping("/carrito/{id}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable String id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/carrito/{carritoId}/articulo/{articuloId}")
    public ResponseEntity<String> agregarArticulo(@PathVariable String carritoId, @PathVariable String articuloId) {
        Carrito carrito = carritoService.findById(carritoId);
        Articulo articulo = articuloService.findById(articuloId);
        if (carrito == null || articulo == null) {
            return ResponseEntity.notFound().build();
        }
        // Create Detalle
        com.example.shoppingcart.Detalle detalle = new com.example.shoppingcart.Detalle();
        detalle.setId(java.util.UUID.randomUUID().toString());
        detalle.setCarrito(carrito);
        detalle.setArticulo(articulo);
        carrito.getDetalles().add(detalle);
        carritoService.save(carrito);
        return ResponseEntity.ok("Artículo agregado al carrito.");
    }
}