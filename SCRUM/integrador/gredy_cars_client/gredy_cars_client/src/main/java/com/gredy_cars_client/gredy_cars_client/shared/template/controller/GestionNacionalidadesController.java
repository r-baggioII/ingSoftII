package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.NacionalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.NacionalidadService;

@Controller
@RequestMapping("/gestion")
public class GestionNacionalidadesController {

    private final NacionalidadService nacionalidadService;

    public GestionNacionalidadesController(NacionalidadService nacionalidadService) {
        this.nacionalidadService = nacionalidadService;
    }

    @GetMapping("/nacionalidades")
    public String gestionarNacionalidades(
        @RequestParam(value = "editId", required = false) String editId,
        Model model
    ) throws ErrorServiceException {
        model.addAttribute("nacionalidades", nacionalidadService.listarActivos());

        NacionalidadDTO form = editId != null && !editId.isBlank() ?
            nacionalidadService.obtener(editId).orElseGet(NacionalidadDTO::new) : new NacionalidadDTO();
        model.addAttribute("nacionalidadForm", form);

        return "gestion/gestion-nacionalidades";
    }

    @PostMapping("/nacionalidades")
    public String guardar(@ModelAttribute("nacionalidadForm") NacionalidadDTO nacionalidad,
                          RedirectAttributes ra) {
        try {
            if (nacionalidad.getId() == null || nacionalidad.getId().isBlank()) {
                nacionalidadService.alta(nacionalidad);
            } else {
                nacionalidadService.modificar(nacionalidad.getId(), nacionalidad);
            }
            ra.addFlashAttribute("success", "Nacionalidad guardada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/nacionalidades";
    }

    @PostMapping("/nacionalidades/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes ra) {
        try {
            nacionalidadService.baja(id);
            ra.addFlashAttribute("success", "Nacionalidad eliminada");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/nacionalidades";
    }
}

