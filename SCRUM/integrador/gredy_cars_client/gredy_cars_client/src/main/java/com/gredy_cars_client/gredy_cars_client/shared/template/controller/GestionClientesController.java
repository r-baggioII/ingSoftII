package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ImagenDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseController;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoImagen;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ClienteService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ImagenService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.NacionalidadService;

@Controller
@RequestMapping("/gestion/clientes")
public class GestionClientesController extends BaseThymeleafController<ClienteDTO, String> {

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
        super(clienteService);
        this.clienteService = clienteService;
        this.nacionalidadService = nacionalidadService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
        this.direccionService = direccionService;
        this.imagenService = imagenService;
    }

    @Override
    protected String getListView() {
        return "gestion/gestion-clientes";
    }

    @Override
    protected String getFormView() {
        return "gestion/gestion-clientes";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/gestion/clientes";
    }

    @Override
    protected String getListModelAttribute() {
        return "clientes";
    }

    @Override
    protected String getFormModelAttribute() {
        return "clienteForm";
    }

    @Override
    protected String getEntityLabel() {
        return "Cliente";
    }

    @Override
    protected ClienteDTO buildNewInstance() {
        ClienteDTO dto = new ClienteDTO();
        dto.setContactoIds(new ArrayList<>());
        dto.setDireccionIds(new ArrayList<>());
        dto.setNacionalidadIds(new ArrayList<>());
        dto.setImagenIds(new ArrayList<>());
        return dto;
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("nacionalidades", nacionalidadService.listarActivos());
        model.addAttribute("direcciones", direccionService.listarActivos());
        model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
        model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());

        try {
            List<ImagenDTO> imagenes = imagenService.listarPorTipo(TipoImagen.PERSONA);
            model.addAttribute("imagenes", imagenes);
        } catch (Exception e) {
            log.warn("Error al cargar imágenes de clientes: {}", e.getMessage());
            model.addAttribute("imagenes", Collections.emptyList());
        }
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase, Model model) throws ErrorServiceException {
        populateCollections(model);
    }

    @GetMapping
    public String gestionarClientes(@RequestParam(value = "editClienteId", required = false) String editClienteId,
                                    Model model) {
        String view = renderList(model);
        if (editClienteId != null && !editClienteId.isBlank()) {
            try {
                Optional<ClienteDTO> clienteOpt = service.obtener(editClienteId);
                if (clienteOpt.isPresent()) {
                    model.addAttribute(getFormModelAttribute(), clienteOpt.get());
                } else {
                    registerError(model, "Cliente no encontrado");
                    model.addAttribute(getFormModelAttribute(), buildNewInstance());
                }
            } catch (ErrorServiceException e) {
                log.warn("No se pudo cargar el cliente {}: {}", editClienteId, e.getMessage());
                registerError(model, e.getMessage());
                model.addAttribute(getFormModelAttribute(), buildNewInstance());
            }
        } else if (!model.containsAttribute(getFormModelAttribute())) {
            model.addAttribute(getFormModelAttribute(), buildNewInstance());
        }
        return view;
    }

    @PostMapping
    public String guardarCliente(@ModelAttribute("clienteForm") ClienteDTO dto,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        return handleCreate(dto, model, redirectAttributes);
    }

    @PostMapping("/{id}")
    public String actualizarCliente(@PathVariable String id,
                                    @ModelAttribute("clienteForm") ClienteDTO dto,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        return handleUpdate(id, dto, model, redirectAttributes);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarCliente(@PathVariable String id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }

    @GetMapping("/clientes/{id}")
    public String verCliente(@PathVariable String id, Model model) {
        try {
            ClienteDTO cliente = service.obtener(id)
                .orElseThrow(() -> new ErrorServiceException("Cliente no encontrado"));
            model.addAttribute("cliente", cliente);
        } catch (ErrorServiceException e) {
            registerError(model, e.getMessage());
            model.addAttribute("cliente", new ClienteDTO());
            model.addAttribute("alquileresCliente", Collections.emptyList());
            return "gestion/cliente-detalle";
        }

        List<AlquilerDTO> alquileres = clienteService.listarAlquileresPorCliente(id);
        model.addAttribute("alquileresCliente", alquileres);
        return "gestion/cliente-detalle";
    }

    private ClienteDTO sanitizeClienteDTO(ClienteDTO dto) {
        if (dto == null) {
            return buildNewInstance();
        }

        dto.setContactosCorreo(null);
        dto.setContactosTelefono(null);
        dto.setDirecciones(null);
        dto.setImagenes(null);
        dto.setNacionalidad(null);

        if (dto.getContactoIds() == null) {
            dto.setContactoIds(new ArrayList<>());
        }
        if (dto.getDireccionIds() == null) {
            dto.setDireccionIds(new ArrayList<>());
        }
        if (dto.getNacionalidadIds() == null) {
            dto.setNacionalidadIds(new ArrayList<>());
        }
        if (dto.getImagenIds() == null) {
            dto.setImagenIds(new ArrayList<>());
        }

        return dto;
    }

    @Override
    public String handleCreate(ClienteDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleCreate(sanitizeClienteDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }

    @Override
    public String handleUpdate(String id, ClienteDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleUpdate(id, sanitizeClienteDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }
}
