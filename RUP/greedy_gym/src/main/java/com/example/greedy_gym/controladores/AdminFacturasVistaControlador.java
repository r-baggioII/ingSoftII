package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminFacturasVistaControlador {

    private boolean verificarSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null && usuario.getRol() == RolUsuario.ADMINISTRATIVO;
    }

    private void withUser(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }
    }

    @GetMapping("/admin/facturas")
    public String adminFacturas(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/facturas";
    }
}
