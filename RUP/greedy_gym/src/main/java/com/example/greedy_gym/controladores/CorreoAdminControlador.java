package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.servicios.CorreoMasivoSocioServicio;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/correos")
public class CorreoAdminControlador {

    private final CorreoMasivoSocioServicio correoMasivoSocioServicio;
    public CorreoAdminControlador(CorreoMasivoSocioServicio correoMasivoSocioServicio) {
        this.correoMasivoSocioServicio = correoMasivoSocioServicio;
    }

    @PostMapping("/socios/saludo")
    public ResponseEntity<Map<String, Object>> enviarSaludoGeneral(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != RolUsuario.ADMINISTRATIVO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "mensaje", "Debes iniciar sesión como administrador para enviar correos"));
        }

        int enviados = correoMasivoSocioServicio.enviarSaludoGeneral();
        return ResponseEntity.ok(Map.of(
                "mensaje", "Saludos enviados", "enviados", enviados));
    }

}
