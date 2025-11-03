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

import com.example.demoApiRest.business.domain.entity.Marca;
import com.example.demoApiRest.business.logic.error.ErrorServiceException;
import com.example.demoApiRest.business.logic.service.MarcaService;


@Controller
@RequestMapping("/marca")
public class MarcaController {

	@Autowired
   	private MarcaService marcaService;
	
	private String viewList= "view/marca/lMarca.html";
	private String redirectList= "redirect:/marca/listMarca";
	private String viewEdit= "view/marca/eMarca.html"; 
   	
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	///////////////// VIEW: lMarca ///////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@GetMapping("/listMarca")
	public String listarMarca(Model model) {
		try {
			  
		  List<Marca> listaMarca = marcaService.listarMarcaActivo();
		  model.addAttribute("listaMarca", listaMarca);

		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");  
		}
		return viewList;
	}
	
	@GetMapping("/altaMarca")
	public String alta(Marca marca, Model model) {
		model.addAttribute("isDisabled", false);
		return viewEdit;
	}
	
	@GetMapping("/consultar/{id}")
	public String consultar(@PathVariable("id") String idMarca, Model model) {
		
		try {
			
		  Marca marca = marcaService.buscarMarca(idMarca);		
		  model.addAttribute("marca", marca);
		  model.addAttribute("isDisabled", true);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/modificar/{id}")
	public String modificar(@PathVariable("id") String idMarca, Model model) {
		
		try {
			
		  Marca marca = marcaService.buscarMarca(idMarca);		
		  model.addAttribute("marca", marca);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/baja/{id}")
	public String baja(@PathVariable("id") String idMarca, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  marcaService.eliminarMarca(idMarca);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;
		  
		}catch(ErrorServiceException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;
		} 
	}
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	///////////////// VIEW: eMarca ///////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@PostMapping("/aceptarEditMarca")
	public String aceptarEdit(Marca marca, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;
		  }
		 
		  if (marca.getId() == null || marca.getId().trim().isEmpty())
		   marcaService.crearMarca(marca.getNombre());
		  else 
		   marcaService.modificarMarca(marca.getId(), marca.getNombre());
			  
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
	
	@GetMapping("/cancelarEditMarca")
	public String cancelarEdit() {
		return redirectList;
	}
	

}
