package org.contactoEmpresa.controller;

import org.contactoEmpresa.entity.ContactoTelefonico;
import org.contactoEmpresa.service.ContactoTelefonicoService;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.contactoEmpresa.enums.BaseUseCaseController;
import org.contactoEmpresa.enums.TipoContacto;
import org.contactoEmpresa.enums.TipoTelefono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contactotelefonico")
public class ContactoTelefonicoController extends BaseController<ContactoTelefonico, String> {

    @Autowired
    public ContactoTelefonicoController(ContactoTelefonicoService service) {
        super(service);
        initController(
            new ContactoTelefonico(),
            "Lista de Contactos Telefónicos",
            "Contacto Telefónico"
        );
        // Sobrescribir nombres de vistas para que coincidan con los archivos HTML
        this.viewList = "contactotelefonico-list";
        this.viewEdit = "contactotelefonico-form";
        this.redirectList = "redirect:/contactotelefonico/list";
        this.nameEntityLower = "contactotelefonico";
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        if (useCase == BaseUseCaseController.ALTA || useCase == BaseUseCaseController.MODIFICACION) {
            this.model.addAttribute("tiposContacto", TipoContacto.values());
            this.model.addAttribute("tiposTelefono", TipoTelefono.values());
        }
    }
}
