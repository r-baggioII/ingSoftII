package org.contactoEmpresa.controller;

import org.contactoEmpresa.entity.Persona;
import org.contactoEmpresa.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/persona")
public class PersonaController extends BaseController<Persona, String> {

    @Autowired
    public PersonaController(PersonaService service) {
        super(service);
        initController(new Persona(), "Listado de Personas", "Gestión de Persona");
    }

}