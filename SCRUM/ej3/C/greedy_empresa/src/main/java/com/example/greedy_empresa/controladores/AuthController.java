package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Usuario;
import com.example.greedy_empresa.entidades.enums.UsuarioRol;
import com.example.greedy_empresa.repositorios.UsuarioRepository;
import com.example.greedy_empresa.servicios.PasswordService;
import com.example.greedy_empresa.servicios.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    public static final String SESSION_USER = "authUser";

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;
    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        if (session.getAttribute(SESSION_USER) != null) {
            return "redirect:/";
        }
        model.addAttribute("activeMenu", null);
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(String username, String password, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameIgnoreCaseAndEliminadoFalse(username);
        if (usuarioOpt.isEmpty() || !passwordService.matches(password, usuarioOpt.get().getPasswordHash())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Credenciales inválidas");
            return "redirect:/login";
        }
        session.setAttribute(SESSION_USER, usuarioOpt.get());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerForm(HttpSession session, Model model) {
        if (session.getAttribute(SESSION_USER) != null) {
            return "redirect:/";
        }
        Usuario usuario = new Usuario();
        usuario.setRol(UsuarioRol.USER);
        model.addAttribute("usuario", usuario);
        model.addAttribute("activeMenu", null);
        return "usuarios/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", null);
            return "usuarios/register";
        }
        usuario.setRol(UsuarioRol.USER);
        try {
            usuarioService.guardar(usuario);
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", null);
            return "usuarios/register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Usuario registrado correctamente. Iniciá sesión.");
        return "redirect:/login";
    }
}
