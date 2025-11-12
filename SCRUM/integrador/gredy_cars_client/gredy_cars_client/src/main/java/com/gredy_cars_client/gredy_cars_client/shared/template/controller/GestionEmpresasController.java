package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpresaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseController;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.EmpresaService;

@Controller
@RequestMapping("/gestion/empresas")
public class GestionEmpresasController extends BaseThymeleafController<EmpresaDTO, String> {

    private final DireccionService direccionService;
    private final ContactoCorreoElectronicoService contactoCorreoService;
    private final ContactoTelefonicoService contactoTelefonicoService;

    public GestionEmpresasController(EmpresaService empresaService,
                                     DireccionService direccionService,
                                     ContactoCorreoElectronicoService contactoCorreoService,
                                     ContactoTelefonicoService contactoTelefonicoService) {
        super(empresaService);
        this.direccionService = direccionService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
    }

    @Override
    protected String getListView() {
        return "gestion/gestion-empresas";
    }

    @Override
    protected String getFormView() {
        return "gestion/gestion-empresas";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/gestion/empresas";
    }

    @Override
    protected String getListModelAttribute() {
        return "empresas";
    }

    @Override
    protected String getFormModelAttribute() {
        return "empresaForm";
    }

    @Override
    protected String getEntityLabel() {
        return "Empresa";
    }

    @Override
    protected EmpresaDTO buildNewInstance() {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setDireccionIds(new ArrayList<>());
        dto.setContactoIds(new ArrayList<>());
        return dto;
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("direcciones", direccionService.listarActivos());
        model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
        model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase, Model model) throws ErrorServiceException {
        populateCollections(model);
    }

    @GetMapping
    public String gestionarEmpresas(@RequestParam(value = "editEmpresaId", required = false) String editEmpresaId,
                                    Model model) {
        String view = renderList(model);
        if (editEmpresaId != null && !editEmpresaId.isBlank()) {
            try {
                Optional<EmpresaDTO> empresaOpt = service.obtener(editEmpresaId);
                if (empresaOpt.isPresent()) {
                    model.addAttribute(getFormModelAttribute(), empresaOpt.get());
                } else {
                    registerError(model, "Empresa no encontrada");
                    model.addAttribute(getFormModelAttribute(), buildNewInstance());
                }
            } catch (ErrorServiceException e) {
                log.warn("No se pudo cargar la empresa {}: {}", editEmpresaId, e.getMessage());
                registerError(model, e.getMessage());
                model.addAttribute(getFormModelAttribute(), buildNewInstance());
            }
        } else if (!model.containsAttribute(getFormModelAttribute())) {
            model.addAttribute(getFormModelAttribute(), buildNewInstance());
        }
        return view;
    }

    @PostMapping
    public String crearEmpresa(@ModelAttribute("empresaForm") EmpresaDTO dto,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        return handleCreate(dto, model, redirectAttributes);
    }

    @PostMapping("/{id}")
    public String actualizarEmpresa(@PathVariable String id,
                                    @ModelAttribute("empresaForm") EmpresaDTO dto,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        return handleUpdate(id, dto, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarEmpresa(@PathVariable String id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }

    private EmpresaDTO sanitizeEmpresaDTO(EmpresaDTO dto) {
        if (dto == null) {
            return buildNewInstance();
        }

        if (dto.getDireccionIds() == null) {
            dto.setDireccionIds(new ArrayList<>());
        }
        if (dto.getContactoIds() == null) {
            dto.setContactoIds(new ArrayList<>());
        }
        return dto;
    }

    @Override
    public String handleCreate(EmpresaDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleCreate(sanitizeEmpresaDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }

    @Override
    public String handleUpdate(String id, EmpresaDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleUpdate(id, sanitizeEmpresaDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }
}
