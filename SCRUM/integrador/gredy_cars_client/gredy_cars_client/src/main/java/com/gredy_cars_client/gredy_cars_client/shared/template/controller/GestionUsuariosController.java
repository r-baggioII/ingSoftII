package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoTelefonicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpleadoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.NacionalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PersonaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.UsuarioDTO;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;

@Controller
@RequestMapping("/gestion")
public class GestionUsuariosController {

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
        this.usuarioService = usuarioService;
        this.personaService = personaService;
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
        this.nacionalidadService = nacionalidadService;
        this.contactoCorreoService = contactoCorreoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
    }

    @GetMapping("/usuarios")
    public String gestionarUsuarios(@RequestParam(value = "editUsuarioId", required = false) String editUsuarioId,
                                    @RequestParam(value = "editPersonaId", required = false) String editPersonaId,
                                    @RequestParam(value = "editClienteId", required = false) String editClienteId,
                                    @RequestParam(value = "editEmpleadoId", required = false) String editEmpleadoId,
                                    Model model) {
        try {
            // Listados
            model.addAttribute("usuarios", usuarioService.listarActivos());
            model.addAttribute("personas", personaService.listarActivos());
            model.addAttribute("clientes", clienteService.listarActivos());
            model.addAttribute("empleados", empleadoService.listarActivos());

            // Catálogos / enums
            model.addAttribute("roles", Rol.values());
            model.addAttribute("tiposDocumento", TipoDocumento.values());
            model.addAttribute("tiposEmpleado", TipoEmpleado.values());
            model.addAttribute("tiposContacto", TipoContacto.values());
            model.addAttribute("tiposTelefono", TipoTelefono.values());
            model.addAttribute("nacionalidades", nacionalidadService.listarActivos());
            
            // Listas de contactos disponibles
            model.addAttribute("contactosCorreo", contactoCorreoService.listarActivos());
            model.addAttribute("contactosTelefono", contactoTelefonicoService.listarActivos());
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            // Inicializar colecciones para evitar NPE en la vista
            model.addAttribute("usuarios", Collections.emptyList());
            model.addAttribute("personas", Collections.emptyList());
            model.addAttribute("clientes", Collections.emptyList());
            model.addAttribute("empleados", Collections.emptyList());
            model.addAttribute("roles", Rol.values());
            model.addAttribute("tiposDocumento", TipoDocumento.values());
            model.addAttribute("tiposEmpleado", TipoEmpleado.values());
            model.addAttribute("tiposContacto", TipoContacto.values());
            model.addAttribute("tiposTelefono", TipoTelefono.values());
            model.addAttribute("nacionalidades", Collections.emptyList());
            model.addAttribute("contactosCorreo", Collections.emptyList());
            model.addAttribute("contactosTelefono", Collections.emptyList());
        }

        // Formularios (nuevo o edición)
        UsuarioDTO usuarioForm;
        try {
            usuarioForm = (editUsuarioId != null && !editUsuarioId.isBlank())
                ? usuarioService.obtener(editUsuarioId).orElseGet(UsuarioDTO::new)
                : new UsuarioDTO();
        } catch (ErrorServiceException e) {
            usuarioForm = new UsuarioDTO();
        }
        model.addAttribute("usuarioForm", usuarioForm);

        PersonaDTO personaForm;
        try {
            personaForm = (editPersonaId != null && !editPersonaId.isBlank())
                ? personaService.obtener(editPersonaId).orElseGet(PersonaDTO::new)
                : new PersonaDTO();
        } catch (ErrorServiceException e) {
            personaForm = new PersonaDTO();
        }
        model.addAttribute("personaForm", personaForm);

        ClienteDTO clienteForm;
        try {
            clienteForm = (editClienteId != null && !editClienteId.isBlank())
                ? clienteService.obtener(editClienteId).orElseGet(ClienteDTO::new)
                : new ClienteDTO();
        } catch (ErrorServiceException e) {
            clienteForm = new ClienteDTO();
        }
        // Inicializar listas si están vacías
        if (clienteForm.getContactosTelefono() == null || clienteForm.getContactosTelefono().isEmpty()) {
            clienteForm.setContactosTelefono(new java.util.ArrayList<>());
            clienteForm.getContactosTelefono().add(new ContactoTelefonicoDTO());
        }
        if (clienteForm.getContactosCorreo() == null || clienteForm.getContactosCorreo().isEmpty()) {
            clienteForm.setContactosCorreo(new java.util.ArrayList<>());
            clienteForm.getContactosCorreo().add(new ContactoCorreoElectronicoDTO());
        }
        if (clienteForm.getNacionalidad() == null) {
            clienteForm.setNacionalidad(new NacionalidadDTO());
        }
        model.addAttribute("clienteForm", clienteForm);

        EmpleadoDTO empleadoForm;
        try {
            empleadoForm = (editEmpleadoId != null && !editEmpleadoId.isBlank())
                ? empleadoService.obtener(editEmpleadoId).orElseGet(EmpleadoDTO::new)
                : new EmpleadoDTO();
        } catch (ErrorServiceException e) {
            empleadoForm = new EmpleadoDTO();
        }
        // Inicializar listas si están vacías
        if (empleadoForm.getContactosTelefono() == null || empleadoForm.getContactosTelefono().isEmpty()) {
            empleadoForm.setContactosTelefono(new java.util.ArrayList<>());
            empleadoForm.getContactosTelefono().add(new ContactoTelefonicoDTO());
        }
        if (empleadoForm.getContactosCorreo() == null || empleadoForm.getContactosCorreo().isEmpty()) {
            empleadoForm.setContactosCorreo(new java.util.ArrayList<>());
            empleadoForm.getContactosCorreo().add(new ContactoCorreoElectronicoDTO());
        }
        model.addAttribute("empleadoForm", empleadoForm);

        return "gestion/gestion-usuarios";
    }

    // --- Usuarios ---
    @PostMapping("/usuarios/usuario")
    public String guardarUsuario(@ModelAttribute("usuarioForm") UsuarioDTO dto, RedirectAttributes ra) {
        System.err.println("=== INICIO guardarUsuario ===");
        System.err.println("DTO recibido: " + dto);
        System.err.println("  - id: " + dto.getId());
        System.err.println("  - nombreUsuario: " + dto.getNombreUsuario());
        System.err.println("  - clave: " + (dto.getClave() != null ? "[OCULTA]" : "null"));
        System.err.println("  - rol: " + dto.getRol());
        System.err.println("  - personaId: " + dto.getPersonaId());
        
        try {
            if (dto.getId() == null || dto.getId().isBlank()) {
                System.err.println("Creando nuevo usuario...");
                usuarioService.alta(dto);
                ra.addFlashAttribute("success", "Usuario creado correctamente");
            } else {
                System.err.println("Modificando usuario existente...");
                usuarioService.modificar(dto.getId(), dto);
                ra.addFlashAttribute("success", "Usuario actualizado correctamente");
            }
            System.err.println("=== FIN guardarUsuario - ÉXITO ===");
        } catch (ErrorServiceException e) {
            System.err.println("ErrorServiceException: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception inesperada: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

    @PostMapping("/usuarios/usuario/{id}/eliminar")
    public String eliminarUsuario(@PathVariable String id, RedirectAttributes ra) {
        try {
            usuarioService.baja(id);
            ra.addFlashAttribute("success", "Usuario eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

    // --- Personas ---
    @PostMapping("/usuarios/persona")
    public String guardarPersona(@ModelAttribute("personaForm") PersonaDTO dto, RedirectAttributes ra) {
        try {
            if (dto.getId() == null || dto.getId().isBlank()) {
                personaService.alta(dto);
                ra.addFlashAttribute("success", "Persona creada correctamente");
            } else {
                personaService.modificar(dto.getId(), dto);
                ra.addFlashAttribute("success", "Persona actualizada correctamente");
            }
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

    @PostMapping("/usuarios/persona/{id}/eliminar")
    public String eliminarPersona(@PathVariable String id, RedirectAttributes ra) {
        try {
            personaService.baja(id);
            ra.addFlashAttribute("success", "Persona eliminada");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

    // --- Clientes ---
    @PostMapping("/usuarios/cliente")
    public String guardarCliente(@ModelAttribute("clienteForm") ClienteDTO dto, RedirectAttributes ra) {
        try {
            if (dto.getId() == null || dto.getId().isBlank()) {
                clienteService.alta(dto);
                ra.addFlashAttribute("success", "Cliente creado correctamente");
            } else {
                clienteService.modificar(dto.getId(), dto);
                ra.addFlashAttribute("success", "Cliente actualizado correctamente");
            }
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

    @PostMapping("/usuarios/cliente/{id}/eliminar")
    public String eliminarCliente(@PathVariable String id, RedirectAttributes ra) {
        try {
            clienteService.baja(id);
            ra.addFlashAttribute("success", "Cliente eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

    // --- Empleados ---
    @PostMapping("/usuarios/empleado")
    public String guardarEmpleado(@ModelAttribute("empleadoForm") EmpleadoDTO dto, RedirectAttributes ra) {
        try {
            if (dto.getId() == null || dto.getId().isBlank()) {
                empleadoService.alta(dto);
                ra.addFlashAttribute("success", "Empleado creado correctamente");
            } else {
                empleadoService.modificar(dto.getId(), dto);
                ra.addFlashAttribute("success", "Empleado actualizado correctamente");
            }
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

    @PostMapping("/usuarios/empleado/{id}/eliminar")
    public String eliminarEmpleado(@PathVariable String id, RedirectAttributes ra) {
        try {
            empleadoService.baja(id);
            ra.addFlashAttribute("success", "Empleado eliminado");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/usuarios";
    }

}
