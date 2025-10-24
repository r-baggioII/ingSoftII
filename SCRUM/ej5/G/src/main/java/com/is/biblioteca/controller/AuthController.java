package com.is.biblioteca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador de Autenticación - Maneja el login, registro y cierre de sesión
 * 
 * RESPONSABILIDADES:
 * 1. Mostrar la página de login
 * 2. Manejar el registro de nuevos usuarios
 * 3. Procesar el inicio de sesión exitoso y cargar datos del usuario
 * 4. Manejar el cierre de sesión
 */
@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * ENDPOINT 1: Página principal (redirecciona al login si no está autenticado)
     */
    @GetMapping("/")
    public String index() {
        // Si el usuario está autenticado, lo redirige a /inicio
        // Si no, Spring Security automáticamente lo redirige al login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/inicio";
        }
        return "redirect:/login";
    }

    /**
     * ENDPOINT 2: Mostrar formulario de login
     * 
     * Spring Security maneja automáticamente la autenticación cuando se envía el formulario a /logincheck
     * Este método solo muestra la página con los mensajes correspondientes
     */
    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            ModelMap modelo) {

        // Mensaje de error si las credenciales son incorrectas
        if (error != null) {
            modelo.put("error", "Usuario o contraseña incorrectos. Por favor, intente nuevamente.");
        }

        // Mensaje de confirmación si el usuario cerró sesión
        if (logout != null) {
            modelo.put("exito", "Ha cerrado sesión exitosamente.");
        }

        return "login";
    }

    /**
     * ENDPOINT 3: Página de inicio después del login exitoso
     * 
     * Este método se ejecuta DESPUÉS de que Spring Security valida las credenciales
     * Aquí cargamos los datos completos del usuario en la sesión
     */
    @GetMapping("/inicio")
    public String inicio(HttpSession session, ModelMap modelo) {
        try {
            // Obtener el usuario autenticado por Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String email = auth.getName(); // Spring Security guarda el email como "username"
                
                // Buscar el usuario completo en la base de datos
                Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
                
                // Guardarlo en la sesión HTTP para usarlo en las vistas
                session.setAttribute("usuariosession", usuario);
                
                modelo.put("usuario", usuario);
                
                // Si es administrador, redirigir al panel de administración
                if (usuario != null && usuario.getRol().toString().equals("ADMIN")) {
                    return "redirect:/admin/dashboard";
                }
            }
            
            return "inicio";
            
        } catch (Exception e) {
            e.printStackTrace();
            modelo.put("error", "Error al cargar la página de inicio");
            return "redirect:/login?error=true";
        }
    }

    /**
     * ENDPOINT 4: Mostrar formulario de registro
     */
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }
    
    /**
     * Alias para mantener compatibilidad
     */
    @GetMapping("/registrar")
    public String mostrarRegistrar() {
        return "registro";
    }

    /**
     * ENDPOINT 5: Procesar el registro de un nuevo usuario
     * 
     * El PasswordEncoder (BCrypt) se encarga automáticamente de encriptar la contraseña
     * antes de guardarla en la base de datos
     */
    @PostMapping("/registrar")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            @RequestParam(required = false) MultipartFile archivo,
            ModelMap modelo,
            RedirectAttributes redirectAttrs) {

        try {
            // El servicio valida los datos y crea el usuario
            // La contraseña se encripta automáticamente con BCrypt
            usuarioService.crearUsuario(nombre, email, password, password2, archivo);

            // Mensaje de éxito usando flash attributes (se muestra una sola vez)
            redirectAttrs.addFlashAttribute("exito", "¡Usuario registrado correctamente! Ahora puede iniciar sesión.");
            return "redirect:/login";

        } catch (ErrorServiceException ex) {
            // Si hay error, volver al formulario con el mensaje y los datos ingresados
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("email", email);
            return "registro";
        }
    }

    /**
     * ENDPOINT 6: Cerrar sesión
     * 
     * Spring Security maneja automáticamente el logout en /logout
     * Este método adicional permite limpiar la sesión manualmente si es necesario
     */
    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Invalida la sesión
        return "redirect:/logout"; // Redirige al logout de Spring Security
    }
}
