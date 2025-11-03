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

import com.example.demoApiRest.business.domain.entity.Marca;
import com.example.demoApiRest.business.logic.service.MarcaService;

@RestController
@RequestMapping("api/v1/marca")
public class MarcaRestController {

	@Autowired
   	private MarcaService marcaService;
	
	/* ResponseEntity: Es una clase de Spring que representa toda la respuesta HTTP, incluyendo el cuerpo, los headers y el código de estado. Es muy útil cuando querés tener control total sobre lo que se devuelve.
	 * .status(HttpStatus.OK): Indica que la respuesta tendrá el código de estado 200 OK, lo que significa que la solicitud fue procesada correctamente
	 * .body(...): Define el contenido (body) de la respuesta. En este caso, se está devolviendo el resultado de marcaService.listarMarcaActivo().
	 */
	
	@GetMapping(value = "/listarMarca")
    public ResponseEntity<?> listarMarca(){
	  try {
		  return ResponseEntity.status(HttpStatus.OK).body(marcaService.listarMarcaActivo());
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@GetMapping(value = "/{id}")
    public ResponseEntity<?> buscarMarca(@PathVariable String id){
	  try {
		  return ResponseEntity.status(HttpStatus.OK).body(marcaService.buscarMarca(id));
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@PostMapping("")
    public ResponseEntity<?> crearMarca(@RequestBody Marca marca){
	  try {
		  marcaService.crearMarca(marca.getNombre());
		  return ResponseEntity.status(HttpStatus.OK).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<?> modificarMarca(@PathVariable String id, @RequestBody Marca marca){
	  try {
		  marcaService.modificarMarca(id,marca.getNombre());
		  return ResponseEntity.status(HttpStatus.OK).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
	@DeleteMapping(value = "/{id}")
    public ResponseEntity<?> eliminarMarca(@PathVariable String id){
	  try {
		  marcaService.eliminarMarca(id);
		  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"exito\":\"La acción se realizó correctamente\"}");
	  }catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Eror al procesar la petición\"}");
	  }
    }
	
}