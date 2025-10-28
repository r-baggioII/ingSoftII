package org.contactoEmpresa.controller;


import org.contactoEmpresa.entity.Persona;
import org.contactoEmpresa.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/empresa")
public class EmpresaController extends BaseController<Persona, String> {

    @Autowired
    public EmpresaController(PersonaService service) {
        super(service);
        initController(new Persona(), "Listado de Empresas", "Gestión de Empresas");
    }

}