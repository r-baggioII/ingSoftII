package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.VehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.AlquilerService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ClienteService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.VehiculoService;

@Controller
@RequestMapping("/gestion")
public class GestionAlquileresController {

    private final AlquilerService alquilerService;
    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;

    public GestionAlquileresController(AlquilerService alquilerService,
                                    ClienteService clienteService,
                                    VehiculoService vehiculoService) {
        this.alquilerService = alquilerService;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/alquileres")
    public String gestionarAlquileres(
        @RequestParam(value = "editId", required = false) String editId,
        Model model
    ) throws ErrorServiceException {
        model.addAttribute("alquileres", alquilerService.listarActivos());

        // Load clients and vehicles for dropdowns
        List<ClienteDTO> clientes = clienteService.listarActivos();
        List<VehiculoDTO> vehiculos = vehiculoService.listarActivos();

        model.addAttribute("clientes", clientes);
        model.addAttribute("vehiculos", vehiculos);

        AlquilerDTO form = editId != null && !editId.isBlank() ?
            alquilerService.obtener(editId).orElseGet(AlquilerDTO::new) : new AlquilerDTO();
        model.addAttribute("alquilerForm", form);

        return "gestion/gestion-alquileres";
    }

    @PostMapping("/alquileres")
    public String guardar(@ModelAttribute("alquilerForm") AlquilerDTO alquiler,
                          RedirectAttributes ra) {
        try {
            if (alquiler.getId() == null || alquiler.getId().isBlank()) {
                alquilerService.alta(alquiler);
            } else {
                alquilerService.modificar(alquiler.getId(), alquiler);
            }
            ra.addFlashAttribute("success", "Alquiler guardado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/alquileres";
    }

    @PostMapping("/alquileres/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes ra) {
        try {
            alquilerService.baja(id);
            ra.addFlashAttribute("success", "Alquiler eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/alquileres";
    }
}