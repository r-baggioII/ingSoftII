package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.LocalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.LocalidadService;

/**
 * Controlador MVC para coordinar las interacciones de Dirección con Thymeleaf.
 */
@Controller
@RequestMapping("/direcciones")
public class DireccionController extends BaseThymeleafController<DireccionDTO, Long> {

    private final LocalidadService localidadService;

    public DireccionController(DireccionService service, LocalidadService localidadService) {
        super(service);
        this.localidadService = localidadService;
    }

    @Override
    protected String getListView() {
        return "direcciones/list";
    }

    @Override
    protected String getFormView() {
        return "direcciones/form";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/direcciones";
    }

    @Override
    protected String getListModelAttribute() {
        return "direcciones";
    }

    @Override
    protected String getFormModelAttribute() {
        return "direccion";
    }

    @Override
    protected String getEntityLabel() {
        return "Dirección";
    }

    @Override
    protected DireccionDTO buildNewInstance() {
        DireccionDTO dto = new DireccionDTO();
        dto.setLocalidad(new LocalidadDTO());
        return dto;
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("localidades", localidadService.listarActivos());
    }

    @GetMapping
    public String listar(Model model) {
        return renderList(model);
    }

    @GetMapping("/nuevo")
    public String crearForm(Model model) {
        return renderCreateForm(model);
    }

    @PostMapping
    public String crear(
        @ModelAttribute("direccion") DireccionDTO direccionDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleCreate(direccionDTO, model, redirectAttributes);
    }

    @GetMapping("/{id}/editar")
    public String editarForm(
        @PathVariable Long id,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return renderEditForm(id, model, redirectAttributes);
    }

    @PostMapping("/{id}")
    public String actualizar(
        @PathVariable Long id,
        @ModelAttribute("direccion") DireccionDTO direccionDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleUpdate(id, direccionDTO, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }
}
