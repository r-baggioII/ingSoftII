package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.config.AuthCheckInterceptor.UserDetailsWithRole;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PromocionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ClienteService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.PromocionService;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequestMapping("/gestion")
public class GestionPromocionesController {

    private static final Logger log = LoggerFactory.getLogger(GestionPromocionesController.class);

    private final PromocionService promocionService;
    private final ClienteService clienteService;

    public GestionPromocionesController(PromocionService promocionService, ClienteService clienteService) {
        this.promocionService = promocionService;
        this.clienteService = clienteService;
    }

    @GetMapping("/promociones")
    public String gestionarPromociones(
            @RequestParam(value = "editId", required = false) String editId,
            Model model
    ) {
        String usuarioRol = obtenerRolActual();
        model.addAttribute("usuarioRol", usuarioRol);

        if (!"JEFE".equalsIgnoreCase(usuarioRol)) {
            model.addAttribute("error", "Solo un Jefe puede gestionar promociones");
            inicializarModeloVacio(model);
            return "gestion/gestion-promociones";
        }

        try {
            List<PromocionDTO> promociones = promocionService.listarActivos();
            List<PromocionDTO> vigentes = promocionService.listarVigentes();
            List<ClienteDTO> clientes = clienteService.listarActivos();
            Map<String, String> clientesMap = clientes.stream()
                    .filter(cliente -> cliente != null && StringUtils.hasText(cliente.getId()))
                    .collect(Collectors.toMap(
                            ClienteDTO::getId,
                            this::nombreClienteSeguro,
                            (existing, ignored) -> existing,
                            LinkedHashMap::new
                    ));

            if (!model.containsAttribute("promocionForm")) {
                PromocionDTO form = cargarPromocion(editId);
                model.addAttribute("promocionForm", form);
                model.addAttribute("editando", StringUtils.hasText(form.getId()));
            } else {
                PromocionDTO form = (PromocionDTO) model.asMap().get("promocionForm");
                model.addAttribute("editando", form != null && StringUtils.hasText(form.getId()));
            }

            model.addAttribute("promociones", promociones);
            model.addAttribute("promocionesVigentes", vigentes);
            model.addAttribute("clientes", clientes);
            model.addAttribute("clientesMap", clientesMap);
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            inicializarModeloVacio(model);
        }

        if (!model.containsAttribute("promocionForm")) {
            model.addAttribute("promocionForm", new PromocionDTO());
            model.addAttribute("editando", false);
        }

        return "gestion/gestion-promociones";
    }

    @PostMapping("/promociones")
    public String guardarPromocion(@ModelAttribute("promocionForm") PromocionDTO promocion,
                                   RedirectAttributes ra) {
        if (!"JEFE".equalsIgnoreCase(obtenerRolActual())) {
            ra.addFlashAttribute("error", "No cuenta con permisos para gestionar promociones");
            return "redirect:/gestion/promociones";
        }
        try {
            if (StringUtils.hasText(promocion.getId())) {
                promocionService.modificar(promocion.getId(), promocion);
                ra.addFlashAttribute("success", "Promoción actualizada correctamente");
            } else {
                promocionService.alta(promocion);
                ra.addFlashAttribute("success", "Promoción creada correctamente");
            }
        } catch (ErrorServiceException e) {
            log.warn("Fallo en alta/modificación de promoción: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("promocionForm", promocion);
            if (StringUtils.hasText(promocion.getId())) {
                return "redirect:/gestion/promociones?editId=" + promocion.getId();
            }
            return "redirect:/gestion/promociones";
        }
        return "redirect:/gestion/promociones";
    }

    @PostMapping("/promociones/{id}/eliminar")
    public String eliminarPromocion(@PathVariable String id, RedirectAttributes ra) {
        if (!"JEFE".equalsIgnoreCase(obtenerRolActual())) {
            ra.addFlashAttribute("error", "No cuenta con permisos para gestionar promociones");
            return "redirect:/gestion/promociones";
        }
        try {
            promocionService.baja(id);
            ra.addFlashAttribute("success", "Promoción eliminada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/promociones";
    }

    private void inicializarModeloVacio(Model model) {
        model.addAttribute("promociones", Collections.emptyList());
        model.addAttribute("promocionesVigentes", Collections.emptyList());
        model.addAttribute("clientes", Collections.emptyList());
        model.addAttribute("clientesMap", Collections.emptyMap());
        if (!model.containsAttribute("promocionForm")) {
            model.addAttribute("promocionForm", new PromocionDTO());
        }
        model.addAttribute("editando", false);
    }

    private PromocionDTO cargarPromocion(String editId) throws ErrorServiceException {
        if (!StringUtils.hasText(editId)) {
            PromocionDTO dto = new PromocionDTO();
            dto.setAplicaATodos(Boolean.TRUE);
            return dto;
        }
        return promocionService.obtener(editId).orElseGet(PromocionDTO::new);
    }

    private String origenRol(Authentication authentication) {
        if (authentication == null) {
            return "CLIENTE";
        }
        if (authentication.getPrincipal() instanceof UserDetailsWithRole userDetails) {
            return userDetails.getRol();
        }
        return "CLIENTE";
    }

    private String obtenerRolActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return origenRol(authentication);
    }

    private String nombreClienteSeguro(ClienteDTO cliente) {
        if (cliente == null) {
            return "Cliente sin identificación";
        }
        String nombre = cliente.getNombre() != null ? cliente.getNombre().trim() : "";
        String apellido = cliente.getApellido() != null ? cliente.getApellido().trim() : "";
        String completo = (nombre + " " + apellido).trim();
        if (StringUtils.hasText(completo)) {
            return completo;
        }
        if (StringUtils.hasText(cliente.getNumeroDocumento())) {
            return cliente.getNumeroDocumento().trim();
        }
        return "Cliente sin identificación";
    }
}
