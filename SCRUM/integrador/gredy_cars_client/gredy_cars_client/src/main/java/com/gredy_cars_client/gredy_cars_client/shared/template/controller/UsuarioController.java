package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.UsuarioDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.Rol;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController extends BaseThymeleafController<UsuarioDTO, String> {

    public UsuarioController(UsuarioService usuarioService) {
        super(usuarioService);
    }

    @Override
    protected String getListView() {
        return "usuarios";
    }

    @Override
    protected String getFormView() {
        return "usuario-form";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/usuarios";
    }

    @Override
    protected String getListModelAttribute() {
        return "usuarios";
    }

    @Override
    protected String getFormModelAttribute() {
        return "usuario";
    }

    @Override
    protected String getEntityLabel() {
        return "Usuario";
    }

    @Override
    protected UsuarioDTO buildNewInstance() {
        return new UsuarioDTO();
    }

    @Override
    protected void populateCollections(Model model) {
        model.addAttribute("roles", Rol.values());
    }

    @GetMapping
    public String listar(Model model) {
        return renderList(model);
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        return renderCreateForm(model);
    }

    @PostMapping
    public String crear(@ModelAttribute("usuario") UsuarioDTO usuario,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        return handleCreate(usuario, model, redirectAttributes);
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable String id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        return renderEditForm(id, model, redirectAttributes);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
                             @ModelAttribute("usuario") UsuarioDTO usuario,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        return handleUpdate(id, usuario, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<UsuarioDTO> usuario = service.obtener(id);
            if (usuario.isPresent()) {
                model.addAttribute("usuario", usuario.get());
                return "usuario-detalle";
            }
            registerError(redirectAttributes, "Usuario no encontrado");
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }
}
