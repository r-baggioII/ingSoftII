package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.servicios.PaisService;
import com.example.greedy_empresa.servicios.ProvinciaService;
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

import java.util.List;

@Controller
@RequestMapping("/provincias")
@RequiredArgsConstructor
public class ProvinciaController {

    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    @ModelAttribute("paises")
    public List<Pais> cargarPaises() {
        return paisService.listarActivos();
    }

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
            @RequestParam(value = "paisId", required = false) String paisId,
            @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", provinciaService.buscar(filtro, paisId, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("paisId", paisId);
        model.addAttribute("activeMenu", "provincias");
        return "provincias/list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        model.addAttribute("provincia", new Provincia());
        model.addAttribute("paisId", "");
        model.addAttribute("activeMenu", "provincias");
        return "provincias/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("provincia") Provincia provincia,
            BindingResult bindingResult,
            @RequestParam("paisId") String paisId,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("paisId", paisId);
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
        try {
            provinciaService.guardar(provincia, paisId);
            redirectAttributes.addFlashAttribute("successMessage", "Provincia guardada correctamente");
            return "redirect:/provincias";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("paisId", paisId);
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        Provincia provincia = provinciaService.buscarPorId(id);
        model.addAttribute("provincia", provincia);
        model.addAttribute("paisId", provincia.getPais() != null ? provincia.getPais().getId() : "");
        model.addAttribute("activeMenu", "provincias");
        return "provincias/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid @ModelAttribute("provincia") Provincia provincia,
            BindingResult bindingResult,
            @RequestParam("paisId") String paisId,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("paisId", paisId);
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
        try {
            provincia.setId(id);
            provinciaService.guardar(provincia, paisId);
            redirectAttributes.addFlashAttribute("successMessage", "Provincia actualizada correctamente");
            return "redirect:/provincias";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("paisId", paisId);
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        provinciaService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Provincia eliminada correctamente");
        return "redirect:/provincias";
    }
}
