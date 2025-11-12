package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
import java.util.Optional;

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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoTelefonicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpleadoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.NacionalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PersonaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.UsuarioDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseController;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.Rol;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoContacto;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoEmpleado;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoTelefono;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ClienteService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.EmpleadoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.NacionalidadService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.PersonaService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.UsuarioService;

@Controller
@RequestMapping("/gestion")
public class GestionUsuariosController extends BaseThymeleafController<UsuarioDTO, String> {

    private final UsuarioService usuarioService;
    private final PersonaService personaService;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    private final NacionalidadService nacionalidadService;
    private final ContactoCorreoElectronicoService contactoCorreoService;
    private final ContactoTelefonicoService contactoTelefonicoService;

    public GestionUsuariosController(UsuarioService usuarioService,
                                     PersonaService personaService,
                                     ClienteService clienteService,
                                     EmpleadoService empleadoService,
                                     NacionalidadService nacionalidadService,
                                     ContactoCorreoElectronicoService contactoCorreoService,
                                     ContactoTelefonicoService contactoTelefonicoService) {
        super(usuarioService);
        this.usuarioService = usuarioService;
        this.personaService = personaService;
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
        this.nacionalidadService = nacionalidadService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
    }

    @Override
    protected String getListView() {
        return "gestion/gestion-usuarios";
    }

    @Override
    protected String getFormView() {
        return "gestion/gestion-usuarios";
    }

    @Override
    protected String getRedirectToList() {
        return "redirect:/gestion/usuarios";
    }

    @Override
    protected String getListModelAttribute() {
        return "usuarios";
    }

    @Override
    protected String getFormModelAttribute() {
        return "usuarioForm";
    }

    @Override
    protected String getEntityLabel() {
        return "Usuario";
    }

    @Override
    protected UsuarioDTO buildNewInstance() {
        return new UsuarioDTO();
    }

    @Override
    protected void populateCollections(Model model) throws ErrorServiceException {
        model.addAttribute("roles", Rol.values());
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("tiposEmpleado", TipoEmpleado.values());
        model.addAttribute("tiposContacto", TipoContacto.values());
        model.addAttribute("tiposTelefono", TipoTelefono.values());
        model.addAttribute("nacionalidades", nacionalidadService.listarActivos());
        model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
        model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());
        model.addAttribute("personas", personaService.listarActivos());
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("empleados", empleadoService.listarActivos());
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase, Model model) throws ErrorServiceException {
        populateCollections(model);
    }

    @Override
    protected void postSuccess(BaseUseCaseController useCase, RedirectAttributes redirectAttributes, UsuarioDTO payload) {
        if (payload != null && StringUtils.hasText(payload.getNombreUsuario())) {
            log.info("Operación {} completada para el usuario {}", useCase, payload.getNombreUsuario());
        }
    }

    @Override
    public String handleCreate(UsuarioDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleCreate(sanitizeUsuarioDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }

    @Override
    public String handleUpdate(String id, UsuarioDTO dto, Model model, RedirectAttributes redirectAttributes) {
        String view = super.handleUpdate(id, sanitizeUsuarioDTO(dto), model, redirectAttributes);
        if (!view.startsWith("redirect:")) {
            renderList(model);
        }
        return view;
    }

    @GetMapping("/usuarios")
    public String gestionarUsuarios(@RequestParam(value = "editUsuarioId", required = false) String editUsuarioId,
                                    @RequestParam(value = "editPersonaId", required = false) String editPersonaId,
                                    @RequestParam(value = "editClienteId", required = false) String editClienteId,
                                    @RequestParam(value = "editEmpleadoId", required = false) String editEmpleadoId,
                                    Model model) {
        String view = renderList(model);
        prepareUsuarioForm(editUsuarioId, model);
        preparePersonaForm(editPersonaId, model);
        prepareClienteForm(editClienteId, model);
        prepareEmpleadoForm(editEmpleadoId, model);
        return view;
    }

    @PostMapping({"/usuarios", "/usuarios/usuario"})
    public String guardarUsuario(@ModelAttribute("usuarioForm") UsuarioDTO dto,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        return handleCreate(dto, model, redirectAttributes);
    }

    @PostMapping({"/usuarios/{id}", "/usuarios/usuario/{id}"})
    public String actualizarUsuario(@PathVariable String id,
                                    @ModelAttribute("usuarioForm") UsuarioDTO dto,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        return handleUpdate(id, dto, model, redirectAttributes);
    }

    @PostMapping({"/usuarios/{id}/eliminar", "/usuarios/usuario/{id}/eliminar"})
    public String eliminarUsuario(@PathVariable String id, RedirectAttributes redirectAttributes) {
        return handleDelete(id, redirectAttributes);
    }

    @PostMapping("/usuarios/{id}/reset-password")
    public String resetPassword(@PathVariable String id,
                                @RequestParam("nuevaClave") String nuevaClave,
                                RedirectAttributes redirectAttributes) {
        try {
            usuarioService.resetPassword(id, nuevaClave);
            registerSuccess(redirectAttributes, "Contraseña actualizada correctamente");
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }

    @PostMapping("/usuarios/persona")
    public String guardarPersona(@ModelAttribute("personaForm") PersonaDTO dto, RedirectAttributes redirectAttributes) {
        try {
            if (!StringUtils.hasText(dto.getId())) {
                personaService.alta(dto);
                registerSuccess(redirectAttributes, "Persona creada correctamente");
            } else {
                personaService.modificar(dto.getId(), dto);
                registerSuccess(redirectAttributes, "Persona actualizada correctamente");
            }
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }

    @PostMapping("/usuarios/persona/{id}/eliminar")
    public String eliminarPersona(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            personaService.baja(id);
            registerSuccess(redirectAttributes, "Persona eliminada");
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }

    @PostMapping("/usuarios/cliente")
    public String guardarCliente(@ModelAttribute("clienteForm") ClienteDTO dto, RedirectAttributes redirectAttributes) {
        try {
            if (!StringUtils.hasText(dto.getId())) {
                clienteService.alta(dto);
                registerSuccess(redirectAttributes, "Cliente creado correctamente");
            } else {
                clienteService.modificar(dto.getId(), dto);
                registerSuccess(redirectAttributes, "Cliente actualizado correctamente");
            }
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }

    @PostMapping("/usuarios/cliente/{id}/eliminar")
    public String eliminarCliente(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.baja(id);
            registerSuccess(redirectAttributes, "Cliente eliminado");
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }

    @PostMapping("/usuarios/empleado")
    public String guardarEmpleado(@ModelAttribute("empleadoForm") EmpleadoDTO dto, RedirectAttributes redirectAttributes) {
        try {
            if (!StringUtils.hasText(dto.getId())) {
                empleadoService.alta(dto);
                registerSuccess(redirectAttributes, "Empleado creado correctamente");
            } else {
                empleadoService.modificar(dto.getId(), dto);
                registerSuccess(redirectAttributes, "Empleado actualizado correctamente");
            }
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }

    @PostMapping("/usuarios/empleado/{id}/eliminar")
    public String eliminarEmpleado(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.baja(id);
            registerSuccess(redirectAttributes, "Empleado eliminado");
        } catch (ErrorServiceException e) {
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }

    private void prepareUsuarioForm(String editUsuarioId, Model model) {
        if (model.containsAttribute(getFormModelAttribute())) {
            return;
        }
        if (StringUtils.hasText(editUsuarioId)) {
            try {
                Optional<UsuarioDTO> usuarioOpt = service.obtener(editUsuarioId);
                if (usuarioOpt.isPresent()) {
                    model.addAttribute(getFormModelAttribute(), usuarioOpt.get());
                    return;
                }
                registerError(model, "Usuario no encontrado");
            } catch (ErrorServiceException e) {
                registerError(model, e.getMessage());
            }
        }
        model.addAttribute(getFormModelAttribute(), buildNewInstance());
    }

    private void preparePersonaForm(String editPersonaId, Model model) {
        PersonaDTO personaForm;
        try {
            personaForm = StringUtils.hasText(editPersonaId)
                ? personaService.obtener(editPersonaId).orElseGet(PersonaDTO::new)
                : new PersonaDTO();
        } catch (ErrorServiceException e) {
            personaForm = new PersonaDTO();
            registerError(model, e.getMessage());
        }
        if (personaForm.getContactos() == null) {
            personaForm.setContactos(new ArrayList<>());
        }
        if (personaForm.getDirecciones() == null) {
            personaForm.setDirecciones(new ArrayList<>());
        }
        if (personaForm.getImagenes() == null) {
            personaForm.setImagenes(new ArrayList<>());
        }
        model.addAttribute("personaForm", personaForm);
    }

    private void prepareClienteForm(String editClienteId, Model model) {
        ClienteDTO clienteForm;
        try {
            clienteForm = StringUtils.hasText(editClienteId)
                ? clienteService.obtener(editClienteId).orElseGet(ClienteDTO::new)
                : new ClienteDTO();
        } catch (ErrorServiceException e) {
            clienteForm = new ClienteDTO();
            registerError(model, e.getMessage());
        }
        if (clienteForm.getContactosTelefono() == null || clienteForm.getContactosTelefono().isEmpty()) {
            clienteForm.setContactosTelefono(new ArrayList<>());
            clienteForm.getContactosTelefono().add(new ContactoTelefonicoDTO());
        }
        if (clienteForm.getContactosCorreo() == null || clienteForm.getContactosCorreo().isEmpty()) {
            clienteForm.setContactosCorreo(new ArrayList<>());
            clienteForm.getContactosCorreo().add(new ContactoCorreoElectronicoDTO());
        }
        if (clienteForm.getNacionalidades() == null || clienteForm.getNacionalidades().isEmpty()) {
            clienteForm.setNacionalidades(new ArrayList<>());
            clienteForm.getNacionalidades().add(new NacionalidadDTO());
        }
        model.addAttribute("clienteForm", clienteForm);
    }

    private void prepareEmpleadoForm(String editEmpleadoId, Model model) {
        EmpleadoDTO empleadoForm;
        try {
            empleadoForm = StringUtils.hasText(editEmpleadoId)
                ? empleadoService.obtener(editEmpleadoId).orElseGet(EmpleadoDTO::new)
                : new EmpleadoDTO();
        } catch (ErrorServiceException e) {
            empleadoForm = new EmpleadoDTO();
            registerError(model, e.getMessage());
        }
        if (empleadoForm.getContactosTelefono() == null || empleadoForm.getContactosTelefono().isEmpty()) {
            empleadoForm.setContactosTelefono(new ArrayList<>());
            empleadoForm.getContactosTelefono().add(new ContactoTelefonicoDTO());
        }
        if (empleadoForm.getContactosCorreo() == null || empleadoForm.getContactosCorreo().isEmpty()) {
            empleadoForm.setContactosCorreo(new ArrayList<>());
            empleadoForm.getContactosCorreo().add(new ContactoCorreoElectronicoDTO());
        }
        model.addAttribute("empleadoForm", empleadoForm);
    }

    private UsuarioDTO sanitizeUsuarioDTO(UsuarioDTO dto) {
        if (dto == null) {
            return buildNewInstance();
        }
        dto.setNombreUsuario(dto.getNombreUsuario() != null ? dto.getNombreUsuario().trim() : null);
        dto.setPersonaId(dto.getPersonaId() != null ? dto.getPersonaId().trim() : null);
        if (dto.getClave() != null && dto.getClave().isBlank()) {
            dto.setClave(null);
        }
        return dto;
    }
}
