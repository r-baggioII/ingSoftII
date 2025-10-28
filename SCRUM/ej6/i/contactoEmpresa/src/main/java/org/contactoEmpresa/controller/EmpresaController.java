package org.contactoEmpresa.controller;

import org.contactoEmpresa.entity.Empresa;
import org.contactoEmpresa.entity.Contacto;
import org.contactoEmpresa.service.EmpresaService;
import org.contactoEmpresa.service.ContactoCorreoElectronicoService;
import org.contactoEmpresa.service.ContactoTelefonicoService;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.contactoEmpresa.enums.BaseUseCaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/empresa")
public class EmpresaController extends BaseController<Empresa, String> {

    private final ContactoCorreoElectronicoService contactoCorreoService;
    private final ContactoTelefonicoService contactoTelefonicoService;

    @Autowired
    public EmpresaController(EmpresaService service, 
                            ContactoCorreoElectronicoService contactoCorreoService,
                            ContactoTelefonicoService contactoTelefonicoService) {
        super(service);
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
        initController(new Empresa(), "Listado de Empresas", "Gestión de Empresas");
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        if (useCase == BaseUseCaseController.ALTA || 
            useCase == BaseUseCaseController.MODIFICACION || 
            useCase == BaseUseCaseController.CONSULTAR) {
            
            // Combinar todos los contactos disponibles
            List<Object> todosLosContactos = new ArrayList<>();
            todosLosContactos.addAll(contactoCorreoService.listarActivos());
            todosLosContactos.addAll(contactoTelefonicoService.listarActivos());
            
            this.model.addAttribute("contactosDisponibles", todosLosContactos);
        }
        
        // Si estamos actualizando y entity es una Empresa, procesar el contacto
        if (useCase == BaseUseCaseController.ACTUALIZAR && this.entity instanceof Empresa) {
            procesarContactoEmpresa((Empresa) this.entity);
        }
    }
    
    private void procesarContactoEmpresa(Empresa empresa) throws ErrorServiceException {
        // Obtener el contactoId del contexto web (se establece en el método actualizar)
        String contactoId = (String) this.model.getAttribute("contactoId");
        
        if (contactoId != null && !contactoId.isEmpty()) {
            Contacto contacto = buscarContactoPorId(contactoId);
            empresa.setContacto(contacto);
        } else {
            empresa.setContacto(null);
        }
    }

    @Override
    @PostMapping("/actualizar")
    public String actualizar(
            @ModelAttribute("item") Empresa empresa,
            RedirectAttributes attributes,
            Model model) {
        
        // Capturar el parámetro contactoId y agregarlo al modelo
        if (model instanceof org.springframework.validation.support.BindingAwareModelMap) {
            org.springframework.web.context.request.ServletRequestAttributes attrs = 
                (org.springframework.web.context.request.ServletRequestAttributes) 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String contactoId = attrs.getRequest().getParameter("contactoId");
                model.addAttribute("contactoId", contactoId);
            }
        }
        
        // Llamar al método padre que se encargará del resto
        return super.actualizar(empresa, attributes, model);
    }

    private Contacto buscarContactoPorId(String id) throws ErrorServiceException {
        if (id == null || id.isEmpty()) {
            return null;
        }
        
        // Intentar buscar en correos electrónicos
        var correoOpt = contactoCorreoService.obtener(id);
        if (correoOpt.isPresent()) {
            return correoOpt.get();
        }
        
        // Intentar buscar en teléfonos
        var telefonoOpt = contactoTelefonicoService.obtener(id);
        if (telefonoOpt.isPresent()) {
            return telefonoOpt.get();
        }
        
        return null;
    }

    @Override
    protected Empresa loadingEntityForFormByError(BaseUseCaseController useCase, Model model, Empresa entity) throws ErrorServiceException {
        preUseCase(useCase);
        return entity;
    }
}