package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PaisDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ProvinciaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.PaisService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ProvinciaService;

/**
 * Controlador MVC para coordinar las interacciones de Provincia con Thymeleaf.
 */
@Controller
@RequestMapping("/provincias")
public class ProvinciaController extends BaseThymeleafController<ProvinciaDTO, Long> {

    private final PaisService paisService;

    public ProvinciaController(ProvinciaService service, PaisService paisService) {
        super(service);
        this.paisService = paisService;
    }

    @Override
    protected String getListView() {
        return "provincias/list";
    }

    @Override
    protected String getFormView() {
        return "provincias/form";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/provincias";
    }

    @Override
    protected String getListModelAttribute() {
        return "provincias";
    }

    @Override
    protected String getFormModelAttribute() {
        return "provincia";
    }

    @Override
    protected String getEntityLabel() {
        return "Provincia";
    }

    @Override
    protected ProvinciaDTO buildNewInstance() {
        ProvinciaDTO dto = new ProvinciaDTO();
        dto.setPais(new PaisDTO());
        return dto;
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("paises", paisService.listarActivos());
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
        @ModelAttribute("provincia") ProvinciaDTO provinciaDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleCreate(provinciaDTO, model, redirectAttributes);
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
        @ModelAttribute("provincia") ProvinciaDTO provinciaDTO,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        return handleUpdate(id, provinciaDTO, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }
}
