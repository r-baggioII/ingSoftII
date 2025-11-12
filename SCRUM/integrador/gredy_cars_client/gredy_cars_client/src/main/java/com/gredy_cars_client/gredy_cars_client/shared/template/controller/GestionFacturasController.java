package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.gredy_cars_client.gredy_cars_client.config.AuthCheckInterceptor.UserDetailsWithRole;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DetalleFacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FormaDePagoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.EstadoFactura;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoPago;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ClienteService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.FacturaService;

/**
 * Controlador MVC para la gestión de facturas desde la interfaz administrativa.
 */
@Controller
@RequestMapping("/gestion")
public class GestionFacturasController {

    private final FacturaService facturaService;
    private final ClienteService clienteService;

    public GestionFacturasController(FacturaService facturaService, ClienteService clienteService) {
        this.facturaService = facturaService;
        this.clienteService = clienteService;
    }

    @GetMapping("/facturas")
    public String gestionarFacturas(
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "facturaId", required = false) String facturaSeleccionada,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        Model model
    ) {
        // Get user role from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioRol = "CLIENTE";
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsWithRole) {
            UserDetailsWithRole userDetails = (UserDetailsWithRole) authentication.getPrincipal();
            usuarioRol = userDetails.getRol();
        }
        model.addAttribute("usuarioRol", usuarioRol);
        
        cargarPantallaFacturas(model, estado, editId, facturaSeleccionada, clienteId, true, true);
        return "gestion/gestion-facturas";
    }

    @GetMapping("/facturas/{id}/detalles")
    public String verDetallesFactura(
        @PathVariable String id,
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        Model model
    ) {
        cargarPantallaFacturas(model, estado, editId, id, clienteId, true, true);
        return "gestion/gestion-facturas";
    }

    @GetMapping("/facturas/{id}/formas-pago")
    public String verFormasPago(
        @PathVariable String id,
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        Model model
    ) {
        cargarPantallaFacturas(model, estado, editId, id, clienteId, true, true);
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
        return redirigirConCliente(facturaDTO.getClienteId());
    }

    @PostMapping("/facturas/{id}/eliminar")
    public String eliminarFactura(
        @PathVariable String id,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        RedirectAttributes ra
    ) {
        try {
            facturaService.eliminar(id);
            ra.addFlashAttribute("success", "Factura eliminada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return redirigirConCliente(clienteId);
    }

    private void cargarPantallaFacturas(
        Model model,
        String estado,
        String editId,
        String facturaSeleccionadaId,
        String clienteId,
        boolean cargarDetalles,
        boolean cargarFormas
    ) {
        List<FacturaDTO> facturas;
        try {
            facturas = StringUtils.hasText(estado)
                ? facturaService.listarPorEstado(estado)
                : facturaService.listar();
        } catch (ErrorServiceException e) {
            facturas = Collections.emptyList();
            model.addAttribute("errorFacturas", "No se pudieron obtener las facturas: " + e.getMessage());
        }
        model.addAttribute("facturas", facturas);
        model.addAttribute("estadosFactura", EstadoFactura.values());
        model.addAttribute("tiposPago", TipoPago.values());
        List<ClienteDTO> clientes;
        try {
            clientes = clienteService.listarActivos();
        } catch (ErrorServiceException e) {
            clientes = Collections.emptyList();
            model.addAttribute("errorClientes", "No se pudieron obtener los clientes: " + e.getMessage());
        }
        model.addAttribute("clientes", clientes);
        model.addAttribute("estadoSeleccionado", estado);

        FacturaDTO formulario = prepararFormulario(model, editId, clienteId);
        String clienteSeleccionado = StringUtils.hasText(clienteId)
            ? clienteId
            : formulario.getClienteId();
        model.addAttribute("clienteSeleccionadoId", clienteSeleccionado);
        String clienteSeleccionadoNombre = null;
        if (StringUtils.hasText(clienteSeleccionado)) {
            clienteSeleccionadoNombre = clientes.stream()
                .filter(cliente -> cliente.getId().equals(clienteSeleccionado))
                .map(cliente -> cliente.getNombre() + " " + cliente.getApellido())
                .findFirst()
                .orElse(null);
        }
        model.addAttribute("clienteSeleccionadoNombre", clienteSeleccionadoNombre);

        // TODO FRONT: agregar acción (botón/JS) que llame a /api/pagos/mp/preferencia y redirija al initPoint para pagos con billetera virtual.
        cargarSeccionesSeleccionadas(model, facturaSeleccionadaId, cargarDetalles, cargarFormas);
    }

    private FacturaDTO prepararFormulario(Model model, String editId, String clienteId) {
        FacturaDTO formulario;
        Map<String, Object> atributos = model.asMap();
        boolean formularioDesdeFlash = model.containsAttribute("facturaForm");
        if (formularioDesdeFlash) {
            formulario = (FacturaDTO) atributos.get("facturaForm");
        } else if (StringUtils.hasText(editId)) {
            try {
                formulario = facturaService.buscar(editId).orElseGet(FacturaDTO::new);
            } catch (ErrorServiceException e) {
                formulario = new FacturaDTO();
                model.addAttribute("errorFacturaForm", "No se pudo cargar la factura a editar: " + e.getMessage());
            }
        } else {
            formulario = new FacturaDTO();
        }

        boolean esFacturaNueva = !StringUtils.hasText(formulario.getId());
        String clienteParaConsulta = StringUtils.hasText(clienteId)
            ? clienteId
            : formulario.getClienteId();

        List<AlquilerDTO> alquileresPendientes = Collections.emptyList();
        if (StringUtils.hasText(clienteParaConsulta)) {
            alquileresPendientes = clienteService.listarAlquileresPendientesFactura(clienteParaConsulta);
        }

        if (esFacturaNueva && !formularioDesdeFlash && !alquileresPendientes.isEmpty()) {
            formulario.setClienteId(clienteParaConsulta);
            formulario.setDetalles(construirDetallesDesdeAlquileres(alquileresPendientes));
        } else if (esFacturaNueva && StringUtils.hasText(clienteId) && !formularioDesdeFlash) {
            formulario.setClienteId(clienteId);
        }

        asegurarColecciones(formulario);
        model.addAttribute("facturaForm", formulario);
        model.addAttribute("alquileresPendientes", alquileresPendientes);
        Map<String, AlquilerDTO> mapaPendientes = alquileresPendientes.isEmpty()
            ? Collections.emptyMap()
            : alquileresPendientes.stream()
                .filter(alquiler -> StringUtils.hasText(alquiler.getId()))
                .collect(Collectors.toMap(AlquilerDTO::getId, Function.identity(), (a, b) -> a));
        model.addAttribute("alquileresPendientesMap", mapaPendientes);
        return formulario;
    }

    private void cargarSeccionesSeleccionadas(
        Model model,
        String facturaSeleccionadaId,
        boolean cargarDetalles,
        boolean cargarFormas
    ) {
        model.addAttribute("facturaSeleccionadaId", facturaSeleccionadaId);

        if (cargarDetalles && StringUtils.hasText(facturaSeleccionadaId)) {
            try {
                model.addAttribute("detallesSeleccionados", facturaService.listarDetalles(facturaSeleccionadaId));
            } catch (ErrorServiceException e) {
                model.addAttribute("detallesSeleccionados", Collections.emptyList());
                model.addAttribute("errorDetalles", "No se pudieron cargar los detalles de la factura seleccionada: " + e.getMessage());
            }
        } else {
            model.addAttribute("detallesSeleccionados", Collections.emptyList());
        }

        if (cargarFormas && StringUtils.hasText(facturaSeleccionadaId)) {
            try {
                model.addAttribute("formasPagoSeleccionadas", facturaService.listarFormasPago(facturaSeleccionadaId));
            } catch (ErrorServiceException e) {
                model.addAttribute("formasPagoSeleccionadas", Collections.emptyList());
                model.addAttribute("errorFormasPago", "No se pudieron cargar las formas de pago de la factura seleccionada: " + e.getMessage());
            }
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

    private List<DetalleFacturaDTO> construirDetallesDesdeAlquileres(List<AlquilerDTO> alquileres) {
        List<DetalleFacturaDTO> detalles = new ArrayList<>();
        for (AlquilerDTO alquiler : alquileres) {
            if (alquiler == null || !StringUtils.hasText(alquiler.getId())) {
                continue;
            }
            DetalleFacturaDTO detalle = new DetalleFacturaDTO();
            detalle.setAlquilerId(alquiler.getId());
            detalle.setCantidad(1);
            detalle.setSubtotal(null);
            detalles.add(detalle);
        }
        if (detalles.isEmpty()) {
            detalles.add(new DetalleFacturaDTO());
        }
        return detalles;
    }

    private String redirigirConCliente(String clienteId) {
        return StringUtils.hasText(clienteId)
            ? "redirect:/gestion/facturas?clienteId=" + clienteId
            : "redirect:/gestion/facturas";
    }
}
