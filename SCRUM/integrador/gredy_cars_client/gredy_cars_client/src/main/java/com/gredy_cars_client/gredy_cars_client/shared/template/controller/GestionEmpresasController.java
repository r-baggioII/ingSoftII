package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoTelefonicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpresaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.EmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestion/empresas")
public class GestionEmpresasController {

    private static final Logger log = LoggerFactory.getLogger(GestionEmpresasController.class);

    private final EmpresaService empresaService;
    private final DireccionService direccionService;
    private final ContactoCorreoElectronicoService contactoCorreoService;
    private final ContactoTelefonicoService contactoTelefonicoService;

    public GestionEmpresasController(EmpresaService empresaService,
                                     DireccionService direccionService,
                                     ContactoCorreoElectronicoService contactoCorreoService,
                                     ContactoTelefonicoService contactoTelefonicoService) {
        this.empresaService = empresaService;
        this.direccionService = direccionService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
    }

    @GetMapping
    public String gestionarEmpresas(@RequestParam(value = "editEmpresaId", required = false) String editEmpresaId,
                                    Model model) {
        try {
            // Listado de empresas
            model.addAttribute("empresas", empresaService.listarActivos());

            // Catálogos de direcciones y contactos disponibles
            model.addAttribute("direcciones", direccionService.listarActivos());
            
            // Listas de contactos disponibles
            model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
            model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());
            
            // Si hay una empresa para editar, cargarla
            if (editEmpresaId != null && !editEmpresaId.isEmpty()) {
                try {
                    empresaService.obtener(editEmpresaId).ifPresent(empresa -> 
                        model.addAttribute("empresaEditar", empresa)
                    );
                } catch (ErrorServiceException e) {
                    log.error("Error al cargar empresa para editar: {}", e.getMessage());
                    model.addAttribute("error", "No se pudo cargar la empresa para editar");
                }
            }

        } catch (ErrorServiceException e) {
            log.error("Error al cargar datos de gestión de empresas", e);
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            model.addAttribute("empresas", Collections.emptyList());
            model.addAttribute("direcciones", Collections.emptyList());
            model.addAttribute("contactosCorreo", Collections.emptyList());
            model.addAttribute("contactosTelefono", Collections.emptyList());
        }

        return "gestion/gestion-empresas";
    }

    @PostMapping("/crear")
    public String crearEmpresa(@RequestParam("nombre") String nombre,
                               @RequestParam(value = "direccionIds", required = false) List<Long> direccionIds,
                               @RequestParam(value = "contactoIds", required = false) List<String> contactoIds,
                               RedirectAttributes redirectAttributes) {
        try {
            log.info("Creando empresa: {}", nombre);
            log.info("DireccionIds: {}", direccionIds);
            log.info("ContactoIds: {}", contactoIds);
            
            EmpresaDTO empresaDTO = new EmpresaDTO();
            empresaDTO.setNombre(nombre);
            empresaDTO.setDireccionIds(direccionIds != null ? direccionIds : new ArrayList<>());
            empresaDTO.setContactoIds(contactoIds != null ? contactoIds : new ArrayList<>());
            
            empresaService.alta(empresaDTO);
            redirectAttributes.addFlashAttribute("success", "Empresa creada exitosamente");
        } catch (ErrorServiceException e) {
            log.error("Error al crear empresa", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear empresa: " + e.getMessage());
        }
        return "redirect:/gestion/empresas";
    }

    @PostMapping("/modificar/{id}")
    public String modificarEmpresa(@PathVariable String id,
                                   @RequestParam("nombre") String nombre,
                                   @RequestParam(value = "direccionIds", required = false) List<Long> direccionIds,
                                   @RequestParam(value = "contactoIds", required = false) List<String> contactoIds,
                                   RedirectAttributes redirectAttributes) {
        try {
            log.info("Modificando empresa ID: {}", id);
            log.info("Nombre: {}, DireccionIds: {}, ContactoIds: {}", nombre, direccionIds, contactoIds);
            
            EmpresaDTO empresaDTO = new EmpresaDTO();
            empresaDTO.setId(id);
            empresaDTO.setNombre(nombre);
            empresaDTO.setDireccionIds(direccionIds != null ? direccionIds : new ArrayList<>());
            empresaDTO.setContactoIds(contactoIds != null ? contactoIds : new ArrayList<>());
            
            empresaService.modificar(id, empresaDTO);
            redirectAttributes.addFlashAttribute("success", "Empresa modificada exitosamente");
        } catch (ErrorServiceException e) {
            log.error("Error al modificar empresa", e);
            redirectAttributes.addFlashAttribute("error", "Error al modificar empresa: " + e.getMessage());
        }
        return "redirect:/gestion/empresas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarEmpresa(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            log.info("Eliminando empresa ID: {}", id);
            empresaService.baja(id);
            redirectAttributes.addFlashAttribute("success", "Empresa eliminada exitosamente");
        } catch (ErrorServiceException e) {
            log.error("Error al eliminar empresa", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar empresa: " + e.getMessage());
        }
        return "redirect:/gestion/empresas";
    }
}
