package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Departamento;
import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.servicios.DepartamentoService;
import com.example.greedy_empresa.servicios.PaisService;
import com.example.greedy_empresa.servicios.ProvinciaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departamentos")
public class DepartamentoController extends BaseController<Departamento, DepartamentoService> {

    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public DepartamentoController(ProvinciaService provinciaService, PaisService paisService) {
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @ModelAttribute("paises")
    public List<Pais> cargarPaises() {
        return paisService.listarActivos();
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("departamento") Departamento departamento,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        String paisId = departamento.getPaisId();
        String provinciaId = departamento.getProvinciaId();
        if (paisId == null || paisId.isBlank()) {
            bindingResult.rejectValue("paisId", "error.paisId", "El país es obligatorio");
        }
        if (provinciaId == null || provinciaId.isBlank()) {
            bindingResult.rejectValue("provinciaId", "error.provinciaId", "La provincia es obligatoria");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("activeMenu", "departamentos");
            return "departamentos/form";
        }
        try {
            servicio.guardar(departamento, provinciaId);
            redirectAttributes.addFlashAttribute("successMessage", "Departamento guardado correctamente");
            return "redirect:/departamentos";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            String message = ex instanceof IllegalArgumentException ? ex.getMessage()
                    : "Los datos ingresados ya existen o no son válidos.";
            bindingResult.reject("error.general", message);
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("activeMenu", "departamentos");
            return "departamentos/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        Departamento departamento = servicio.buscarPorId(id);
        String paisId = departamento.getProvincia() != null && departamento.getProvincia().getPais() != null
                ? departamento.getProvincia().getPais().getId() : "";
        String provinciaId = departamento.getProvincia() != null ? departamento.getProvincia().getId() : "";
        model.addAttribute("departamento", departamento);
        model.addAttribute("paisId", paisId);
        model.addAttribute("provinciaId", provinciaId);
        model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
        model.addAttribute("activeMenu", "departamentos");
        return "departamentos/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid @ModelAttribute("departamento") Departamento departamento,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        String paisId = departamento.getPaisId();
        String provinciaId = departamento.getProvinciaId();
        if (paisId == null || paisId.isBlank()) {
            bindingResult.rejectValue("paisId", "error.paisId", "El país es obligatorio");
        }
        if (provinciaId == null || provinciaId.isBlank()) {
            bindingResult.rejectValue("provinciaId", "error.provinciaId", "La provincia es obligatoria");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("activeMenu", "departamentos");
            return "departamentos/form";
        }
        try {
            departamento.setId(id);
            servicio.guardar(departamento, provinciaId);
            redirectAttributes.addFlashAttribute("successMessage", "Departamento actualizado correctamente");
            return "redirect:/departamentos";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            String message = ex instanceof IllegalArgumentException ? ex.getMessage()
                    : "Los datos ingresados ya existen o no son válidos.";
            bindingResult.reject("error.general", message);
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("activeMenu", "departamentos");
            return "departamentos/form";
        }
    }

    private List<Provincia> obtenerProvincias(String paisId) {
        if (paisId == null || paisId.isBlank()) {
            return provinciaService.listarTodas();
        }
        return provinciaService.listarPorPais(paisId);
    }

    @Override
    protected String getNombreEntidad() {
        return "Departamento";
    }
}