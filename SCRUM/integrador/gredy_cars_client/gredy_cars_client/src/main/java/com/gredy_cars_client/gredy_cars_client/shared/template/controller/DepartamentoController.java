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
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ProvinciaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DepartamentoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ProvinciaService;

/**
 * Controlador MVC para coordinar las interacciones de Departamento con Thymeleaf.
 */
@Controller
@RequestMapping("/departamentos")
public class DepartamentoController extends BaseThymeleafController<DepartamentoDTO, Long> {

    private final ProvinciaService provinciaService;

    public DepartamentoController(DepartamentoService service, ProvinciaService provinciaService) {
        super(service);
        this.provinciaService = provinciaService;
    }

    @Override
    protected String getListView() {
        return "departamentos/list";
    }

    @Override
    protected String getFormView() {
        return "departamentos/form";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/departamentos";
    }

    @Override
    protected String getListModelAttribute() {
        return "departamentos";
    }

    @Override
    protected String getFormModelAttribute() {
        return "departamento";
    }

    @Override
    protected String getEntityLabel() {
        return "Departamento";
    }

    @Override
    protected DepartamentoDTO buildNewInstance() {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setProvincia(new ProvinciaDTO());
        return dto;
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("provincias", provinciaService.listarActivos());
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
        @ModelAttribute("departamento") DepartamentoDTO departamentoDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleCreate(departamentoDTO, model, redirectAttributes);
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
        @ModelAttribute("departamento") DepartamentoDTO departamentoDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleUpdate(id, departamentoDTO, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }
}
