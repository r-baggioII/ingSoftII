package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.Collections;
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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.VehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CaracteristicaVehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.VehiculoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.CaracteristicaVehiculoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.CostoVehiculoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ImagenService;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.EstadoVehiculo;

@Controller
@RequestMapping("/gestion")
public class GestionVehiculosController {

    private final VehiculoService vehiculoService;
    private final CaracteristicaVehiculoService caracteristicaService;
    private final CostoVehiculoService costoService;
    private final ImagenService imagenService;

    public GestionVehiculosController(
            VehiculoService vehiculoService,
            CaracteristicaVehiculoService caracteristicaService,
            CostoVehiculoService costoService,
            ImagenService imagenService) {
        this.vehiculoService = vehiculoService;
        this.caracteristicaService = caracteristicaService;
        this.costoService = costoService;
        this.imagenService = imagenService;
    }

    @GetMapping("/vehiculos")
    public String gestionarVehiculos(
        @RequestParam(value = "editId", required = false) String editId,
        Model model
    ) {
        model.addAttribute("vehiculos", cargarVehiculos(model));
        VehiculoDTO vehiculoForm = cargarVehiculoForm(editId, model);
        if (vehiculoForm.getEstadoVehiculo() == null) {
            vehiculoForm.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
        }
        model.addAttribute("vehiculoForm", vehiculoForm);

        model.addAttribute("caracteristicaForm", buildCaracteristicaForm(vehiculoForm));

        return "gestion/gestion-vehiculos";
    }

    @PostMapping("/vehiculos")
    public String guardarVehiculo(@ModelAttribute("vehiculoForm") VehiculoDTO vehiculo,
                          @ModelAttribute("caracteristicaForm") CaracteristicaVehiculoDTO caracteristica,
                          RedirectAttributes ra) {
        try {
            sanitizeVehiculo(vehiculo);
            normalizeCaracteristica(caracteristica);

            if (vehiculo.getEstadoVehiculo() == null) {
                vehiculo.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
            }
            // Set default values for characteristic when creating new vehicle
            String caracteristicaId = sanitizeIdentifier(caracteristica.getId());
            caracteristica.setId(caracteristicaId);
            if (caracteristicaId == null) {
                caracteristica.setCantidadVehiculoAlquilado(0); // Always start with 0 rented
                if (caracteristica.getCantidadTotalVehiculo() == 0) {
                    caracteristica.setCantidadTotalVehiculo(1); // Default to 1 vehicle
                }
            }

            // Primero guardar/actualizar la característica
            String caracId;
            // Properly check if characteristic ID is null, empty, or just whitespace
            if (caracteristicaId == null) {
                CaracteristicaVehiculoDTO saved = caracteristicaService.alta(caracteristica);
                caracId = saved.getId();
                caracteristica.setId(caracId);
            } else {
                caracteristicaService.modificar(caracteristicaId, caracteristica);
                caracId = caracteristicaId;
            }

            // Asociar la característica al vehículo
            vehiculo.setCaracteristicaVehiculoId(caracId);

            // Guardar/actualizar el vehículo
            String vehiculoId = sanitizeIdentifier(vehiculo.getId());
            vehiculo.setId(vehiculoId);
            if (vehiculoId == null) {
                vehiculoService.alta(vehiculo);
            } else {
                vehiculoService.modificar(vehiculoId, vehiculo);
            }

            ra.addFlashAttribute("success", "Vehículo guardado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/vehiculos";
    }

    @PostMapping("/vehiculos/{id}/eliminar")
    public String eliminarVehiculo(@PathVariable String id, RedirectAttributes ra) {
        try {
            vehiculoService.baja(id);
            ra.addFlashAttribute("success", "Vehículo eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/vehiculos";
    }

    private CaracteristicaVehiculoDTO buildCaracteristicaForm(VehiculoDTO vehiculoForm) {
        CaracteristicaVehiculoDTO form = new CaracteristicaVehiculoDTO();
        if (vehiculoForm == null || vehiculoForm.getCaracteristica() == null) {
            form.setCantidadTotalVehiculo(Math.max(form.getCantidadTotalVehiculo(), 1));
            form.setCantidadVehiculoAlquilado(Math.max(form.getCantidadVehiculoAlquilado(), 0));
            return form;
        }
        form.setId(vehiculoForm.getCaracteristica().getId());
        form.setMarca(vehiculoForm.getCaracteristica().getMarca());
        form.setModelo(vehiculoForm.getCaracteristica().getModelo());
        form.setAnio(vehiculoForm.getCaracteristica().getAnio());
        form.setCantidadPuerta(vehiculoForm.getCaracteristica().getCantidadPuerta());
        form.setCantidadAsiento(vehiculoForm.getCaracteristica().getCantidadAsiento());
        form.setCantidadTotalVehiculo(vehiculoForm.getCaracteristica().getCantidadTotalVehiculo());
        form.setCantidadVehiculoAlquilado(vehiculoForm.getCaracteristica().getCantidadVehiculoAlquilado());
        return form;
    }

    private List<VehiculoDTO> cargarVehiculos(Model model) {
        try {
            return vehiculoService.listarActivos();
        } catch (ErrorServiceException e) {
            appendError(model, "No se pudo cargar la lista de vehículos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private VehiculoDTO cargarVehiculoForm(String editId, Model model) {
        if (editId == null || editId.trim().isBlank()) {
            return new VehiculoDTO();
        }
        try {
            return vehiculoService.obtener(editId.trim()).orElseGet(VehiculoDTO::new);
        } catch (ErrorServiceException e) {
            appendError(model, "No se pudo obtener el vehículo seleccionado: " + e.getMessage());
            return new VehiculoDTO();
        }
    }

    private void appendError(Model model, String message) {
        if (message == null || message.isBlank()) return;
        Object existing = model.asMap().get("error");
        if (existing == null) {
            model.addAttribute("error", message);
        } else if (existing instanceof String existingMessage && !existingMessage.isBlank()) {
            model.addAttribute("error", existingMessage + " | " + message);
        } else {
            model.addAttribute("error", message);
        }
    }

    private void sanitizeVehiculo(VehiculoDTO vehiculo) {
        if (vehiculo == null) {
            return;
        }
        if (vehiculo.getPatente() != null) {
            vehiculo.setPatente(vehiculo.getPatente().trim().toUpperCase());
        }
        String caracteristicaId = sanitizeIdentifier(vehiculo.getCaracteristicaVehiculoId());
        vehiculo.setCaracteristicaVehiculoId(caracteristicaId);
        if (vehiculo.getCaracteristica() != null) {
            vehiculo.getCaracteristica().setId(caracteristicaId);
        }
    }

    private void normalizeCaracteristica(CaracteristicaVehiculoDTO dto) {
        if (dto == null) {
            return;
        }
        if (dto.getMarca() != null) {
            dto.setMarca(dto.getMarca().trim());
        }
        if (dto.getModelo() != null) {
            dto.setModelo(dto.getModelo().trim());
        }
        if (dto.getAnio() <= 0) {
            dto.setAnio(1);
        }
        if (dto.getCantidadPuerta() <= 0) {
            dto.setCantidadPuerta(1);
        }
        if (dto.getCantidadAsiento() <= 0) {
            dto.setCantidadAsiento(1);
        }
        if (dto.getCantidadTotalVehiculo() <= 0) {
            dto.setCantidadTotalVehiculo(1);
        }
        if (dto.getCantidadVehiculoAlquilado() < 0) {
            dto.setCantidadVehiculoAlquilado(0);
        }
        if (dto.getCantidadVehiculoAlquilado() > dto.getCantidadTotalVehiculo()) {
            dto.setCantidadVehiculoAlquilado(dto.getCantidadTotalVehiculo());
        }
    }

    private String sanitizeIdentifier(String rawId) {
        if (rawId == null) {
            return null;
        }
        String cleaned = rawId.trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        cleaned = cleaned.replaceAll("[^0-9a-fA-F\\-]", "");
        return cleaned.isEmpty() ? null : cleaned;
    }
}
