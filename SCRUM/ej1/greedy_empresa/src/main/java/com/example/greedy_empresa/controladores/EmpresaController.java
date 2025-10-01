package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Empresa;
import com.example.greedy_empresa.servicios.EmpresaService;
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
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
            @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", empresaService.buscar(filtro, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("activeMenu", "empresas");
        return "empresas/list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        model.addAttribute("empresa", new Empresa());
        model.addAttribute("activeMenu", "empresas");
        return "empresas/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("empresa") Empresa empresa, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "empresas");
            return "empresas/form";
        }
        try {
            empresaService.guardar(empresa);
            redirectAttributes.addFlashAttribute("successMessage", "Empresa guardada correctamente");
            return "redirect:/empresas";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "empresas");
            return "empresas/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("empresa", empresaService.buscarPorId(id));
        model.addAttribute("activeMenu", "empresas");
        return "empresas/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid @ModelAttribute("empresa") Empresa empresa,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "empresas");
            return "empresas/form";
        }
        try {
            empresa.setId(id);
            empresaService.guardar(empresa);
            redirectAttributes.addFlashAttribute("successMessage", "Empresa actualizada correctamente");
            return "redirect:/empresas";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "empresas");
            return "empresas/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        empresaService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Empresa eliminada correctamente");
        return "redirect:/empresas";
    }
}
