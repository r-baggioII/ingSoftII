package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.service.PaisService;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PaisDTO;

/**
 * Controlador MVC para coordinar las interacciones de País con Thymeleaf.
 */
@Controller
@RequestMapping("/paises")
public class PaisController extends BaseThymeleafController<PaisDTO, Long> {

    public PaisController(PaisService service) {
        super(service);
    }

    @Override
    protected String getListView() {
        return "paises/list";
    }

    @Override
    protected String getFormView() {
        return "paises/form";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/paises";
    }

    @Override
    protected String getListModelAttribute() {
        return "paises";
    }

    @Override
    protected String getFormModelAttribute() {
        return "pais";
    }

    @Override
    protected String getEntityLabel() {
        return "País";
    }

    @Override
    protected PaisDTO buildNewInstance() {
        return new PaisDTO();
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
        @ModelAttribute("pais") PaisDTO paisDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleCreate(paisDTO, model, redirectAttributes);
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
        @ModelAttribute("pais") PaisDTO paisDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleUpdate(id, paisDTO, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }
}
