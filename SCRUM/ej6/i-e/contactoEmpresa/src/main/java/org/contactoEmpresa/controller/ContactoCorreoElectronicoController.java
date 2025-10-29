package org.contactoEmpresa.controller;

import org.contactoEmpresa.entity.ContactoCorreoElectronico;
import org.contactoEmpresa.service.ContactoCorreoElectronicoService;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.contactoEmpresa.enums.BaseUseCaseController;
import org.contactoEmpresa.enums.TipoContacto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/correo")
public class ContactoCorreoElectronicoController extends BaseController<ContactoCorreoElectronico, String> {

    @Autowired
    public ContactoCorreoElectronicoController(ContactoCorreoElectronicoService service) {
        super(service);
        initController(
            new ContactoCorreoElectronico(),
            "Lista de Contactos por Correo Electrónico",
            "Contacto por Correo Electrónico"
        );
        // Sobrescribir nombres de vistas para que coincidan con los archivos HTML
        this.viewList = "correo-list";
        this.viewEdit = "correo-form";
        this.redirectList = "redirect:/correo/list";
        this.nameEntityLower = "correo";
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        if (useCase == BaseUseCaseController.ALTA || useCase == BaseUseCaseController.MODIFICACION) {
            this.model.addAttribute("tiposContacto", TipoContacto.values());
        }
    }
}
