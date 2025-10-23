package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminVistaControlador {

    private boolean verificarSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null && usuario.getRol() == RolUsuario.ADMINISTRATIVO;
    }

    private String withUser(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }
        return null;
    }

    @GetMapping("/admin/usuarios")
    public String adminUsuarios(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/usuarios";
    }

    @GetMapping("/admin/socios")
    public String adminSocios(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/empleados")
    public String adminEmpleados(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/empleados";
    }

    @GetMapping("/admin/empresas")
    public String adminEmpresas(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/empresas";
    }

    @GetMapping("/admin/sucursales")
    public String adminSucursales(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/sucursales";
    }

    @GetMapping("/admin/direcciones")
    public String adminDirecciones(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/direcciones";
    }

    @GetMapping("/admin/cuotas")
    public String adminCuotas(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/cuotas";
    }

    @GetMapping("/admin/valor-cuotas")
    public String adminValorCuotas(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/valor-cuotas";
    }

    @GetMapping("/admin/mensajes")
    public String adminMensajes(HttpSession session, Model model) {
        if (!verificarSesion(session)) return "redirect:/login";
        withUser(session, model);
        return "admin/mensajes";
    }
}
