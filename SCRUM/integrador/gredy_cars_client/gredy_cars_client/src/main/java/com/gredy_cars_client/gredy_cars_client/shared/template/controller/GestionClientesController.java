package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoTelefonicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ImagenDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.NacionalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoImagen;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ClienteService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ImagenService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.NacionalidadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestion/clientes")
public class GestionClientesController {

    private static final Logger log = LoggerFactory.getLogger(GestionClientesController.class);

    private final ClienteService clienteService;
    private final NacionalidadService nacionalidadService;
    private final ContactoCorreoElectronicoService contactoCorreoService;
    private final ContactoTelefonicoService contactoTelefonicoService;
    private final DireccionService direccionService;
    private final ImagenService imagenService;

    public GestionClientesController(ClienteService clienteService,
                                     NacionalidadService nacionalidadService,
                                     ContactoCorreoElectronicoService contactoCorreoService,
                                     ContactoTelefonicoService contactoTelefonicoService,
                                     DireccionService direccionService,
                                     ImagenService imagenService) {
        this.clienteService = clienteService;
        this.nacionalidadService = nacionalidadService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
        this.direccionService = direccionService;
        this.imagenService = imagenService;
    }

    @GetMapping
    public String gestionarClientes(@RequestParam(value = "editClienteId", required = false) String editClienteId,
                                    Model model) {
        try {
            // Listados
            model.addAttribute("clientes", clienteService.listarActivos());

            // Catálogos / enums
            model.addAttribute("tiposDocumento", TipoDocumento.values());
            model.addAttribute("nacionalidades", nacionalidadService.listarActivos());
            model.addAttribute("direcciones", direccionService.listarActivos());
            
            // Listas de contactos disponibles
            model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
            model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());
            
            // Listas de imágenes disponibles (tipo PERSONA)
            try {
                List<ImagenDTO> imagenes = imagenService.listarPorTipo(TipoImagen.PERSONA);
                model.addAttribute("imagenes", imagenes);
            } catch (Exception e) {
                log.warn("Error al cargar imágenes: {}", e.getMessage());
                model.addAttribute("imagenes", Collections.emptyList());
            }
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clientes", Collections.emptyList());
            model.addAttribute("tiposDocumento", TipoDocumento.values());
            model.addAttribute("nacionalidades", Collections.emptyList());
            model.addAttribute("direcciones", Collections.emptyList());
            model.addAttribute("contactosCorreo", Collections.emptyList());
            model.addAttribute("contactosTelefono", Collections.emptyList());
        }

        // Formulario (nuevo o edición)
        ClienteDTO clienteForm;
        try {
            if (editClienteId != null && !editClienteId.isBlank()) {
                clienteForm = clienteService.obtener(editClienteId).orElseGet(ClienteDTO::new);
            } else {
                clienteForm = new ClienteDTO();
            }
        } catch (ErrorServiceException e) {
            clienteForm = new ClienteDTO();
        }
        
        // Inicializar listas vacías si son null
        if (clienteForm.getContactoIds() == null) {
            clienteForm.setContactoIds(new ArrayList<>());
        }
        if (clienteForm.getDireccionIds() == null) {
            clienteForm.setDireccionIds(new ArrayList<>());
        }
        if (clienteForm.getNacionalidadIds() == null) {
            clienteForm.setNacionalidadIds(new ArrayList<>());
        }
        if (clienteForm.getImagenIds() == null) {
            clienteForm.setImagenIds(new ArrayList<>());
        }
        
        model.addAttribute("clienteForm", clienteForm);

        return "gestion/gestion-clientes";
    }

    @PostMapping("/cliente")
    public String guardarCliente(@ModelAttribute("clienteForm") ClienteDTO dto, RedirectAttributes ra) {
        log.info("=== INICIO guardarCliente ===");
        log.info("DTO recibido: {}", dto);
        log.info("Nombre: {}, Apellido: {}", dto.getNombre(), dto.getApellido());
        log.info("Documento: {} - {}", dto.getTipoDocumento(), dto.getNumeroDocumento());
        log.info("NacionalidadIds: {}", dto.getNacionalidadIds());
        log.info("DireccionIds: {}", dto.getDireccionIds());
        log.info("ContactoIds: {}", dto.getContactoIds());
        log.info("Direccion Estadia: {}", dto.getDireccionEstadia());
        
        try {
            // Limpiar objetos completos que no se deben enviar al servidor
            dto.setContactosCorreo(null);
            dto.setContactosTelefono(null);
            dto.setDirecciones(null);
            dto.setImagenes(null);
            dto.setNacionalidad(null);
            
            // Asegurar que las listas de IDs no sean null
            if (dto.getDireccionIds() == null) {
                dto.setDireccionIds(new ArrayList<>());
            }
            if (dto.getContactoIds() == null) {
                dto.setContactoIds(new ArrayList<>());
            }
            if (dto.getNacionalidadIds() == null) {
                dto.setNacionalidadIds(new ArrayList<>());
            }
            if (dto.getImagenIds() == null) {
                dto.setImagenIds(new ArrayList<>());
            }
            
            log.info("Enviando al servidor - DireccionIds: {}, ContactoIds: {}, NacionalidadIds: {}", 
                    dto.getDireccionIds(), dto.getContactoIds(), dto.getNacionalidadIds());
            
            if (dto.getId() == null || dto.getId().isBlank()) {
                log.info("Creando nuevo cliente...");
                ClienteDTO guardado = clienteService.alta(dto);
                log.info("Cliente creado con ID: {}", guardado.getId());
                ra.addFlashAttribute("success", "Cliente creado correctamente");
            } else {
                log.info("Modificando cliente con ID: {}", dto.getId());
                clienteService.modificar(dto.getId(), dto);
                ra.addFlashAttribute("success", "Cliente actualizado correctamente");
            }
        } catch (ErrorServiceException e) {
            log.error("Error al guardar cliente: {}", e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/clientes";
    }

    @PostMapping("/cliente/{id}/eliminar")
    public String eliminarCliente(@PathVariable String id, RedirectAttributes ra) {
        try {
            clienteService.baja(id);
            ra.addFlashAttribute("success", "Cliente eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/clientes";
    }

    @GetMapping("/clientes/{id}")
    public String verCliente(@PathVariable String id, Model model) {
        try {
            ClienteDTO cliente = clienteService.obtener(id)
                    .orElseThrow(() -> new ErrorServiceException("Cliente no encontrado"));
            model.addAttribute("cliente", cliente);
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliente", new ClienteDTO());
            model.addAttribute("alquileresCliente", Collections.emptyList());
            return "gestion/cliente-detalle";
        }

        List<AlquilerDTO> alquileres = clienteService.listarAlquileresPorCliente(id);
        model.addAttribute("alquileresCliente", alquileres);
        return "gestion/cliente-detalle";
    }
}
