package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.servicios.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
            @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", proveedorService.buscar(filtro, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("activeMenu", "proveedores");
        return "proveedores/list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("activeMenu", "proveedores");
        return "proveedores/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("proveedor") Proveedor proveedor,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
        try {
            proveedorService.guardar(proveedor);
            redirectAttributes.addFlashAttribute("successMessage", "Proveedor guardado correctamente");
            return "redirect:/proveedores";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("proveedor", proveedorService.buscarPorId(id));
        model.addAttribute("activeMenu", "proveedores");
        return "proveedores/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid @ModelAttribute("proveedor") Proveedor proveedor,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
        try {
            proveedor.setId(id);
            proveedorService.guardar(proveedor);
            redirectAttributes.addFlashAttribute("successMessage", "Proveedor actualizado correctamente");
            return "redirect:/proveedores";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        proveedorService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Proveedor eliminado correctamente");
        return "redirect:/proveedores";
    }
}
