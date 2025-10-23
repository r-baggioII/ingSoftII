package org.example.controller;

import org.example.entity.Usuario;
import org.example.service.UsuarioService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioController extends BaseController<Usuario, String> {

    public UsuarioController(UsuarioService service, ApplicationContext context) {
        super(service, context);
        // Inicializar configuración específica del controlador
        Usuario usuario = new Usuario();
        initController(usuario, "Gestión de Usuarios", "Formulario de Usuario");
    }
}
