package com.example.greedy_gym.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.SocioRepositorio;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginControlador {

    private final SocioRepositorio socioRepositorio;

    public LoginControlador(SocioRepositorio socioRepositorio) {
        this.socioRepositorio = socioRepositorio;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // El POST /login ahora lo maneja Spring Security automáticamente
    // a través de SeguridadWeb y LoginSuccessHandler

    @GetMapping("/dashboard/admin")
    public String dashboardAdmin(HttpSession session, Model model) {
        if (!verificarSesion(session, RolUsuario.ADMINISTRATIVO)) {
            return "redirect:/login";
        }
        // Redirigimos al nuevo dashboard administrativo (v2)
        return "redirect:/dashboard/admin2";
    }

    @GetMapping("/dashboard/admin2")
    public String dashboardAdmin2(HttpSession session, Model model) {
        if (!verificarSesion(session, RolUsuario.ADMINISTRATIVO)) {
            return "redirect:/login";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "dashboard-admin-2";
    }

    @GetMapping("/dashboard/empleado")
    public String dashboardEmpleado(HttpSession session, Model model) {
        if (!verificarSesion(session, RolUsuario.PROFESOR)) {
            return "redirect:/login";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "dashboard-empleado";
    }

    @GetMapping("/dashboard/socio")
    public String dashboardSocio(HttpSession session, Model model) {
        if (!verificarSesion(session, RolUsuario.SOCIO)) {
            return "redirect:/login";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        socioRepositorio.findByUsuario_IdAndEliminadoFalse(usuario.getId())
                .ifPresent(socio -> model.addAttribute("socio", socio));
        Object mensajePago = session.getAttribute("mensajePago");
        if (mensajePago != null) {
            model.addAttribute("mensajePago", mensajePago);
            session.removeAttribute("mensajePago");
        }
        Object errorPago = session.getAttribute("errorPago");
        if (errorPago != null) {
            model.addAttribute("errorPago", errorPago);
            session.removeAttribute("errorPago");
        }
        Object ultimaFacturaId = session.getAttribute("ultimaFacturaId");
        if (ultimaFacturaId != null) {
            model.addAttribute("ultimaFacturaId", ultimaFacturaId);
            session.removeAttribute("ultimaFacturaId");
        }
        model.addAttribute("mercadoPagoHabilitado", true);
        model.addAttribute("mercadoPagoPublicKey", "");
        return "dashboard-socio";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private boolean verificarSesion(HttpSession session, RolUsuario rolRequerido) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null && usuario.getRol() == rolRequerido;
    }
}
