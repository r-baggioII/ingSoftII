package com.example.demoApiRest.controller.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoApiRest.business.domain.entity.Producto;
import com.example.demoApiRest.business.logic.service.ProductoService;

@RestController
@RequestMapping("api/v1/producto")
public class ProductoRestController {

	@Autowired
   	private ProductoService productoService;
	
	@GetMapping(value = "/listarProducto")
    public ResponseEntity<?> listarProducto(){
	  try {
		  return ResponseEntity.status(HttpStatus.OK).body(productoService.listarProductoActivo());
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@GetMapping(value = "/{id}")
    public ResponseEntity<?> buscarProducto(@PathVariable String id){
	  try {
		  return ResponseEntity.status(HttpStatus.OK).body(productoService.buscarProducto(id));
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@PostMapping("")
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto){
	  try {
		  productoService.crearProducto(producto.getCategoria().getId(), producto.getMarca().getId(), producto.getNombre(), producto.getPrecio());
		  return ResponseEntity.status(HttpStatus.OK).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<?> modificarProducto(@PathVariable String id, @RequestBody Producto producto){
	  try {
		  productoService.modificarProducto(id, producto.getCategoria().getId(), producto.getMarca().getId(), producto.getNombre(), producto.getPrecio());
		  return ResponseEntity.status(HttpStatus.OK).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@DeleteMapping(value = "/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable String id){
	  try {
		  productoService.eliminarProducto(id);
		  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
}
