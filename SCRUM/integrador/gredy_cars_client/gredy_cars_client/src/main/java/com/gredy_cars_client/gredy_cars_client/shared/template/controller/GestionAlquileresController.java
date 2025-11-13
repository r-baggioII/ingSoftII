package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.config.AuthCheckInterceptor.UserDetailsWithRole;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CaracteristicaVehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DocumentacionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.VehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.AlquilerService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ClienteService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DocumentacionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.VehiculoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.CaracteristicaVehiculoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.WhatsAppService;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumentacion;

@Controller
@RequestMapping("/gestion")
public class GestionAlquileresController {

    private final AlquilerService alquilerService;
    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;
    private final CaracteristicaVehiculoService caracteristicaService;
    private final DocumentacionService documentacionService;
    private final WhatsAppService whatsAppService;

    public GestionAlquileresController(AlquilerService alquilerService,
                                       ClienteService clienteService,
                                       VehiculoService vehiculoService,
                                       CaracteristicaVehiculoService caracteristicaService,
                                       DocumentacionService documentacionService,
                                       WhatsAppService whatsAppService) {
        this.alquilerService = alquilerService;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
        this.caracteristicaService = caracteristicaService;
        this.documentacionService = documentacionService;
        this.whatsAppService = whatsAppService;
    }

    @PostMapping("/alquileres/{id}/recordatorio-whatsapp")
    public String enviarRecordatorioWhatsApp(
        @PathVariable String id,
        RedirectAttributes redirectAttributes
    ) {
        try {
            whatsAppService.enviarRecordatorioManual(id);
            redirectAttributes.addFlashAttribute("success", "Se envió el recordatorio de WhatsApp correctamente.");
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/alquileres";
    }

    @GetMapping("/alquileres")
    public String gestionarAlquileres(
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "editDocId", required = false) String editDocId,
        Model model
    ) throws ErrorServiceException {
        // Get user role from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioRol = "CLIENTE";
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsWithRole) {
            UserDetailsWithRole userDetails = (UserDetailsWithRole) authentication.getPrincipal();
            usuarioRol = userDetails.getRol();
        }
        model.addAttribute("usuarioRol", usuarioRol);
        
        model.addAttribute("alquileres", alquilerService.listarActivos());

        // Load clients and vehicles for dropdowns
        List<ClienteDTO> clientes = clienteService.listarActivos();
        List<VehiculoDTO> vehiculos = vehiculoService.listarActivos();
        List<DocumentacionDTO> documentaciones = documentacionService.listarActivos();
        Map<String, DocumentacionDTO> documentacionMap = new HashMap<>();
        List<DocumentacionDTO> documentosIdentidad = new ArrayList<>();
        List<DocumentacionDTO> carnetsConducir = new ArrayList<>();
        for (DocumentacionDTO doc : documentaciones) {
            documentacionMap.put(doc.getId(), doc);
            if (doc.getTipoDocumentacion() == TipoDocumentacion.DOCUMENTO_IDENTIDAD) {
                documentosIdentidad.add(doc);
            } else if (doc.getTipoDocumentacion() == TipoDocumentacion.CARNET_DE_CONDUCIR) {
                carnetsConducir.add(doc);
            }
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("documentaciones", documentaciones);
        model.addAttribute("documentacionMap", documentacionMap);
        model.addAttribute("documentosIdentidad", documentosIdentidad);
        model.addAttribute("carnetsConducir", carnetsConducir);
        model.addAttribute("tiposDocumentacion", TipoDocumentacion.values());

        AlquilerDTO form = editId != null && !editId.isBlank() ?
            alquilerService.obtener(editId).orElseGet(AlquilerDTO::new) : new AlquilerDTO();
        model.addAttribute("alquilerForm", form);

        String documentoIdentidadSeleccionado = null;
        String carnetConducirSeleccionado = null;
        if (form.getDocumentacionIds() != null) {
            for (String docId : form.getDocumentacionIds()) {
                DocumentacionDTO doc = documentacionMap.get(docId);
                if (doc == null) {
                    continue;
                }
                if (doc.getTipoDocumentacion() == TipoDocumentacion.DOCUMENTO_IDENTIDAD) {
                    documentoIdentidadSeleccionado = docId;
                } else if (doc.getTipoDocumentacion() == TipoDocumentacion.CARNET_DE_CONDUCIR) {
                    carnetConducirSeleccionado = docId;
                }
            }
        }
        model.addAttribute("documentoIdentidadSeleccionado", documentoIdentidadSeleccionado);
        model.addAttribute("carnetConducirSeleccionado", carnetConducirSeleccionado);

        if (!model.containsAttribute("documentacionForm")) {
            DocumentacionDTO docForm = editDocId != null && !editDocId.isBlank() ?
                documentacionService.obtener(editDocId).orElseGet(DocumentacionDTO::new) : new DocumentacionDTO();
            model.addAttribute("documentacionForm", docForm);
        }

        return "gestion/gestion-alquileres";
    }

    @PostMapping("/alquileres")
    public String guardar(@ModelAttribute("alquilerForm") AlquilerDTO alquiler,
                          @RequestParam(value = "documentoIdentidadId", required = false) String documentoIdentidadId,
                          @RequestParam(value = "carnetConducirId", required = false) String carnetConducirId,
                          RedirectAttributes ra) {
        try {
            List<String> docIds = new ArrayList<>();
            if (StringUtils.hasText(documentoIdentidadId)) {
                docIds.add(documentoIdentidadId);
            }
            if (StringUtils.hasText(carnetConducirId)) {
                docIds.add(carnetConducirId);
            }
            alquiler.setDocumentacionIds(docIds);

            if (alquiler.getId() == null || alquiler.getId().isBlank()) {
                alquilerService.alta(alquiler);
            } else {
                alquilerService.modificar(alquiler.getId(), alquiler);
            }

            // Synchronize characteristic counts after rental save/update
            sincronizarConteosCaracteristicas();

            ra.addFlashAttribute("success", "Alquiler guardado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/alquileres";
    }

    @PostMapping("/alquileres/documentacion")
    public String guardarDocumentacion(@ModelAttribute("documentacionForm") DocumentacionDTO documentacion,
                                       RedirectAttributes ra) {
        try {
            if (documentacion.getId() == null || documentacion.getId().isBlank()) {
                documentacionService.alta(documentacion);
                ra.addFlashAttribute("success", "Documentación creada correctamente");
            } else {
                documentacionService.modificar(documentacion.getId(), documentacion);
                ra.addFlashAttribute("success", "Documentación actualizada correctamente");
            }
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("documentacionForm", documentacion);
        }
        return "redirect:/gestion/alquileres";
    }

    @PostMapping("/alquileres/documentacion/{id}/eliminar")
    public String eliminarDocumentacion(@PathVariable String id, RedirectAttributes ra) {
        try {
            documentacionService.baja(id);
            ra.addFlashAttribute("success", "Documentación eliminada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/alquileres";
    }

    @PostMapping("/alquileres/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes ra) {
        try {
            alquilerService.baja(id);

            // Synchronize characteristic counts after rental deletion
            sincronizarConteosCaracteristicas();

            ra.addFlashAttribute("success", "Alquiler eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/alquileres";
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
}
