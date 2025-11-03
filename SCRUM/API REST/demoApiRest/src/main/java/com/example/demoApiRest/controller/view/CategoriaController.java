package com.example.demoApiRest.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demoApiRest.business.domain.entity.Categoria;
import com.example.demoApiRest.business.logic.error.ErrorServiceException;
import com.example.demoApiRest.business.logic.service.CategoriaService;


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
	///////////////// VIEW: lCategoria ///////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@GetMapping("/listCategoria")
	public String listarCategoria(Model model) {
		try {
			  
		  List<Categoria> listaCategoria = categoriaService.listarCategoriaActivo();
		  model.addAttribute("listaCategoria", listaCategoria);

		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");  
		}
		return viewList;
	}
	
	@GetMapping("/altaCategoria")
	public String alta(Categoria categoria, Model model) {
		model.addAttribute("isDisabled", false);
		return viewEdit;
	}
	
	@GetMapping("/consultar/{id}")
	public String consultar(@PathVariable("id") String idCategoria, Model model) {
		
		try {
			
		  Categoria categoria = categoriaService.buscarCategoria(idCategoria);		
		  model.addAttribute("categoria", categoria);
		  model.addAttribute("isDisabled", true);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/modificar/{id}")
	public String modificar(@PathVariable("id") String idCategoria, Model model) {
		
		try {
			
		  Categoria categoria = categoriaService.buscarCategoria(idCategoria);		
		  model.addAttribute("categoria", categoria);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/baja/{id}")
	public String baja(@PathVariable("id") String idCategoria, RedirectAttributes attributes, Model model) {	
		
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
	///////////////// VIEW: eCategoria ///////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@PostMapping("/aceptarEditCategoria")
	public String aceptarEdit(Categoria categoria, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;
		  }
		 
		  if (categoria.getId() == null || categoria.getId().trim().isEmpty())
		   categoriaService.crearCategoria(categoria.getNombre());
		  else 
		   categoriaService.modificarCategoria(categoria.getId(), categoria.getNombre());
			  
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;
		  
		}catch(ErrorServiceException e) {	
			  model.addAttribute("msgError", e.getMessage());
			  return viewEdit;
		}catch(Exception e) {
			  model.addAttribute("msgError", "Error de Sistema");
			  return viewEdit;
		}
		
	}
	
	@GetMapping("/cancelarEditCategoria")
	public String cancelarEdit() {
		return redirectList;
	}
	

}

