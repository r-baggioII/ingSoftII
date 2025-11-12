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
public class GestionFacturasController extends BaseThymeleafController<FacturaDTO, String> {

    private final FacturaService facturaService;
    private final ClienteService clienteService;

    public GestionFacturasController(FacturaService facturaService, ClienteService clienteService) {
        super(facturaService);
        this.facturaService = facturaService;
        this.clienteService = clienteService;
    }

    @Override
    protected String getListView() {
        return "gestion/gestion-facturas";
    }

    @Override
    protected String getFormView() {
        return "gestion/gestion-facturas";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/gestion/facturas";
    }

    @Override
    protected String getListModelAttribute() {
        return "facturas";
    }

    @Override
    protected String getFormModelAttribute() {
        return "facturaForm";
    }

    @Override
    protected String getEntityLabel() {
        return "Factura";
    }

    @Override
    protected FacturaDTO buildNewInstance() {
        FacturaDTO nueva = new FacturaDTO();
        asegurarColecciones(nueva);
        return nueva;
    }

    @Override
    protected void populateCollections(Model model) {
        addUsuarioRol(model);
        addStaticCatalogs(model);
        ensureClientes(model);
    }

    @GetMapping("/facturas")
    public String gestionarFacturas(
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "facturaId", required = false) String facturaSeleccionada,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        Model model
    ) {
        addUsuarioRol(model);
        FacturaViewState viewState = FacturaViewState.of(estado, editId, facturaSeleccionada, clienteId);
        prepareFacturaScreen(model, viewState, true, true);
        return getListView();
    }

    @GetMapping("/facturas/{id}/detalles")
    public String verDetallesFactura(
        @PathVariable String id,
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        Model model
    ) {
        addUsuarioRol(model);
        FacturaViewState viewState = FacturaViewState.of(estado, editId, id, clienteId);
        prepareFacturaScreen(model, viewState, true, true);
        return getListView();
    }

    @GetMapping("/facturas/{id}/formas-pago")
    public String verFormasPago(
        @PathVariable String id,
        @RequestParam(value = "estado", required = false) String estado,
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        Model model
    ) {
        addUsuarioRol(model);
        FacturaViewState viewState = FacturaViewState.of(estado, editId, id, clienteId);
        prepareFacturaScreen(model, viewState, true, true);
        return getListView();
    }

    @PostMapping("/facturas")
    public String crearFactura(
        @ModelAttribute("facturaForm") FacturaDTO facturaDTO,
        @RequestParam(value = "estadoFiltro", required = false) String estadoFiltro,
        @RequestParam(value = "facturaSeleccionadaId", required = false) String facturaSeleccionadaId,
        @RequestParam(value = "clienteSeleccionadoId", required = false) String clienteSeleccionadoId,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        FacturaViewState viewState = FacturaViewState.of(estadoFiltro, null, facturaSeleccionadaId, clienteSeleccionadoId);
        return processFacturaMutation(facturaDTO, viewState, model, redirectAttributes, false);
    }

    @PostMapping("/facturas/{id}")
    public String actualizarFactura(
        @PathVariable String id,
        @ModelAttribute("facturaForm") FacturaDTO facturaDTO,
        @RequestParam(value = "estadoFiltro", required = false) String estadoFiltro,
        @RequestParam(value = "facturaSeleccionadaId", required = false) String facturaSeleccionadaId,
        @RequestParam(value = "clienteSeleccionadoId", required = false) String clienteSeleccionadoId,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        facturaDTO.setId(id);
        FacturaViewState viewState = FacturaViewState.of(estadoFiltro, id, facturaSeleccionadaId, clienteSeleccionadoId);
        return processFacturaMutation(facturaDTO, viewState, model, redirectAttributes, true);
    }

    @PostMapping("/facturas/{id}/eliminar")
    public String eliminarFactura(
        @PathVariable String id,
        @RequestParam(value = "clienteId", required = false) String clienteId,
        RedirectAttributes redirectAttributes
    ) {
        String outcome = super.handleDelete(id, redirectAttributes);
        if (outcome.startsWith("redirect:/gestion/facturas") && StringUtils.hasText(clienteId)) {
            return buildRedirect(clienteId);
        }
        return outcome;
    }

    private String processFacturaMutation(
        FacturaDTO facturaDTO,
        FacturaViewState viewState,
        Model model,
        RedirectAttributes redirectAttributes,
        boolean isUpdate
    ) {
        addUsuarioRol(model);
        FacturaDTO sanitized = sanitizeFactura(facturaDTO);
        String outcome = isUpdate
            ? super.handleUpdate(sanitized.getId(), sanitized, model, redirectAttributes)
            : super.handleCreate(sanitized, model, redirectAttributes);

        FacturaViewState stateWithCliente = viewState.withClienteFallback(sanitized.getClienteId());

        if (outcome.startsWith("redirect:/")) {
            return buildRedirect(stateWithCliente.clienteId());
        }

        prepareFacturaScreen(model, stateWithCliente, true, true);
        return outcome;
    }

    private String buildRedirect(String clienteId) {
        return StringUtils.hasText(clienteId)
            ? "redirect:/gestion/facturas?clienteId=" + clienteId
            : getRedirectToList();
    }

    private void prepareFacturaScreen(Model model, FacturaViewState viewState, boolean cargarDetalles, boolean cargarFormas) {
        addStaticCatalogs(model);
        List<ClienteDTO> clientes = ensureClientes(model);
        loadFacturas(model, viewState.estado());

        model.addAttribute("estadoSeleccionado", viewState.estado());
        model.addAttribute("facturaSeleccionadaId", viewState.facturaSeleccionadaId());

        FacturaDTO formulario = prepararFormulario(model, viewState);
        String clienteSeleccionado = resolveClienteSeleccionado(formulario, viewState);
        model.addAttribute("clienteSeleccionadoId", clienteSeleccionado);
        model.addAttribute("clienteSeleccionadoNombre", buscarNombreCliente(clientes, clienteSeleccionado));

        cargarSeccionesSeleccionadas(model, viewState.facturaSeleccionadaId(), cargarDetalles, cargarFormas);
    }

    private FacturaDTO prepararFormulario(Model model, FacturaViewState viewState) {
        FacturaDTO formulario;
        Map<String, Object> atributos = model.asMap();
        boolean formularioDesdeModelo = atributos.containsKey(getFormModelAttribute());
        if (formularioDesdeModelo) {
            formulario = (FacturaDTO) atributos.get(getFormModelAttribute());
        } else if (StringUtils.hasText(viewState.editId())) {
            try {
                formulario = facturaService.buscar(viewState.editId()).orElseGet(FacturaDTO::new);
            } catch (ErrorServiceException e) {
                formulario = new FacturaDTO();
                model.addAttribute("errorFacturaForm", "No se pudo cargar la factura a editar: " + e.getMessage());
            }
        } else {
            formulario = buildNewInstance();
        }

        boolean esFacturaNueva = !StringUtils.hasText(formulario.getId());
        String clienteParaConsulta = StringUtils.hasText(viewState.clienteId())
            ? viewState.clienteId()
            : formulario.getClienteId();

        List<AlquilerDTO> alquileresPendientes = Collections.emptyList();
        if (StringUtils.hasText(clienteParaConsulta)) {
            alquileresPendientes = clienteService.listarAlquileresPendientesFactura(clienteParaConsulta);
        }

        if (esFacturaNueva && !formularioDesdeModelo && !alquileresPendientes.isEmpty()) {
            formulario.setClienteId(clienteParaConsulta);
            formulario.setDetalles(construirDetallesDesdeAlquileres(alquileresPendientes));
        } else if (esFacturaNueva && !formularioDesdeModelo && StringUtils.hasText(viewState.clienteId())) {
            formulario.setClienteId(viewState.clienteId());
        }

        asegurarColecciones(formulario);
        model.addAttribute(getFormModelAttribute(), formulario);
        model.addAttribute("alquileresPendientes", alquileresPendientes);
        model.addAttribute("alquileresPendientesMap", alquileresPendientes.isEmpty()
            ? Collections.emptyMap()
            : alquileresPendientes.stream()
                .filter(alquiler -> StringUtils.hasText(alquiler.getId()))
                .collect(Collectors.toMap(AlquilerDTO::getId, Function.identity(), (a, b) -> a)));
        return formulario;
    }

    private void cargarSeccionesSeleccionadas(
        Model model,
        String facturaSeleccionadaId,
        boolean cargarDetalles,
        boolean cargarFormas
    ) {
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

    private void loadFacturas(Model model, String estado) {
        try {
            List<FacturaDTO> facturas = StringUtils.hasText(estado)
                ? facturaService.listarPorEstado(estado)
                : facturaService.listar();
            model.addAttribute("facturas", facturas);
        } catch (ErrorServiceException e) {
            model.addAttribute("facturas", Collections.emptyList());
            model.addAttribute("errorFacturas", "No se pudieron obtener las facturas: " + e.getMessage());
        }
    }

    private List<ClienteDTO> ensureClientes(Model model) {
        Map<String, Object> atributos = model.asMap();
        if (atributos.containsKey("clientes")) {
            Object existentes = atributos.get("clientes");
            if (existentes instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<ClienteDTO> clientes = (List<ClienteDTO>) existentes;
                return clientes;
            }
        }
        try {
            List<ClienteDTO> clientes = clienteService.listarActivos();
            model.addAttribute("clientes", clientes);
            return clientes;
        } catch (ErrorServiceException e) {
            model.addAttribute("clientes", Collections.emptyList());
            model.addAttribute("errorClientes", "No se pudieron obtener los clientes: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private void addUsuarioRol(Model model) {
        if (model.asMap().containsKey("usuarioRol")) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioRol = "CLIENTE";
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsWithRole userDetails) {
            usuarioRol = userDetails.getRol();
        }
        model.addAttribute("usuarioRol", usuarioRol);
    }

    private void addStaticCatalogs(Model model) {
        model.addAttribute("estadosFactura", EstadoFactura.values());
        model.addAttribute("tiposPago", TipoPago.values());
    }

    private String resolveClienteSeleccionado(FacturaDTO formulario, FacturaViewState viewState) {
        if (StringUtils.hasText(viewState.clienteId())) {
            return viewState.clienteId();
        }
        return formulario.getClienteId();
    }

    private String buscarNombreCliente(List<ClienteDTO> clientes, String clienteId) {
        if (!StringUtils.hasText(clienteId)) {
            return null;
        }
        return clientes.stream()
            .filter(cliente -> clienteId.equals(cliente.getId()))
            .map(cliente -> cliente.getNombre() + " " + cliente.getApellido())
            .findFirst()
            .orElse(null);
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

    private FacturaDTO sanitizeFactura(FacturaDTO dto) {
        if (dto == null) {
            return buildNewInstance();
        }
        asegurarColecciones(dto);
        return dto;
    }

    private record FacturaViewState(String estado, String editId, String facturaSeleccionadaId, String clienteId) {

        static FacturaViewState of(String estado, String editId, String facturaSeleccionadaId, String clienteId) {
            return new FacturaViewState(
                normalize(estado),
                normalize(editId),
                normalize(facturaSeleccionadaId),
                normalize(clienteId)
            );
        }

        FacturaViewState withClienteFallback(String fallback) {
            return StringUtils.hasText(clienteId)
                ? this
                : new FacturaViewState(estado, editId, facturaSeleccionadaId, normalize(fallback));
        }

        private static String normalize(String value) {
            return StringUtils.hasText(value) ? value : null;
        }
    }
}
