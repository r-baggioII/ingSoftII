package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.UsuarioDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.Rol;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador MVC para gestionar usuarios.
 * Todas las operaciones están protegidas por AuthCheckInterceptor.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos los usuarios
     */
    @GetMapping
    public String listar(Model model) {
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarActivos();
            model.addAttribute("usuarios", usuarios);
            return "usuarios";
        } catch (ErrorServiceException e) {
            log.error("Error al listar usuarios", e);
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            return "usuarios";
        }
    }

    /**
     * Muestra el formulario para crear un nuevo usuario
     */
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("esNuevo", true);
        return "usuario-form";
    }

    /**
     * Procesa la creación de un nuevo usuario
     */
    @PostMapping("/nuevo")
    public String crear(@ModelAttribute UsuarioDTO usuario, RedirectAttributes ra) {
        try {
            usuarioService.alta(usuario);
            ra.addFlashAttribute("success", "Usuario creado exitosamente");
            return "redirect:/usuarios";
        } catch (ErrorServiceException e) {
            log.error("Error al crear usuario", e);
            ra.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
            return "redirect:/usuarios/nuevo";
        }
    }

    /**
     * Muestra el formulario para editar un usuario existente
     */
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable String id, Model model, RedirectAttributes ra) {
        try {
            Optional<UsuarioDTO> usuario = usuarioService.obtener(id);
            if (usuario.isPresent()) {
                model.addAttribute("usuario", usuario.get());
                model.addAttribute("roles", Rol.values());
                model.addAttribute("esNuevo", false);
                return "usuario-form";
            } else {
                ra.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/usuarios";
            }
        } catch (ErrorServiceException e) {
            log.error("Error al obtener usuario", e);
            ra.addFlashAttribute("error", "Error al cargar usuario: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    /**
     * Procesa la actualización de un usuario
     */
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable String id, @ModelAttribute UsuarioDTO usuario, RedirectAttributes ra) {
        try {
            usuarioService.modificar(id, usuario);
            ra.addFlashAttribute("success", "Usuario actualizado exitosamente");
            return "redirect:/usuarios";
        } catch (ErrorServiceException e) {
            log.error("Error al actualizar usuario", e);
            ra.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
            return "redirect:/usuarios/editar/" + id;
        }
    }

    /**
     * Elimina un usuario
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes ra) {
        try {
            usuarioService.baja(id);
            ra.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (ErrorServiceException e) {
            log.error("Error al eliminar usuario", e);
            ra.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    /**
     * Muestra los detalles de un usuario
     */
    @GetMapping("/{id}")
    public String ver(@PathVariable String id, Model model, RedirectAttributes ra) {
        try {
            Optional<UsuarioDTO> usuario = usuarioService.obtener(id);
            if (usuario.isPresent()) {
                model.addAttribute("usuario", usuario.get());
                return "usuario-detalle";
            } else {
                ra.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/usuarios";
            }
        } catch (ErrorServiceException e) {
            log.error("Error al obtener usuario", e);
            ra.addFlashAttribute("error", "Error al cargar usuario: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }
}
