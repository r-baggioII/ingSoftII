package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DepartamentoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.LocalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DepartamentoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.LocalidadService;

/**
 * Controlador MVC para coordinar las interacciones de Localidad con Thymeleaf.
 */
@Controller
@RequestMapping("/localidades")
public class LocalidadController extends BaseThymeleafController<LocalidadDTO, Long> {

    private final DepartamentoService departamentoService;

    public LocalidadController(LocalidadService service, DepartamentoService departamentoService) {
        super(service);
        this.departamentoService = departamentoService;
    }

    @Override
    protected String getListView() {
        return "localidades/list";
    }

    @Override
    protected String getFormView() {
        return "localidades/form";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/localidades";
    }

    @Override
    protected String getListModelAttribute() {
        return "localidades";
    }

    @Override
    protected String getFormModelAttribute() {
        return "localidad";
    }

    @Override
    protected String getEntityLabel() {
        return "Localidad";
    }

    @Override
    protected LocalidadDTO buildNewInstance() {
        LocalidadDTO dto = new LocalidadDTO();
        dto.setDepartamento(new DepartamentoDTO());
        return dto;
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("departamentos", departamentoService.listarActivos());
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
        @ModelAttribute("localidad") LocalidadDTO localidadDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleCreate(localidadDTO, model, redirectAttributes);
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
        @ModelAttribute("localidad") LocalidadDTO localidadDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleUpdate(id, localidadDTO, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }
}
