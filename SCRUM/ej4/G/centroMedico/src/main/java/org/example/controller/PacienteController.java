package org.example.controller;

import org.example.entity.Paciente;
import org.example.service.PacienteService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/paciente")
public class PacienteController extends BaseController<Paciente, String> {

    public PacienteController(PacienteService service, ApplicationContext context) {
        super(service, context);
        // Inicializar configuración específica del controlador
        Paciente paciente = new Paciente();
        initController(paciente, "Gestión de Pacientes", "Formulario de Paciente");
    }
}
