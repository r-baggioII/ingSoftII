package com.example.demoApiRestConsumer.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demoApiRestConsumer.business.domain.dto.CategoriaDTO;
import com.example.demoApiRestConsumer.business.logic.error.ErrorServiceException;
import com.example.demoApiRestConsumer.business.logic.service.CategoriaService;


@Controller
@RequestMapping("/categoria")
public class CategoriaController {

	@Autowired
   	private CategoriaService categoriaService;
	
	private String viewList= "view/categoria/lCategoria.html";
	private String redirectList= "redirect:/categoria/listCategoria";
	private String viewEdit= "view/categoria/eCategoria.html"; 
   	
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	///////////// VIEW: lCategoria //////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@GetMapping("/listCategoria")
	public String listarCategoria(Model model) {
		try {
			  
		  List<CategoriaDTO> listaCategoria = categoriaService.listarCategoriaActivo();
		  model.addAttribute("listaCategoria", listaCategoria);

		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");  
		}
		return viewList;
	}
	
	@GetMapping("/altaCategoria")
	public String alta(CategoriaDTO categoria, Model model) {
		
		categoria = new CategoriaDTO();
		categoria.setId("");
		categoria.setNombre("");
		categoria.setEliminado(false);
		
		model.addAttribute("categoria", categoria);
		model.addAttribute("isDisabled", false);
		
		return viewEdit;
	}
	
	@GetMapping("/consultar")
	public String consultar(@RequestParam(value="id") String idCategoria, Model model) {
		
		try {
			
		  CategoriaDTO categoria = categoriaService.buscarCategoria(idCategoria);		
		  model.addAttribute("categoria", categoria);
		  model.addAttribute("isDisabled", true);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/modificar")
	public String modificar(@RequestParam(value="id") String idCategoria, Model model) {
		
		try {
			
		  CategoriaDTO categoria = categoriaService.buscarCategoria(idCategoria);		
		  model.addAttribute("categoria", categoria);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/baja")
	public String baja(@RequestParam(value="id") String idCategoria, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  categoriaService.eliminarCategoria(idCategoria);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;
		  
		}catch(ErrorServiceException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;
		} 
	}
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	///////////// VIEW: eCategoria //////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@PostMapping("/aceptarEditCategoria")
	public String aceptarEdit(@RequestParam(value="id") String idCategoria, @RequestParam(value="nombre") String nombreCategoria, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (idCategoria == null || idCategoria.trim().isEmpty())
		    categoriaService.crearCategoria(nombreCategoria);
		  else 
		    categoriaService.modificarCategoria(idCategoria, nombreCategoria);
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;
		  
		}catch(ErrorServiceException e) {
			  return error (e.getMessage(), model, idCategoria, nombreCategoria);
		}catch(Exception e) {
			  return error ("Error de Sistema", model, idCategoria, nombreCategoria);
		}
		
	}

	private String error (String mensaje, Model model, String id, String nombre) {
		try {
			
			model.addAttribute("msgError", mensaje);
			if (id != null && !id.trim().isEmpty()) {
			 model.addAttribute("categoria", categoriaService.buscarCategoria(id));
			}else {
			  CategoriaDTO categoria = new CategoriaDTO();
			  categoria.setId("");
			  categoria.setNombre(nombre);
			  model.addAttribute("categoria",categoria);	
			}
			
		}catch(Exception e) {}
		return viewEdit;
	}
	
	@GetMapping("/cancelarEditNacionalidad")
	public String cancelarEdit() {
		return redirectList;
	}
	

}

