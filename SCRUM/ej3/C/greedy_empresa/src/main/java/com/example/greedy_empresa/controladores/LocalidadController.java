package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Departamento;
import com.example.greedy_empresa.entidades.Localidad;
import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.servicios.DepartamentoService;
import com.example.greedy_empresa.servicios.LocalidadService;
import com.example.greedy_empresa.servicios.PaisService;
import com.example.greedy_empresa.servicios.ProvinciaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/localidades")
public class LocalidadController extends BaseController<Localidad, LocalidadService> {

    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public LocalidadController(DepartamentoService departamentoService, ProvinciaService provinciaService, PaisService paisService) {
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @ModelAttribute("paises")
    public List<Pais> cargarPaises() {
        return paisService.listarActivos();
    }

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
            @RequestParam(value = "paisId", required = false) String paisId,
            @RequestParam(value = "provinciaId", required = false) String provinciaId,
            @RequestParam(value = "departamentoId", required = false) String departamentoId,
            Pageable pageable,
            Model model) {
        model.addAttribute("page", servicio.buscar(filtro, paisId, provinciaId, departamentoId, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("paisId", paisId);
        model.addAttribute("provinciaId", provinciaId);
        model.addAttribute("departamentoId", departamentoId);
        model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
        model.addAttribute("departamentosSeleccionados", obtenerDepartamentos(provinciaId));
        model.addAttribute("activeMenu", "localidades");
        return "localidades/list";
    }

    @GetMapping("/new")
    public String nuevo(@RequestParam(value = "paisId", required = false) String paisId,
            @RequestParam(value = "provinciaId", required = false) String provinciaId,
            @RequestParam(value = "departamentoId", required = false) String departamentoId,
            Model model) {
        Localidad localidad = new Localidad();
        localidad.setPaisId(paisId != null ? paisId : "");
        localidad.setProvinciaId(provinciaId != null ? provinciaId : "");
        localidad.setDepartamentoId(departamentoId != null ? departamentoId : "");
        model.addAttribute("localidad", localidad);
        model.addAttribute("provinciasSeleccionadas", obtenerProvincias(localidad.getPaisId()));
        model.addAttribute("departamentosSeleccionados", obtenerDepartamentos(localidad.getProvinciaId()));
        model.addAttribute("activeMenu", "localidades");
        return "localidades/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("localidad") Localidad localidad,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        String paisId = localidad.getPaisId();
        String provinciaId = localidad.getProvinciaId();
        String departamentoId = localidad.getDepartamentoId();
        if (paisId == null || paisId.isBlank()) {
            bindingResult.rejectValue("paisId", "error.paisId", "El país es obligatorio");
        }
        if (provinciaId == null || provinciaId.isBlank()) {
            bindingResult.rejectValue("provinciaId", "error.provinciaId", "La provincia es obligatoria");
        }
        if (departamentoId == null || departamentoId.isBlank()) {
            bindingResult.rejectValue("departamentoId", "error.departamentoId", "El departamento es obligatorio");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("departamentosSeleccionados", obtenerDepartamentos(provinciaId));
            model.addAttribute("activeMenu", "localidades");
            return "localidades/form";
        }
        try {
            servicio.guardar(localidad, departamentoId);
            redirectAttributes.addFlashAttribute("successMessage", "Localidad guardada correctamente");
            return "redirect:/localidades";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            String message = ex instanceof IllegalArgumentException ? ex.getMessage()
                    : "Los datos ingresados ya existen o no son válidos.";
            bindingResult.reject("error.general", message);
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("departamentosSeleccionados", obtenerDepartamentos(provinciaId));
            model.addAttribute("activeMenu", "localidades");
            return "localidades/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        Localidad localidad = servicio.buscarPorId(id);
        String paisId = localidad.getDepartamento() != null && localidad.getDepartamento().getProvincia() != null
                && localidad.getDepartamento().getProvincia().getPais() != null
                        ? localidad.getDepartamento().getProvincia().getPais().getId() : "";
        String provinciaId = localidad.getDepartamento() != null && localidad.getDepartamento().getProvincia() != null
                ? localidad.getDepartamento().getProvincia().getId() : "";
        String departamentoId = localidad.getDepartamento() != null ? localidad.getDepartamento().getId() : "";

        model.addAttribute("localidad", localidad);
        model.addAttribute("paisId", paisId);
        model.addAttribute("provinciaId", provinciaId);
        model.addAttribute("departamentoId", departamentoId);
        model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
        model.addAttribute("departamentosSeleccionados", obtenerDepartamentos(provinciaId));
        model.addAttribute("activeMenu", "localidades");
        return "localidades/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid @ModelAttribute("localidad") Localidad localidad,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        String paisId = localidad.getPaisId();
        String provinciaId = localidad.getProvinciaId();
        String departamentoId = localidad.getDepartamentoId();
        if (paisId == null || paisId.isBlank()) {
            bindingResult.rejectValue("paisId", "error.paisId", "El país es obligatorio");
        }
        if (provinciaId == null || provinciaId.isBlank()) {
            bindingResult.rejectValue("provinciaId", "error.provinciaId", "La provincia es obligatoria");
        }
        if (departamentoId == null || departamentoId.isBlank()) {
            bindingResult.rejectValue("departamentoId", "error.departamentoId", "El departamento es obligatorio");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("departamentosSeleccionados", obtenerDepartamentos(provinciaId));
            model.addAttribute("activeMenu", "localidades");
            return "localidades/form";
        }
        try {
            localidad.setId(id);
            servicio.guardar(localidad, departamentoId);
            redirectAttributes.addFlashAttribute("successMessage", "Localidad actualizada correctamente");
            return "redirect:/localidades";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            String message = ex instanceof IllegalArgumentException ? ex.getMessage()
                    : "Los datos ingresados ya existen o no son válidos.";
            bindingResult.reject("error.general", message);
            model.addAttribute("provinciasSeleccionadas", obtenerProvincias(paisId));
            model.addAttribute("departamentosSeleccionados", obtenerDepartamentos(provinciaId));
            model.addAttribute("activeMenu", "localidades");
            return "localidades/form";
        }
    }

    private List<Provincia> obtenerProvincias(String paisId) {
        if (paisId == null || paisId.isBlank()) {
            return provinciaService.listarTodas();
        }
        return provinciaService.listarPorPais(paisId);
    }

    private List<Departamento> obtenerDepartamentos(String provinciaId) {
        if (provinciaId == null || provinciaId.isBlank()) {
            return departamentoService.listarTodos();
        }
        return departamentoService.listarPorProvincia(provinciaId);
    }

    @Override
    protected String getNombreEntidad() {
        return "Localidad";
    }
}