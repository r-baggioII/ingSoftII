package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.servicios.PaisService;
import com.example.greedy_empresa.servicios.ProvinciaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/provincias")
public class ProvinciaController extends BaseController<Provincia, ProvinciaService> {

    private final PaisService paisService;

    public ProvinciaController(PaisService paisService) {
        this.paisService = paisService;
    }

    @ModelAttribute("paises")
    public List<Pais> cargarPaises() {
        return paisService.listarActivos();
    }

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
                         @PageableDefault(size = 10) Pageable pageable,
                         Model model) {
        return super.listar(filtro, pageable, model);
    }

    @Override
    @GetMapping("/new")
    public String nuevo(Model model) {
        Provincia provincia = new Provincia();
        provincia.setPaisId("");
        model.addAttribute("provincia", provincia);
        model.addAttribute("activeMenu", "provincias");
        return "provincias/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("provincia") Provincia provincia,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        String paisId = provincia.getPaisId();
        if (paisId == null || paisId.isBlank()) {
            bindingResult.rejectValue("paisId", "error.paisId", "El país es obligatorio");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
        try {
            servicio.guardar(provincia, paisId);
            redirectAttributes.addFlashAttribute("successMessage", "Provincia guardada correctamente");
            return "redirect:/provincias";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            String message = ex instanceof IllegalArgumentException ? ex.getMessage()
                    : "Los datos ingresados ya existen o no son válidos.";
            bindingResult.reject("error.general", message);
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
    }

    @Override
    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        Provincia provincia = servicio.buscarPorId(id);
        model.addAttribute("provincia", provincia);
        model.addAttribute("paisId", provincia.getPais() != null ? provincia.getPais().getId() : "");
        model.addAttribute("activeMenu", "provincias");
        return "provincias/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid @ModelAttribute("provincia") Provincia provincia,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        String paisId = provincia.getPaisId();
        if (paisId == null || paisId.isBlank()) {
            bindingResult.rejectValue("paisId", "error.paisId", "El país es obligatorio");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
        try {
            provincia.setId(id);
            servicio.guardar(provincia, paisId);
            redirectAttributes.addFlashAttribute("successMessage", "Provincia actualizada correctamente");
            return "redirect:/provincias";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            String message = ex instanceof IllegalArgumentException ? ex.getMessage()
                    : "Los datos ingresados ya existen o no son válidos.";
            bindingResult.reject("error.general", message);
            model.addAttribute("activeMenu", "provincias");
            return "provincias/form";
        }
    }

    @Override
    protected String getNombreEntidad() {
        return "Provincia";
    }
}
