package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpleadoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ImagenDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoEmpleado;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoImagen;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.EmpleadoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ImagenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestion/empleados")
public class GestionEmpleadosController {

    private static final Logger log = LoggerFactory.getLogger(GestionEmpleadosController.class);

    private final EmpleadoService empleadoService;
    private final ContactoCorreoElectronicoService contactoCorreoService;
    private final ContactoTelefonicoService contactoTelefonicoService;
    private final DireccionService direccionService;
    private final ImagenService imagenService;

    public GestionEmpleadosController(EmpleadoService empleadoService,
                                      ContactoCorreoElectronicoService contactoCorreoService,
                                      ContactoTelefonicoService contactoTelefonicoService,
                                      DireccionService direccionService,
                                      ImagenService imagenService) {
        this.empleadoService = empleadoService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
        this.direccionService = direccionService;
        this.imagenService = imagenService;
    }

    @GetMapping
    public String gestionarEmpleados(@RequestParam(value = "editEmpleadoId", required = false) String editEmpleadoId,
                                     Model model) {
        try {
            // Listados
            model.addAttribute("empleados", empleadoService.listarActivos());

            // Catálogos / enums
            model.addAttribute("tiposDocumento", TipoDocumento.values());
            model.addAttribute("tiposEmpleado", TipoEmpleado.values());
            model.addAttribute("direcciones", direccionService.listarActivos());
            
            // Listas de contactos disponibles
            model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
            model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());
            
            // Listas de imágenes disponibles (tipo PERSONA)
            try {
                List<ImagenDTO> imagenes = imagenService.listarPorTipo(TipoImagen.PERSONA);
                model.addAttribute("imagenes", imagenes);
            } catch (Exception e) {
                log.warn("Error al cargar imágenes: {}", e.getMessage());
                model.addAttribute("imagenes", Collections.emptyList());
            }
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empleados", Collections.emptyList());
            model.addAttribute("tiposDocumento", TipoDocumento.values());
            model.addAttribute("tiposEmpleado", TipoEmpleado.values());
            model.addAttribute("direcciones", Collections.emptyList());
            model.addAttribute("contactosCorreo", Collections.emptyList());
            model.addAttribute("contactosTelefono", Collections.emptyList());
        }

        // Formulario (nuevo o edición)
        EmpleadoDTO empleadoForm;
        try {
            if (editEmpleadoId != null && !editEmpleadoId.isBlank()) {
                empleadoForm = empleadoService.obtener(editEmpleadoId).orElseGet(EmpleadoDTO::new);
            } else {
                empleadoForm = new EmpleadoDTO();
            }
        } catch (ErrorServiceException e) {
            empleadoForm = new EmpleadoDTO();
        }
        
        // Inicializar listas vacías si son null
        if (empleadoForm.getContactoIds() == null) {
            empleadoForm.setContactoIds(new ArrayList<>());
        }
        if (empleadoForm.getDireccionIds() == null) {
            empleadoForm.setDireccionIds(new ArrayList<>());
        }
        if (empleadoForm.getImagenIds() == null) {
            empleadoForm.setImagenIds(new ArrayList<>());
        }
        
        model.addAttribute("empleadoForm", empleadoForm);

        return "gestion/gestion-empleados";
    }

    @PostMapping("/empleado")
    public String guardarEmpleado(@ModelAttribute("empleadoForm") EmpleadoDTO dto, RedirectAttributes ra) {
        log.info("=== INICIO guardarEmpleado ===");
        log.info("DTO recibido: {}", dto);
        log.info("Nombre: {}, Apellido: {}", dto.getNombre(), dto.getApellido());
        log.info("Documento: {} - {}", dto.getTipoDocumento(), dto.getNumeroDocumento());
        log.info("TipoEmpleado: {}", dto.getTipoEmpleado());
        log.info("DireccionIds: {}", dto.getDireccionIds());
        log.info("ContactoIds: {}", dto.getContactoIds());
        
        try {
            // Limpiar objetos completos que no se deben enviar al servidor
            dto.setContactosCorreo(null);
            dto.setContactosTelefono(null);
            dto.setDirecciones(null);
            dto.setImagenes(null);
            
            // Asegurar que las listas de IDs no sean null
            if (dto.getDireccionIds() == null) {
                dto.setDireccionIds(new ArrayList<>());
            }
            if (dto.getContactoIds() == null) {
                dto.setContactoIds(new ArrayList<>());
            }
            if (dto.getImagenIds() == null) {
                dto.setImagenIds(new ArrayList<>());
            }
            
            log.info("Enviando al servidor - DireccionIds: {}, ContactoIds: {}", 
                    dto.getDireccionIds(), dto.getContactoIds());
            
            if (dto.getId() == null || dto.getId().isBlank()) {
                log.info("Creando nuevo empleado...");
                EmpleadoDTO guardado = empleadoService.alta(dto);
                log.info("Empleado creado con ID: {}", guardado.getId());
                ra.addFlashAttribute("success", "Empleado creado correctamente");
            } else {
                log.info("Modificando empleado con ID: {}", dto.getId());
                empleadoService.modificar(dto.getId(), dto);
                ra.addFlashAttribute("success", "Empleado actualizado correctamente");
            }
        } catch (ErrorServiceException e) {
            log.error("Error al guardar empleado: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/empleados";
    }

    @PostMapping("/empleado/{id}/eliminar")
    public String eliminarEmpleado(@PathVariable String id, RedirectAttributes ra) {
        try {
            empleadoService.baja(id);
            ra.addFlashAttribute("success", "Empleado eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/empleados";
    }
}
