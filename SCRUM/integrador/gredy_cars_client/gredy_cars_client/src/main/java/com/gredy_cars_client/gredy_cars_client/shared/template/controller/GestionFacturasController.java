package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DetalleFacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FormaDePagoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.EstadoFactura;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoPago;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.FacturaService;

/**
 * Controlador MVC para la gestión de facturas desde la interfaz administrativa.
 */
@Controller
@RequestMapping("/gestion")
public class GestionFacturasController {

    private final FacturaService facturaService;

    public GestionFacturasController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping("/facturas")
    public String gestionarFacturas(
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "facturaId", required = false) String facturaSeleccionada,
        Model model
    ) throws ErrorServiceException {
        cargarPantallaFacturas(model, estado, editId, facturaSeleccionada, true, true);
        return "gestion/gestion-facturas";
    }

    @GetMapping("/facturas/{id}/detalles")
    public String verDetallesFactura(
        @PathVariable String id,
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        Model model
    ) throws ErrorServiceException {
        cargarPantallaFacturas(model, estado, editId, id, true, true);
        return "gestion/gestion-facturas";
    }

    @GetMapping("/facturas/{id}/formas-pago")
    public String verFormasPago(
        @PathVariable String id,
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        Model model
    ) throws ErrorServiceException {
        cargarPantallaFacturas(model, estado, editId, id, true, true);
        return "gestion/gestion-facturas";
    }

    @PostMapping("/facturas")
    public String guardarFactura(
        @ModelAttribute("facturaForm") FacturaDTO facturaDTO,
        RedirectAttributes ra
    ) {
        try {
            if (!StringUtils.hasText(facturaDTO.getId())) {
                facturaService.crear(facturaDTO);
                ra.addFlashAttribute("success", "Factura creada correctamente");
            } else {
                if (facturaService.modificar(facturaDTO.getId(), facturaDTO).isPresent()) {
                    ra.addFlashAttribute("success", "Factura actualizada correctamente");
                } else {
                    ra.addFlashAttribute("error", "No se encontró la factura a actualizar");
                }
            }
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("facturaForm", facturaDTO);
        }
        return "redirect:/gestion/facturas";
    }

    @PostMapping("/facturas/{id}/eliminar")
    public String eliminarFactura(@PathVariable String id, RedirectAttributes ra) {
        try {
            facturaService.eliminar(id);
            ra.addFlashAttribute("success", "Factura eliminada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/facturas";
    }

    private void cargarPantallaFacturas(
        Model model,
        String estado,
        String editId,
        String facturaSeleccionadaId,
        boolean cargarDetalles,
        boolean cargarFormas
    ) throws ErrorServiceException {
        List<FacturaDTO> facturas = StringUtils.hasText(estado)
            ? facturaService.listarPorEstado(estado)
            : facturaService.listar();
        model.addAttribute("facturas", facturas);
        model.addAttribute("estadosFactura", EstadoFactura.values());
        model.addAttribute("tiposPago", TipoPago.values());
        model.addAttribute("estadoSeleccionado", estado);

        prepararFormulario(model, editId);
        cargarSeccionesSeleccionadas(model, facturaSeleccionadaId, cargarDetalles, cargarFormas);
    }

    private void prepararFormulario(Model model, String editId) throws ErrorServiceException {
        FacturaDTO formulario;
        Map<String, Object> atributos = model.asMap();
        if (model.containsAttribute("facturaForm")) {
            formulario = (FacturaDTO) atributos.get("facturaForm");
        } else if (StringUtils.hasText(editId)) {
            formulario = facturaService.buscar(editId).orElseGet(FacturaDTO::new);
        } else {
            formulario = new FacturaDTO();
        }
        asegurarColecciones(formulario);
        model.addAttribute("facturaForm", formulario);
    }

    private void cargarSeccionesSeleccionadas(
        Model model,
        String facturaSeleccionadaId,
        boolean cargarDetalles,
        boolean cargarFormas
    ) throws ErrorServiceException {
        model.addAttribute("facturaSeleccionadaId", facturaSeleccionadaId);

        if (cargarDetalles && StringUtils.hasText(facturaSeleccionadaId)) {
            model.addAttribute("detallesSeleccionados", facturaService.listarDetalles(facturaSeleccionadaId));
        } else {
            model.addAttribute("detallesSeleccionados", Collections.emptyList());
        }

        if (cargarFormas && StringUtils.hasText(facturaSeleccionadaId)) {
            model.addAttribute("formasPagoSeleccionadas", facturaService.listarFormasPago(facturaSeleccionadaId));
        } else {
            model.addAttribute("formasPagoSeleccionadas", Collections.emptyList());
        }
    }

    private void asegurarColecciones(FacturaDTO factura) {
        if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            List<DetalleFacturaDTO> detalles = new ArrayList<>();
            detalles.add(new DetalleFacturaDTO());
            factura.setDetalles(detalles);
        }
        if (factura.getFormasPago() == null || factura.getFormasPago().isEmpty()) {
            List<FormaDePagoDTO> formas = new ArrayList<>();
            formas.add(new FormaDePagoDTO());
            factura.setFormasPago(formas);
        }
    }
}
