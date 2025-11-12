package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpleadoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ImagenDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseController;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoEmpleado;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoImagen;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.EmpleadoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ImagenService;

@Controller
@RequestMapping("/gestion/empleados")
public class GestionEmpleadosController extends BaseThymeleafController<EmpleadoDTO, String> {

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
        super(empleadoService);
        this.empleadoService = empleadoService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
        this.direccionService = direccionService;
        this.imagenService = imagenService;
    }

    @Override
    protected String getListView() {
        return "gestion/gestion-empleados";
    }

    @Override
    protected String getFormView() {
        return "gestion/gestion-empleados";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/gestion/empleados";
    }

    @Override
    protected String getListModelAttribute() {
        return "empleados";
    }

    @Override
    protected String getFormModelAttribute() {
        return "empleadoForm";
    }

    @Override
    protected String getEntityLabel() {
        return "Empleado";
    }

    @Override
    protected EmpleadoDTO buildNewInstance() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setContactoIds(new ArrayList<>());
        dto.setDireccionIds(new ArrayList<>());
        dto.setImagenIds(new ArrayList<>());
        return dto;
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("tiposEmpleado", TipoEmpleado.values());
        model.addAttribute("direcciones", direccionService.listarActivos());
        model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
        model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());

        try {
            List<ImagenDTO> imagenes = imagenService.listarPorTipo(TipoImagen.PERSONA);
            model.addAttribute("imagenes", imagenes);
        } catch (Exception e) {
            log.warn("Error al cargar imágenes de empleados: {}", e.getMessage());
            model.addAttribute("imagenes", Collections.emptyList());
        }
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase, Model model) throws ErrorServiceException {
        populateCollections(model);
    }

    @GetMapping
    public String gestionarEmpleados(@RequestParam(value = "editEmpleadoId", required = false) String editEmpleadoId,
                                     Model model) {
        String view = renderList(model);
        if (editEmpleadoId != null && !editEmpleadoId.isBlank()) {
            try {
                Optional<EmpleadoDTO> empleadoOpt = service.obtener(editEmpleadoId);
                if (empleadoOpt.isPresent()) {
                    model.addAttribute(getFormModelAttribute(), empleadoOpt.get());
                } else {
                    registerError(model, "Empleado no encontrado");
                    model.addAttribute(getFormModelAttribute(), buildNewInstance());
                }
            } catch (ErrorServiceException e) {
                log.warn("No se pudo cargar el empleado {}: {}", editEmpleadoId, e.getMessage());
                registerError(model, e.getMessage());
                model.addAttribute(getFormModelAttribute(), buildNewInstance());
            }
        } else if (!model.containsAttribute(getFormModelAttribute())) {
            model.addAttribute(getFormModelAttribute(), buildNewInstance());
        }
        return view;
    }

    @PostMapping
    public String guardarEmpleado(@ModelAttribute("empleadoForm") EmpleadoDTO dto,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        return handleCreate(dto, model, redirectAttributes);
    }

    @PostMapping("/{id}")
    public String actualizarEmpleado(@PathVariable String id,
                                     @ModelAttribute("empleadoForm") EmpleadoDTO dto,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        return handleUpdate(id, dto, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarEmpleado(@PathVariable String id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }

    private EmpleadoDTO sanitizeEmpleadoDTO(EmpleadoDTO dto) {
        if (dto == null) {
            return buildNewInstance();
        }

        dto.setContactosCorreo(null);
        dto.setContactosTelefono(null);
        dto.setDirecciones(null);
        dto.setImagenes(null);

        if (dto.getContactoIds() == null) {
            dto.setContactoIds(new ArrayList<>());
        }
        if (dto.getDireccionIds() == null) {
            dto.setDireccionIds(new ArrayList<>());
        }
        if (dto.getImagenIds() == null) {
            dto.setImagenIds(new ArrayList<>());
        }

        return dto;
    }

    @Override
    public String handleCreate(EmpleadoDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleCreate(sanitizeEmpleadoDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }

    @Override
    public String handleUpdate(String id, EmpleadoDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleUpdate(id, sanitizeEmpleadoDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }
}
