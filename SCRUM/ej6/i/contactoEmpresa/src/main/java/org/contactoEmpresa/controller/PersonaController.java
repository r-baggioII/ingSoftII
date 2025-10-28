package org.contactoEmpresa.controller;

import org.contactoEmpresa.entity.Persona;
import org.contactoEmpresa.entity.Contacto;
import org.contactoEmpresa.service.PersonaService;
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
@RequestMapping("/persona")
public class PersonaController extends BaseController<Persona, String> {

    private final ContactoCorreoElectronicoService contactoCorreoService;
    private final ContactoTelefonicoService contactoTelefonicoService;

    @Autowired
    public PersonaController(PersonaService service,
                            ContactoCorreoElectronicoService contactoCorreoService,
                            ContactoTelefonicoService contactoTelefonicoService) {
        super(service);
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
        initController(new Persona(), "Listado de Personas", "Gestión de Persona");
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
        
        // Si estamos actualizando y entity es una Persona, procesar los contactos
        if (useCase == BaseUseCaseController.ACTUALIZAR && this.entity instanceof Persona) {
            procesarContactosPersona((Persona) this.entity);
        }
    }
    
    private void procesarContactosPersona(Persona persona) throws ErrorServiceException {
        // Obtener los contactoIds del contexto web
        @SuppressWarnings("unchecked")
        List<String> contactoIds = (List<String>) this.model.getAttribute("contactoIds");
        
        if (contactoIds != null && !contactoIds.isEmpty()) {
            // Limpiar contactos existentes
            persona.getContactos().clear();
            
            // Agregar los nuevos contactos
            for (String contactoId : contactoIds) {
                Contacto contacto = buscarContactoPorId(contactoId);
                if (contacto != null) {
                    persona.addContacto(contacto);
                }
            }
        } else {
            // Si no hay contactos seleccionados, limpiar la lista
            persona.getContactos().clear();
        }
    }

    @Override
    @PostMapping("/actualizar")
    public String actualizar(
            @ModelAttribute("item") Persona persona,
            RedirectAttributes attributes,
            Model model) {
        
        // Capturar los contactoIds desde el request usando RequestContextHolder
        org.springframework.web.context.request.ServletRequestAttributes attrs = 
            (org.springframework.web.context.request.ServletRequestAttributes) 
            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        
        if (attrs != null) {
            String[] contactoIdsArray = attrs.getRequest().getParameterValues("contactoIds");
            if (contactoIdsArray != null && contactoIdsArray.length > 0) {
                List<String> contactoIds = java.util.Arrays.asList(contactoIdsArray);
                model.addAttribute("contactoIds", contactoIds);
            }
        }
        
        // Llamar al método padre que se encargará del resto
        return super.actualizar(persona, attributes, model);
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
    protected Persona loadingEntityForFormByError(BaseUseCaseController useCase, Model model, Persona entity) throws ErrorServiceException {
        preUseCase(useCase);
        return entity;
    }
}