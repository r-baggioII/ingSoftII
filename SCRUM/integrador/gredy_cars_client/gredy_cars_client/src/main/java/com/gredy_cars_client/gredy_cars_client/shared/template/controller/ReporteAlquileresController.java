package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.config.AuthCheckInterceptor.UserDetailsWithRole;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.VehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.VehiculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.net.URI;

@Controller
@RequestMapping("/reportes")
public class ReporteAlquileresController {

    private static final Logger log = LoggerFactory.getLogger(ReporteAlquileresController.class);

    @Value("${greedy.api.base-url}")
    private String backendUrl;

    private final VehiculoService vehiculoService;
    private final RestTemplate restTemplate;

    public ReporteAlquileresController(VehiculoService vehiculoService, RestTemplate restTemplate) {
        this.vehiculoService = vehiculoService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/alquileres")
    public String reportesAlquileres(Model model) throws ErrorServiceException {
        // Get user role from Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioRol = "CLIENTE";
        if (auth != null && auth.getPrincipal() instanceof UserDetailsWithRole) {
            UserDetailsWithRole userDetails = (UserDetailsWithRole) auth.getPrincipal();
            usuarioRol = userDetails.getRol();
        }

        // Verify JEFE role
        if (!"JEFE".equals(usuarioRol)) {
            return "redirect:/acceso-denegado";
        }

        model.addAttribute("usuarioRol", usuarioRol);
        model.addAttribute("backendUrl", backendUrl);

        try {
            // Load vehicles for filter dropdown
            List<VehiculoDTO> vehiculos = vehiculoService.listarActivos();
            model.addAttribute("vehiculos", vehiculos);
        } catch (Exception e) {
            // If vehicles fail to load, continue with empty list
            model.addAttribute("vehiculos", new java.util.ArrayList<>());
        }

        // Set default dates (last 30 days)
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusDays(30);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        return "reportes/reporte-alquileres";
    }

    @GetMapping("/alquileres/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> descargarReporteAlquileres(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String vehiculoId) {

        // Verify JEFE role
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioRol = "CLIENTE";
        if (auth != null && auth.getPrincipal() instanceof UserDetailsWithRole) {
            UserDetailsWithRole userDetails = (UserDetailsWithRole) auth.getPrincipal();
            usuarioRol = userDetails.getRol();
        }

        if (!"JEFE".equals(usuarioRol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(backendUrl)
                    .path("/reportes/alquileres/pdf")
                    .queryParam("fechaInicio", fechaInicio)
                    .queryParam("fechaFin", fechaFin);

            if (vehiculoId != null && !vehiculoId.isBlank()) {
                builder.queryParam("vehiculoId", vehiculoId.trim());
            }

            URI uri = builder.build(true).toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Call backend API
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Build response headers for PDF download
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.APPLICATION_PDF);
                responseHeaders.setContentDisposition(ContentDisposition.attachment()
                        .filename("reporte-alquileres-" + LocalDate.now() + ".pdf")
                        .build());

                return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
            } else {
                return ResponseEntity.status(response.getStatusCode()).build();
            }
        } catch (RestClientResponseException e) {
            log.error("Error al descargar reporte de alquileres: status={}, body={}",
                    e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            log.error("Error inesperado al descargar reporte de alquileres", e);
            // Log error and return internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
