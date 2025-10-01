package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.servicios.PaisService;
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
@RequiredArgsConstructor
@RequestMapping("/paises")
public class PaisController {

    private final PaisService paisService;

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
            @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", paisService.buscar(filtro, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("activeMenu", "paises");
        return "paises/list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        model.addAttribute("pais", new Pais());
        model.addAttribute("activeMenu", "paises");
        return "paises/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("pais") Pais pais, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "paises");
            return "paises/form";
        }
        try {
            paisService.guardar(pais);
            redirectAttributes.addFlashAttribute("successMessage", "País guardado correctamente");
            return "redirect:/paises";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "paises");
            return "paises/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("pais", paisService.buscarPorId(id));
        model.addAttribute("activeMenu", "paises");
        return "paises/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id, @Valid @ModelAttribute("pais") Pais pais,
            BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "paises");
            return "paises/form";
        }
        try {
            pais.setId(id);
            paisService.guardar(pais);
            redirectAttributes.addFlashAttribute("successMessage", "País actualizado correctamente");
            return "redirect:/paises";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", "paises");
            return "paises/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        paisService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "País eliminado correctamente");
        return "redirect:/paises";
    }
}
