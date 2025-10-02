package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Usuario;
import com.example.greedy_empresa.entidades.UsuarioPersona;
import com.example.greedy_empresa.entidades.enums.UsuarioRol;
import com.example.greedy_empresa.servicios.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @ModelAttribute("roles")
    public UsuarioRol[] roles() {
        return UsuarioRol.values();
    }

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
            @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", usuarioService.buscar(filtro, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("activeMenu", "usuarios");
        return "usuarios/list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        Usuario usuario = new Usuario();
        usuario.setRol(UsuarioRol.USER);
        usuario.setPersona(new UsuarioPersona()); // Inicializar persona
        model.addAttribute("usuario", usuario);
        model.addAttribute("activeMenu", "usuarios");
        return "usuarios/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Asegurar que persona esté inicializada
        if (usuario.getPersona() == null) {
            usuario.setPersona(new UsuarioPersona());
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "usuarios");
            return "usuarios/form";
        }
        try {
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario guardado correctamente");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "usuarios");
            return "usuarios/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        
        // Asegurar que persona esté inicializada correctamente
        if (usuario.getPersona() == null) {
            usuario.setPersona(new UsuarioPersona());
        }
        
        usuario.setPassword("");
        usuario.setConfirmPassword("");
        model.addAttribute("usuario", usuario);
        model.addAttribute("activeMenu", "usuarios");
        return "usuarios/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Asegurar que persona esté inicializada
        if (usuario.getPersona() == null) {
            usuario.setPersona(new UsuarioPersona());
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "usuarios");
            return "usuarios/form";
        }
        try {
            usuario.setId(id);
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "usuarios");
            return "usuarios/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        usuarioService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente");
        return "redirect:/usuarios";
    }
}
