package org.example.controller;

import org.example.entity.Medico;
import org.example.service.MedicoService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/medico")
public class MedicoController extends BaseController<Medico, String> {

    public MedicoController(MedicoService service, ApplicationContext context) {
        super(service, context);
        // Inicializar configuración específica del controlador
        Medico medico = new Medico();
        initController(medico, "Gestión de Médicos", "Formulario de Médico");
    }
}
