package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

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
        // Always synchronize characteristic counts with actual vehicle counts
        sincronizarConteosCaracteristicas();

        model.addAttribute("vehiculos", cargarVehiculos(model));
        model.addAttribute("caracteristicasExistentes", cargarCaracteristicasExistentes(model));
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
                          @RequestParam(value = "caracteristicaExistente", required = false) String caracteristicaExistente,
                          @RequestParam(value = "imagenId", required = false) String imagenId,
                          RedirectAttributes ra) {
        try {
            sanitizeVehiculo(vehiculo);

            if (vehiculo.getEstadoVehiculo() == null) {
                vehiculo.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
            }

            String vehiculoId = sanitizeIdentifier(vehiculo.getId());
            vehiculo.setId(vehiculoId);
            boolean isNewVehicle = (vehiculoId == null || vehiculoId.isEmpty());
            VehiculoDTO vehiculoPersistente = null;

            String caracId;

            // Check if user selected an existing characteristic
            String caracteristicaExistenteId = sanitizeIdentifier(caracteristicaExistente);
            if (caracteristicaExistenteId != null && !caracteristicaExistenteId.isEmpty()) {
                // Use existing characteristic - ignore the form fields
                caracId = caracteristicaExistenteId;
                vehiculo.setCaracteristicaVehiculoId(caracId);
            } else {
                // Create or update characteristic - only if no existing characteristic was selected
                normalizeCaracteristica(caracteristica);
                String caracteristicaId = sanitizeIdentifier(caracteristica.getId());
                caracteristica.setId(caracteristicaId);
                boolean needsCaracteristicaFallback = caracteristicaId == null
                        || (vehiculoId != null && vehiculoId.equals(caracteristicaId));

                if (!isNewVehicle && needsCaracteristicaFallback) {
                    vehiculoPersistente = vehiculoService.obtener(vehiculoId).orElse(null);
                }

                // When the form lost the characteristic ID (or accidentally sent the vehicle ID),
                // fall back to the characteristic already associated with the vehicle being edited.
                if (needsCaracteristicaFallback
                        && vehiculoPersistente != null
                        && vehiculoPersistente.getCaracteristicaVehiculoId() != null) {
                    caracteristicaId = sanitizeIdentifier(vehiculoPersistente.getCaracteristicaVehiculoId());
                    caracteristica.setId(caracteristicaId);
                }

                List<String> imagenIds = resolveImagenIdsForSave(imagenId, caracteristicaId);
                caracteristica.setImagenIds(imagenIds);

                if (caracteristicaId == null || caracteristicaId.isEmpty()) {
                    caracteristica.setCantidadVehiculoAlquilado(0); // Always start with 0 rented
                    if (caracteristica.getCantidadTotalVehiculo() == 0) {
                        caracteristica.setCantidadTotalVehiculo(1); // Default to 1 vehicle
                    }
                }

                // Primero guardar/actualizar la característica
                if (caracteristicaId == null || caracteristicaId.isEmpty()) {
                    CaracteristicaVehiculoDTO saved = caracteristicaService.alta(caracteristica);
                    caracId = saved.getId();
                    caracteristica.setId(caracId);
                } else {
                    caracteristicaService.modificar(caracteristicaId, caracteristica);
                    caracId = caracteristicaId;
                }

                // Asociar la característica al vehículo
                vehiculo.setCaracteristicaVehiculoId(caracId);
            }

            // Guardar/actualizar el vehículo
            if (isNewVehicle) {
                vehiculoService.alta(vehiculo);
            } else {
                vehiculoService.modificar(vehiculoId, vehiculo);
            }

            // Synchronize all characteristic counts after vehicle save/update
            sincronizarConteosCaracteristicas();

            ra.addFlashAttribute("success", "Vehículo guardado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/vehiculos";
    }

    @PostMapping("/vehiculos/{id}/eliminar")
    public String eliminarVehiculo(@PathVariable String id, RedirectAttributes ra) {
        try {
            // Get vehicle info before deletion to update characteristic count
            VehiculoDTO vehiculoAEliminar = vehiculoService.obtener(id).orElse(null);
            String caracteristicaId = null;

            if (vehiculoAEliminar != null && vehiculoAEliminar.getCaracteristica() != null) {
                caracteristicaId = vehiculoAEliminar.getCaracteristica().getId();
            }

            vehiculoService.baja(id);

            // Synchronize all characteristic counts after vehicle deletion
            sincronizarConteosCaracteristicas();

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

    private List<CaracteristicaVehiculoDTO> cargarCaracteristicasExistentes(Model model) {
        try {
            return caracteristicaService.listarActivos();
        } catch (ErrorServiceException e) {
            appendError(model, "No se pudo cargar la lista de características: " + e.getMessage());
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
        // Don't override the characteristic ID from the form
        // The characteristic ID should come from the caracteristicaForm, not from vehiculo
        if (vehiculo.getCaracteristica() != null && vehiculo.getCaracteristica().getId() != null) {
            String caracteristicaId = sanitizeIdentifier(vehiculo.getCaracteristica().getId());
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

        String trimmed = rawId.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // When multiple IDs arrive (e.g. "id1,id2"), keep the first well-formed UUID only.
        Matcher matcher = UUID_PATTERN.matcher(trimmed);
        if (matcher.find()) {
            return matcher.group();
        }

        String cleaned = trimmed.replaceAll("[^0-9a-fA-F\\-]", "");
        return cleaned.isEmpty() ? null : cleaned;
    }

    private void actualizarConteoCaracteristica(String caracteristicaId, int cambioTotal, int cambioAlquilado) {
        try {
            // Get current characteristic
            CaracteristicaVehiculoDTO caracteristica = caracteristicaService.obtener(caracteristicaId).orElse(null);
            if (caracteristica != null) {
                // Update counts
                int nuevoTotal = Math.max(0, caracteristica.getCantidadTotalVehiculo() + cambioTotal);
                int nuevoAlquilado = Math.max(0, Math.min(nuevoTotal, caracteristica.getCantidadVehiculoAlquilado() + cambioAlquilado));

                caracteristica.setCantidadTotalVehiculo(nuevoTotal);
                caracteristica.setCantidadVehiculoAlquilado(nuevoAlquilado);

                // Save updated characteristic
                caracteristicaService.modificar(caracteristicaId, caracteristica);
            }
        } catch (ErrorServiceException e) {
            // Log error but don't fail the main operation
            System.err.println("Error actualizando conteo de característica: " + e.getMessage());
        }
    }

    private void sincronizarConteosCaracteristicas() {
        try {
            // Get all vehicles and characteristics
            List<VehiculoDTO> vehiculos = vehiculoService.listarActivos();
            List<CaracteristicaVehiculoDTO> caracteristicas = caracteristicaService.listarActivos();

            // Count vehicles per characteristic
            Map<String, Integer> conteoPorCaracteristica = new HashMap<>();
            Map<String, Integer> alquiladosPorCaracteristica = new HashMap<>();

            // Initialize counts
            for (CaracteristicaVehiculoDTO carac : caracteristicas) {
                conteoPorCaracteristica.put(carac.getId(), 0);
                alquiladosPorCaracteristica.put(carac.getId(), 0);
            }

            // Count actual vehicles
            for (VehiculoDTO vehiculo : vehiculos) {
                String caracteristicaId = vehiculo.getCaracteristicaVehiculoId();
                if (caracteristicaId != null && conteoPorCaracteristica.containsKey(caracteristicaId)) {
                    int currentCount = conteoPorCaracteristica.get(caracteristicaId);
                    conteoPorCaracteristica.put(caracteristicaId, currentCount + 1);

                    // Count rented vehicles
                    if ("ALQUILADO".equals(vehiculo.getEstadoVehiculo().name())) {
                        int currentAlquilados = alquiladosPorCaracteristica.get(caracteristicaId);
                        alquiladosPorCaracteristica.put(caracteristicaId, currentAlquilados + 1);
                    }
                }
            }

            // Update characteristics with correct counts
            for (CaracteristicaVehiculoDTO carac : caracteristicas) {
                String id = carac.getId();
                int nuevoTotal = conteoPorCaracteristica.getOrDefault(id, 0);
                int nuevoAlquilado = alquiladosPorCaracteristica.getOrDefault(id, 0);

                // Only update if counts are different
                if (carac.getCantidadTotalVehiculo() != nuevoTotal ||
                    carac.getCantidadVehiculoAlquilado() != nuevoAlquilado) {

                    carac.setCantidadTotalVehiculo(nuevoTotal);
                    carac.setCantidadVehiculoAlquilado(nuevoAlquilado);
                    caracteristicaService.modificar(id, carac);
                }
            }
        } catch (ErrorServiceException e) {
            // Log error but don't fail the main operation
            System.err.println("Error sincronizando conteos de características: " + e.getMessage());
        }
    }

    private List<String> resolveImagenIdsForSave(String rawImagenId, String caracteristicaId) {
        String sanitizedImageId = sanitizeIdentifier(rawImagenId);
        if (sanitizedImageId != null) {
            return new ArrayList<>(List.of(sanitizedImageId));
        }

        if (caracteristicaId == null || caracteristicaId.isBlank()) {
            return new ArrayList<>();
        }

        try {
            return caracteristicaService.obtener(caracteristicaId)
                    .map(CaracteristicaVehiculoDTO::getImagenIds)
                    .map(ArrayList::new)
                    .orElseGet(ArrayList::new);
        } catch (ErrorServiceException e) {
            System.err.println("No se pudieron cargar las imágenes existentes de la característica "
                    + caracteristicaId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
