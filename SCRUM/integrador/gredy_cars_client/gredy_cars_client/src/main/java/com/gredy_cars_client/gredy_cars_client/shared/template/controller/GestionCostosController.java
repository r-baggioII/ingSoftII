package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CaracteristicaVehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CaracteristicaVehiculoMinDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CostoVehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.CaracteristicaVehiculoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.CostoVehiculoService;

@Controller
@RequestMapping("/gestion")
public class GestionCostosController {

    private static final Comparator<LocalDate> DATE_DESC = Comparator.nullsLast(Comparator.reverseOrder());

    private final CostoVehiculoService costoService;
    private final CaracteristicaVehiculoService caracteristicaService;

    public GestionCostosController(
            CostoVehiculoService costoService,
            CaracteristicaVehiculoService caracteristicaService) {
        this.costoService = costoService;
        this.caracteristicaService = caracteristicaService;
    }

    @GetMapping("/costos")
    public String gestionarCostos(
            @RequestParam(value = "editId", required = false) String editId,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        String sanitizedEditId = sanitizeIdentifier(editId);

        if (!model.containsAttribute("costoForm")) {
            model.addAttribute("costoForm", cargarCostoForm(sanitizedEditId, model));
        }

        List<CaracteristicaVehiculoDTO> caracteristicas = cargarCaracteristicas(model);
        List<CostoVehiculoDTO> costos = cargarCostos(model, caracteristicas);
        List<CostoVehiculoDTO> costosFiltrados = filtrarCostos(costos, search);

        model.addAttribute("caracteristicas", caracteristicas);
        model.addAttribute("costos", costosFiltrados);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("editId", sanitizedEditId);

        CostoVehiculoDTO form = (CostoVehiculoDTO) model.asMap().get("costoForm");
        boolean editMode = form != null && StringUtils.hasText(form.getId());
        model.addAttribute("editMode", editMode);

        return "gestion/gestion-costos";
    }

    @PostMapping("/costos")
    public String guardarCosto(@ModelAttribute("costoForm") CostoVehiculoDTO costo,
                               RedirectAttributes ra) {
        try {
            sanitizeCosto(costo);
            validarCosto(costo);

            boolean esNuevo = !StringUtils.hasText(costo.getId());
            if (esNuevo) {
                costoService.alta(costo);
                ra.addFlashAttribute("success", "Costo creado correctamente");
            } else {
                costoService.modificar(costo.getId(), costo);
                ra.addFlashAttribute("success", "Costo actualizado correctamente");
            }
        } catch (ErrorServiceException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("costoForm", costo);
            if (StringUtils.hasText(costo.getId())) {
                ra.addAttribute("editId", costo.getId());
            }
        }
        return "redirect:/gestion/costos";
    }

    @PostMapping("/costos/{id}/eliminar")
    public String eliminarCosto(@PathVariable String id, RedirectAttributes ra) {
        String sanitizedId = sanitizeIdentifier(id);
        if (!StringUtils.hasText(sanitizedId)) {
            ra.addFlashAttribute("error", "El identificador del costo es inválido");
            return "redirect:/gestion/costos";
        }
        try {
            costoService.baja(sanitizedId);
            ra.addFlashAttribute("success", "Costo eliminado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/costos";
    }

    private List<CaracteristicaVehiculoDTO> cargarCaracteristicas(Model model) {
        System.err.println("=== INICIO cargarCaracteristicas ===");
        try {
            System.err.println("Llamando a caracteristicaService.listarActivos()...");
            List<CaracteristicaVehiculoDTO> result = caracteristicaService.listarActivos();
            System.err.println("cargarCaracteristicas result: " + (result != null ? result.size() : 0) + " elementos");
            System.err.println("=== FIN cargarCaracteristicas ===");
            return result;
        } catch (ErrorServiceException e) {
            System.err.println("ErrorServiceException en cargarCaracteristicas: " + e.getMessage());
            appendError(model, "No se pudo cargar la lista de características: " + e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Exception en cargarCaracteristicas: " + e.getMessage());
            e.printStackTrace();
            appendError(model, "Error inesperado en características: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<CostoVehiculoDTO> cargarCostos(Model model, List<CaracteristicaVehiculoDTO> caracteristicas) {
        System.err.println("=== INICIO cargarCostos ===");
        try {
            System.err.println("Llamando a costoService.listarActivos()...");
            List<CostoVehiculoDTO> costos = costoService.listarActivos();
            System.err.println("costoService.listarActivos() retornó: " + (costos != null ? costos.size() : "null") + " costos");

            if (costos != null) {
                System.err.println("Detalle de costos recibidos:");
                costos.forEach(c -> {
                    System.err.println("  - ID: " + c.getId());
                    System.err.println("    Costo: " + c.getCosto());
                    System.err.println("    FechaDesde: " + c.getFechaDesde());
                    System.err.println("    CaracteristicaID: " + c.getIdCaracteristicaVehiculo());
                    System.err.println("    Eliminado: " + c.getEliminado());
                });

                System.err.println("Ordenando costos...");
                costos.sort(Comparator
                        .comparing(CostoVehiculoDTO::getFechaDesde, DATE_DESC)
                        .thenComparing(CostoVehiculoDTO::getFechaHasta, DATE_DESC)
                        .thenComparing(CostoVehiculoDTO::getId, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

                System.err.println("Costos ordenados, ahora hidratando características...");
                Map<String, CaracteristicaVehiculoDTO> index = caracteristicas == null
                        ? Collections.emptyMap()
                        : caracteristicas.stream()
                            .filter(Objects::nonNull)
                            .filter(car -> StringUtils.hasText(car.getId()))
                            .collect(Collectors.toMap(CaracteristicaVehiculoDTO::getId, car -> car, (a, b) -> a));

                System.err.println("Índice de características creado con " + index.size() + " elementos");
                costos.forEach(costo -> hidratarCaracteristica(costo, index));
                System.err.println("Hidratación completada");
            }

            System.err.println("=== FIN cargarCostos ===");
            return costos != null ? costos : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("EXCEPTION en cargarCostos: " + e.getMessage());
            e.printStackTrace();
            appendError(model, "Error al cargar costos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private CostoVehiculoDTO cargarCostoForm(String editId, Model model) {
        if (!StringUtils.hasText(editId)) {
            return new CostoVehiculoDTO();
        }
        try {
            return costoService.obtener(editId).orElseGet(CostoVehiculoDTO::new);
        } catch (ErrorServiceException e) {
            appendError(model, "No se pudo obtener el costo seleccionado: " + e.getMessage());
            return new CostoVehiculoDTO();
        }
    }

    private void sanitizeCosto(CostoVehiculoDTO costo) {
        if (costo == null) {
            return;
        }
        costo.setId(sanitizeIdentifier(costo.getId()));

        String caracteristicaId = sanitizeIdentifier(costo.getIdCaracteristicaVehiculo());
        costo.setIdCaracteristicaVehiculo(caracteristicaId);

        if (StringUtils.hasText(caracteristicaId)) {
            if (costo.getCaracteristica() == null) {
                costo.setCaracteristica(new CaracteristicaVehiculoMinDTO());
            }
            costo.getCaracteristica().setId(caracteristicaId);
        } else if (costo.getCaracteristica() != null) {
            costo.getCaracteristica().setId(null);
        }
    }

    private void validarCosto(CostoVehiculoDTO costo) {
        if (costo == null) {
            throw new IllegalArgumentException("Debe completar el formulario de costos");
        }
        if (costo.getFechaDesde() == null || costo.getFechaHasta() == null) {
            throw new IllegalArgumentException("Las fechas desde y hasta son obligatorias");
        }
        if (costo.getFechaHasta().isBefore(costo.getFechaDesde())) {
            throw new IllegalArgumentException("La fecha Hasta no puede ser anterior a la fecha Desde");
        }
        if (costo.getCosto() <= 0) {
            throw new IllegalArgumentException("El costo debe ser mayor a 0");
        }
        if (!StringUtils.hasText(costo.getIdCaracteristicaVehiculo())) {
            throw new IllegalArgumentException("Debe seleccionar una característica de vehículo");
        }
    }

    private void hidratarCaracteristica(CostoVehiculoDTO costo, Map<String, CaracteristicaVehiculoDTO> index) {
        if (costo == null || index == null || index.isEmpty()) {
            return;
        }
        String caracteristicaId = sanitizeIdentifier(costo.getIdCaracteristicaVehiculo());
        if (!StringUtils.hasText(caracteristicaId)) {
            return;
        }
        if (costo.getCaracteristica() != null && StringUtils.hasText(costo.getCaracteristica().getId())) {
            return;
        }
        CaracteristicaVehiculoDTO origen = index.get(caracteristicaId);
        if (origen == null) {
            return;
        }
        costo.setCaracteristica(convertirAMinima(origen));
    }

    private CaracteristicaVehiculoMinDTO convertirAMinima(CaracteristicaVehiculoDTO origen) {
        if (origen == null) {
            return null;
        }
        CaracteristicaVehiculoMinDTO min = new CaracteristicaVehiculoMinDTO();
        min.setId(origen.getId());
        min.setMarca(origen.getMarca());
        min.setModelo(origen.getModelo());
        min.setCantidadPuerta(origen.getCantidadPuerta());
        min.setCantidadAsiento(origen.getCantidadAsiento());
        min.setAnio(origen.getAnio());
        min.setCantidadTotalVehiculo(origen.getCantidadTotalVehiculo());
        min.setCantidadVehiculoAlquilado(origen.getCantidadVehiculoAlquilado());
        return min;
    }

    private List<CostoVehiculoDTO> filtrarCostos(List<CostoVehiculoDTO> costos, String search) {
        System.err.println("=== INICIO filtrarCostos ===");
        System.err.println("Costos recibidos: " + (costos != null ? costos.size() : "null"));
        System.err.println("Search term: '" + search + "'");

        if (costos == null) {
            System.err.println("costos es null, retornando empty list");
            return Collections.emptyList();
        }
        if (!StringUtils.hasText(search)) {
            System.err.println("No hay search term, retornando todos los costos");
            return costos;
        }

        String needle = search.trim().toLowerCase(Locale.ROOT);
        System.err.println("Needle para búsqueda: '" + needle + "'");

        List<CostoVehiculoDTO> filtrados = costos.stream()
                .filter(costo -> {
                    boolean matches = matchesBusqueda(costo, needle);
                    System.err.println("  - Costo " + costo.getId() + " matches: " + matches);
                    return matches;
                })
                .collect(Collectors.toList());

        System.err.println("Costos filtrados: " + filtrados.size());
        System.err.println("=== FIN filtrarCostos ===");
        return filtrados;
    }

    private boolean matchesBusqueda(CostoVehiculoDTO costo, String needle) {
        if (costo == null) {
            return false;
        }
        return Stream.of(
                    costo.getId(),
                    costo.getIdCaracteristicaVehiculo(),
                    formatDate(costo.getFechaDesde()),
                    formatDate(costo.getFechaHasta()),
                    costo.getCaracteristica() != null ? costo.getCaracteristica().getMarca() : null,
                    costo.getCaracteristica() != null ? costo.getCaracteristica().getModelo() : null
                )
                .filter(StringUtils::hasText)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(needle));
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : "";
    }

    private void appendError(Model model, String message) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        Object existing = model.asMap().get("error");
        if (existing == null) {
            model.addAttribute("error", message);
        } else if (existing instanceof String existingMessage && StringUtils.hasText(existingMessage)) {
            model.addAttribute("error", existingMessage + " | " + message);
        } else {
            model.addAttribute("error", message);
        }
    }

    private String sanitizeIdentifier(String rawId) {
        if (rawId == null) {
            return null;
        }
        String trimmed = rawId.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
