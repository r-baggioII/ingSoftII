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
import com.example.demoApiRest.business.domain.entity.Marca;
import com.example.demoApiRest.business.domain.entity.Producto;
import com.example.demoApiRest.business.logic.error.ErrorServiceException;
import com.example.demoApiRest.business.logic.service.CategoriaService;
import com.example.demoApiRest.business.logic.service.MarcaService;
import com.example.demoApiRest.business.logic.service.ProductoService;


@Controller
@RequestMapping("/producto")
public class ProductoController {

	@Autowired
   	private ProductoService productoService;
	
	@Autowired
   	private CategoriaService categoriaService;
	
	@Autowired
   	private MarcaService marcaService;
	
	private String viewList= "view/producto/lProducto.html";
	private String redirectList= "redirect:/producto/listProducto";
	private String viewEdit= "view/producto/eProducto.html"; 
   	
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	///////////////// VIEW: lProducto ///////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@GetMapping("/listProducto")
	public String listarProducto(Model model) {
		try {
			  
		  List<Producto> listaProducto = productoService.listarProductoActivo();
		  model.addAttribute("listaProducto", listaProducto);

		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());  
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");  
		}
		return viewList;
	}
	
	@GetMapping("/altaProducto")
	public String alta(Producto producto, Model model) {
		
		try {
			
		  List<Categoria> categorias = categoriaService.listarCategoriaActivo();	
		  List<Marca> marcas = marcaService.listarMarcaActivo();
		  
		  model.addAttribute("categorias", categorias);
		  model.addAttribute("marcas", marcas);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;
		  
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}catch(Exception e) {
		  model.addAttribute("msgError", "Error de Sistema");
		  return viewList;
		}	
	}
	
	@GetMapping("/consultar/{id}")
	public String consultar(@PathVariable("id") String idProducto, Model model) {
		
		try {
			
		  Producto producto = productoService.buscarProducto(idProducto);
		  List<Categoria> categorias = categoriaService.listarCategoriaActivo();
		  List<Marca> marcas = marcaService.listarMarcaActivo();
		  
		  model.addAttribute("categorias", categorias);
		  model.addAttribute("marcas", marcas);
		  model.addAttribute("producto", producto);
		  model.addAttribute("isDisabled", true);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/modificar/{id}")
	public String modificar(@PathVariable("id") String idProducto, Model model) {
		
		try {
			
		  Producto producto = productoService.buscarProducto(idProducto);
		  List<Categoria> categorias = categoriaService.listarCategoriaActivo();
		  List<Marca> marcas = marcaService.listarMarcaActivo();
		  
		  model.addAttribute("categorias", categorias);
		  model.addAttribute("marcas", marcas);
		  model.addAttribute("producto", producto);
		  model.addAttribute("isDisabled", false);
		  
		  return viewEdit;
		 
		}catch(ErrorServiceException e) {	
		  model.addAttribute("msgError", e.getMessage());
		  return viewList;
		}		  
	}
	
	@GetMapping("/baja/{id}")
	public String baja(@PathVariable("id") String idProducto, RedirectAttributes attributes, Model model) {	
		
		try {
			
		  productoService.eliminarProducto(idProducto);		
		  attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
		  return redirectList;
		  
		}catch(ErrorServiceException e) {	
		   model.addAttribute("msgError", e.getMessage());
		   return redirectList;
		} 
	}
	
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	////////////// VIEW: eProducto //////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	@PostMapping("/aceptarEditProducto")
	public String aceptarEdit(Producto producto, BindingResult result, RedirectAttributes attributes, Model model){
		
		try {
			
		  if (result.hasErrors()){		
			model.addAttribute("msgError", "Error de Sistema");
			return viewEdit;
		  }
		 
		  if (producto.getId() == null || producto.getId().trim().isEmpty())
		   productoService.crearProducto(producto.getCategoria().getId(), producto.getMarca().getId(), producto.getNombre(), producto.getPrecio());
		  else 
		   productoService.modificarProducto(producto.getId(), producto.getCategoria().getId(), producto.getMarca().getId(), producto.getNombre(), producto.getPrecio());
			  
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
	
	@GetMapping("/cancelarEditProducto")
	public String cancelarEdit() {
		return redirectList;
	}
	

}