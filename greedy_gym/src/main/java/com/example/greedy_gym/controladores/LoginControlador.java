package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.servicios.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginControlador {

    private final UsuarioServicio usuarioServicio;

    public LoginControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String nombreUsuario,
                               @RequestParam String clave,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioServicio.login(nombreUsuario, clave);
            session.setAttribute("usuario", usuario);
            session.setAttribute("nombreUsuario", usuario.getNombreUsuario());
            session.setAttribute("rol", usuario.getRol().toString());

            // Redirigir según el rol del usuario
            return switch (usuario.getRol()) {
                case ADMINISTRATIVO -> "redirect:/dashboard/admin";
                case PROFESOR -> "redirect:/dashboard/empleado";
                case SOCIO -> "redirect:/dashboard/socio";
            };
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard/admin")
    public String dashboardAdmin(HttpSession session, Model model) {
        if (!verificarSesion(session, RolUsuario.ADMINISTRATIVO)) {
            return "redirect:/login";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "dashboard-admin";
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
