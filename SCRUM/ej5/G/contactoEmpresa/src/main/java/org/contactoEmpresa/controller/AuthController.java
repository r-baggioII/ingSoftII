package org.contactoEmpresa.controller;

import jakarta.validation.Valid;
import org.contactoEmpresa.entity.Usuario;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.contactoEmpresa.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de login
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String registro,  // ✅ AGREGADO
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión correctamente");
        }
        // ✅ AGREGADO: Mensaje de registro exitoso
        if (registro != null) {
            model.addAttribute("mensajeRegistro", "Registro exitoso. Ya puedes iniciar sesión");
        }
        return "auth/login";
    }

    /**
     * Muestra el formulario de registro
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        Usuario usuario = new Usuario();
        usuario.setRol(Usuario.Rol.MECANICO);  // ✅ Asignar rol por defecto
        model.addAttribute("usuario", usuario);
        return "auth/registro";
    }

    /**
     * Procesa el registro de un nuevo usuario
     */
    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                   BindingResult result,
                                   Model model) {
        // Si hay errores de validación
        if (result.hasErrors()) {
            return "auth/registro";
        }

        try {
            // Asegurar que el rol sea MECANICO
            usuario.setRol(Usuario.Rol.MECANICO);
            usuarioService.registrarUsuario(usuario);
            return "redirect:/login?registro=exitoso";
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);  // 🔥 IMPORTANTE: Devolver el objeto
            return "auth/registro";
        }
    }

    /**
     * Dashboard principal después del login
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "dashboard";
    }

    /**
     * Página de acceso denegado
     */
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";
    }
}