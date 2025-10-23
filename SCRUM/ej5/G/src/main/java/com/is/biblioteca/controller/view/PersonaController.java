package com.is.biblioteca.controller.view;

import com.is.biblioteca.business.domain.entity.Persona;
import com.is.biblioteca.business.logic.service.PersonaService;
import com.is.biblioteca.controller.BaseController;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/persona")
public class PersonaController extends BaseController<Persona, String> {
    
    public PersonaController(PersonaService service, ApplicationContext context) {
        super(service, context);
        initController(new Persona(), "Lista de Personas", "Persona");
    }
}
