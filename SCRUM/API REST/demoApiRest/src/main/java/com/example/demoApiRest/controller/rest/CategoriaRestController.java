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

import com.example.demoApiRest.business.domain.entity.Categoria;
import com.example.demoApiRest.business.logic.service.CategoriaService;

@RestController
@RequestMapping("api/v1/categoria")
public class CategoriaRestController {

	@Autowired
   	private CategoriaService categoriaService;
	
	@GetMapping(value = "/listarCategoria")
    public ResponseEntity<?> listarCategoria(){
	  try {
		  return ResponseEntity.status(HttpStatus.OK).body(categoriaService.listarCategoriaActivo());
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@GetMapping(value = "/{id}")
    public ResponseEntity<?> buscarCategoria(@PathVariable String id){
	  try {
		  return ResponseEntity.status(HttpStatus.OK).body(categoriaService.buscarCategoria(id));
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@PostMapping("")
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria){
	  try {
		  categoriaService.crearCategoria(categoria.getNombre());
		  return ResponseEntity.status(HttpStatus.OK).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<?> modificarCategoria(@PathVariable String id, @RequestBody Categoria categoria){
	  try {
		  categoriaService.modificarCategoria(id,categoria.getNombre());
		  return ResponseEntity.status(HttpStatus.OK).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@DeleteMapping(value = "/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable String id){
	  try {
		  categoriaService.eliminarCategoria(id);
		  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
}
