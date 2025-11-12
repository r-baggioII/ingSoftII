package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.config.AuthCheckInterceptor.UserDetailsWithRole;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.MercadoPagoPreferenceResponse;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.EstadoFactura;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.FacturaService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.PagoMpService;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente")
public class MisFacturasController {

    private final FacturaService facturaService;
    private final PagoMpService pagoMpService;

    public MisFacturasController(FacturaService facturaService, PagoMpService pagoMpService) {
        this.facturaService = facturaService;
        this.pagoMpService = pagoMpService;
    }

    @GetMapping("/mis-facturas")
    public String verMisFacturas(Model model) {
        addUsuarioContext(model);
        String clienteId = obtenerUsuarioId();

        if (!StringUtils.hasText(clienteId)) {
            model.addAttribute("error", "No se pudo determinar el cliente autenticado.");
            model.addAttribute("facturas", Collections.emptyList());
            return "cliente/mis-facturas";
        }

        try {
            List<FacturaDTO> facturas = facturaService.listarPorCliente(clienteId);
            model.addAttribute("facturas", facturas);
        } catch (ErrorServiceException e) {
            model.addAttribute("facturas", Collections.emptyList());
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("clienteId", clienteId);
        model.addAttribute("estadoPagada", EstadoFactura.PAGADA);
        model.addAttribute("estadoPendiente", EstadoFactura.SIN_DEFINIR);
        return "cliente/mis-facturas";
    }

    @PostMapping("/mis-facturas/{id}/mp")
    public String pagarDesdeCliente(
            @PathVariable String id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            MercadoPagoPreferenceResponse response = pagoMpService.generarPreferenciaPorFactura(id);
            if (response == null || !StringUtils.hasText(response.getInitPoint())) {
                throw new ErrorServiceException("La API de pagos no devolvió un link de pago válido");
            }
            return "redirect:" + response.getInitPoint();
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cliente/mis-facturas";
        }
    }

    @GetMapping("/mis-facturas/{id}/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> descargarPdfCliente(@PathVariable String id) {
        try {
            byte[] pdf = facturaService.descargarPdf(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("factura-" + id + ".pdf")
                            .build());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
        } catch (ErrorServiceException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private void addUsuarioContext(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioRol = "CLIENTE";
        String usuarioNombre = "Cliente";
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsWithRole userDetails) {
            usuarioRol = userDetails.getRol();
            usuarioNombre = userDetails.getNombreUsuario();
        }
        model.addAttribute("usuarioRol", usuarioRol);
        model.addAttribute("usuarioNombre", usuarioNombre);
    }

    private String obtenerUsuarioId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsWithRole userDetails) {
            return userDetails.getUsuarioId();
        }
        return null;
    }
}
