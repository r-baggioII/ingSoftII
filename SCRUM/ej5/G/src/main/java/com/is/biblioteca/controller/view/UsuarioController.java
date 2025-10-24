package com.is.biblioteca.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	//////////////////////////////////////////
	//////////////////////////////////////////
	////////////// VIEW: LOGIN ///////////////
	//////////////////////////////////////////
	//////////////////////////////////////////

	@GetMapping("/login")
	public String login(@RequestParam(required = false) String error, ModelMap modelo) {

		if (error != null) {
			modelo.put("error", "Usuario o Contraseña invalidos!");
		}

		return "login.html";
	}

	// Este método se ejecuta DESPUÉS del login exitoso de Spring Security
	@GetMapping("/inicio")
	public String inicio(HttpSession session, ModelMap modelo) {
		try {
			// Obtener el email del usuario autenticado por Spring Security
			org.springframework.security.core.Authentication auth = 
				org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
			
			if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
				String email = auth.getName(); // Spring Security usa el email como username
				
				// Buscar el usuario completo en la base de datos
				Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
				
				// Guardarlo en sesión para que las vistas puedan accederlo
				session.setAttribute("usuariosession", usuario);
				
				// Verificar si es admin para redirigir
				if (usuario != null && usuario.getRol().toString().equals("ADMIN")) {
					return "redirect:/admin/dashboard";
				}
			}
			
			return "inicio.html";
		} catch (Exception e) {
			e.printStackTrace();
			modelo.put("error", "Error al cargar la página de inicio");
			return "redirect:/usuario/login?error=true";
		}
	}

	@GetMapping("/logout")
	public String salir(HttpSession session) {
		session.setAttribute("usuariosession", null);
		return "redirect:/usuario/login?logout=true";
	}

	//////////////////////////////////////////
	//////////////////////////////////////////
	//////////// VIEW: CREAR USUARIO /////////
	//////////////////////////////////////////
	//////////////////////////////////////////

	@GetMapping("/registrar")
	public String irEditAlta() {
		return "registro.html";
	}

    @PostMapping("/registro")
    public String aceptarEditAlta(@RequestParam String nombre, @RequestParam String email,
	    @RequestParam String password, String password2, ModelMap modelo, MultipartFile archivo,
	    org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {

		try {

			usuarioService.crearUsuario(nombre, email, password, password2, archivo);

			// Mostrar confirmación en la pantalla de login usando flash attributes
			redirectAttrs.addFlashAttribute("exito", "Usuario registrado correctamente!");
			return "redirect:/usuario/login";

		} catch (ErrorServiceException ex) {

			modelo.put("error", ex.getMessage());
			modelo.put("nombre", nombre);
			modelo.put("email", email);

			return "registro.html";
		}

	}

	//////////////////////////////////////////
	//////////////////////////////////////////
	///////// VIEW: MODIFICAR USUARIO //////// 
	//////////////////////////////////////////
	//////////////////////////////////////////

	@GetMapping("/perfil")
	public String irEditModificar(ModelMap modelo, HttpSession session) {

		Usuario usuario = (Usuario) session.getAttribute("usuariosession");
		modelo.put("usuario", usuario);

		return "usuario_modificar.html";
	}
	
	@GetMapping("/perfil/{id}")
	public String irEditModificar(ModelMap modelo, @PathVariable String id) {

	  try {	
		  
		Usuario usuario = usuarioService.buscarUsuario(id);
		modelo.put("usuario", usuario);
		
		return "usuario_modificar.html";
		
	  } catch (ErrorServiceException e) {
          modelo.put("error", e.getMessage());
          return "usuario_list";
      } catch (Exception e) {
          modelo.put("error", "Error de Sistemas");
          return "usuario_list";
      }	
	}

	@PostMapping("/perfil/{id}")
	public String irEditModificar(MultipartFile archivo, @PathVariable String id, @RequestParam String nombre,
			@RequestParam String email, @RequestParam String password, @RequestParam String password2,
			ModelMap modelo) {

		try {

			usuarioService.modificarUsuario(id, nombre, email, password, password2, archivo);

			modelo.put("exito", "Usuario actualizado correctamente!");

			return "redirect:/admin/dashboard";

		} catch (ErrorServiceException ex) {

			modelo.put("error", ex.getMessage());
			modelo.put("nombre", nombre);
			modelo.put("email", email);

			return "usuario_modificar.html";
		}

	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/panel")
	public String irPanel(HttpSession session) {
		Usuario logueado = (Usuario) session.getAttribute("usuariosession");
		
		if (logueado != null && logueado.getRol().toString().equals("ADMIN")) {
			return "redirect:/admin/dashboard";
		} else {
			return "inicio.html";
		}
	}

}
