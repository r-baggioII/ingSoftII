package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.ArrayList;
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

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoTelefonicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.CanalContacto;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoContacto;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoTelefono;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoCorreoElectronicoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ContactoTelefonicoService;

@Controller
@RequestMapping("/gestion")
public class GestionContactosController {

    private final ContactoCorreoElectronicoService correoService;
    private final ContactoTelefonicoService telefonicoService;

    public GestionContactosController(
        ContactoCorreoElectronicoService correoService,
        ContactoTelefonicoService telefonicoService
    ) {
        this.correoService = correoService;
        this.telefonicoService = telefonicoService;
    }

    @ModelAttribute("tiposContacto")
    public TipoContacto[] tiposContacto() {
        return TipoContacto.values();
    }

    @ModelAttribute("tiposTelefono")
    public TipoTelefono[] tiposTelefono() {
        return TipoTelefono.values();
    }

    @ModelAttribute("canalesContacto")
    public CanalContacto[] canalesContacto() {
        return CanalContacto.values();
    }

    @GetMapping("/contactos")
    public String gestionarContactos(
        @RequestParam(value = "editId", required = false) String editId,
        @RequestParam(value = "canal", required = false) CanalContacto canal,
        Model model
    ) throws ErrorServiceException {
        List<ContactoRow> registros = new ArrayList<>();

        List<ContactoCorreoElectronicoDTO> correos = correoService.listarActivos();
        correos.forEach(c -> registros.add(
            new ContactoRow(
                c.getId(),
                CanalContacto.CORREO,
                c.getMail(),
                c.getTipoContacto(),
                null,
                c.getPersonaId(),
                c.getObservacion()
            )
        ));

        List<ContactoTelefonicoDTO> telefonos = telefonicoService.listarActivos();
        telefonos.forEach(t -> registros.add(
            new ContactoRow(
                t.getId(),
                CanalContacto.TELEFONO,
                t.getTelefono(),
                t.getTipoContacto(),
                t.getTipoTelefono(),
                t.getPersonaId(),
                t.getObservacion()
            )
        ));

        ContactoForm form = new ContactoForm();
        if (editId != null && !editId.isBlank()) {
            if (canal == null) {
                canal = registros.stream()
                    .filter(r -> r.id().equals(editId))
                    .map(ContactoRow::canal)
                    .findFirst()
                    .orElse(null);
            }

            if (canal == CanalContacto.CORREO) {
                Optional<ContactoCorreoElectronicoDTO> correo = correoService.obtener(editId);
                correo.ifPresent(form::fromCorreo);
            } else if (canal == CanalContacto.TELEFONO) {
                Optional<ContactoTelefonicoDTO> telefono = telefonicoService.obtener(editId);
                telefono.ifPresent(form::fromTelefono);
            }
        }

        if (form.getCanal() == null) {
            form.setCanal(CanalContacto.CORREO);
        }

        model.addAttribute("contactos", registros);
        model.addAttribute("contactoForm", form);

        return "gestion/gestion-contactos";
    }

    @PostMapping("/contactos")
    public String guardar(@ModelAttribute("contactoForm") ContactoForm contacto, RedirectAttributes ra) {
        try {
            if (contacto.getCanal() == CanalContacto.CORREO) {
                ContactoCorreoElectronicoDTO dto = contacto.toCorreoDTO();

                if (dto.getId() == null || dto.getId().isBlank()) {
                    correoService.alta(dto);
                    ra.addFlashAttribute("success", "Contacto de correo guardado correctamente");
                } else {
                    correoService.modificar(dto.getId(), dto);
                    ra.addFlashAttribute("success", "Contacto de correo actualizado correctamente");
                }
            } else if (contacto.getCanal() == CanalContacto.TELEFONO) {
                ContactoTelefonicoDTO dto = contacto.toTelefonoDTO();

                if (dto.getId() == null || dto.getId().isBlank()) {
                    telefonicoService.alta(dto);
                    ra.addFlashAttribute("success", "Contacto telefónico guardado correctamente");
                } else {
                    telefonicoService.modificar(dto.getId(), dto);
                    ra.addFlashAttribute("success", "Contacto telefónico actualizado correctamente");
                }
            } else {
                ra.addFlashAttribute("error", "Debe seleccionar un canal de contacto válido");
            }
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/contactos";
    }

    @PostMapping("/contactos/{canal}/{id}/eliminar")
    public String eliminar(@PathVariable CanalContacto canal, @PathVariable String id, RedirectAttributes ra) {
        try {
            if (canal == CanalContacto.CORREO) {
                correoService.baja(id);
            } else if (canal == CanalContacto.TELEFONO) {
                telefonicoService.baja(id);
            } else {
                ra.addFlashAttribute("error", "Canal de contacto desconocido");
                return "redirect:/gestion/contactos";
            }
            ra.addFlashAttribute("success", "Contacto eliminado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/contactos";
    }
    private static final class ContactoForm {

        private String id;
        private CanalContacto canal;
        private TipoContacto tipoContacto;
        private String personaId;
        private String observacion;
        private String mail;
        private String telefono;
        private TipoTelefono tipoTelefono;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public CanalContacto getCanal() {
            return canal;
        }

        public void setCanal(CanalContacto canal) {
            this.canal = canal;
        }

        public TipoContacto getTipoContacto() {
            return tipoContacto;
        }

        public void setTipoContacto(TipoContacto tipoContacto) {
            this.tipoContacto = tipoContacto;
        }

        public String getPersonaId() {
            return personaId;
        }

        public void setPersonaId(String personaId) {
            this.personaId = personaId;
        }

        public String getObservacion() {
            return observacion;
        }

        public void setObservacion(String observacion) {
            this.observacion = observacion;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public TipoTelefono getTipoTelefono() {
            return tipoTelefono;
        }

        public void setTipoTelefono(TipoTelefono tipoTelefono) {
            this.tipoTelefono = tipoTelefono;
        }

        void fromCorreo(ContactoCorreoElectronicoDTO dto) {
            this.id = dto.getId();
            this.canal = CanalContacto.CORREO;
            this.mail = dto.getMail();
            this.tipoContacto = dto.getTipoContacto();
            this.observacion = dto.getObservacion();
            this.personaId = dto.getPersonaId();
            this.telefono = null;
            this.tipoTelefono = null;
        }

        void fromTelefono(ContactoTelefonicoDTO dto) {
            this.id = dto.getId();
            this.canal = CanalContacto.TELEFONO;
            this.telefono = dto.getTelefono();
            this.tipoTelefono = dto.getTipoTelefono();
            this.tipoContacto = dto.getTipoContacto();
            this.observacion = dto.getObservacion();
            this.personaId = dto.getPersonaId();
            this.mail = null;
        }

        ContactoCorreoElectronicoDTO toCorreoDTO() {
            ContactoCorreoElectronicoDTO dto = new ContactoCorreoElectronicoDTO();
            dto.setId(id);
            dto.setMail(mail);
            dto.setTipoContacto(tipoContacto);
            dto.setObservacion(observacion);
            dto.setPersonaId(personaId);
            return dto;
        }

        ContactoTelefonicoDTO toTelefonoDTO() {
            ContactoTelefonicoDTO dto = new ContactoTelefonicoDTO();
            dto.setId(id);
            dto.setTelefono(telefono);
            dto.setTipoTelefono(tipoTelefono);
            dto.setTipoContacto(tipoContacto);
            dto.setObservacion(observacion);
            dto.setPersonaId(personaId);
            return dto;
        }
    }

    private record ContactoRow(
        String id,
        CanalContacto canal,
        String valorPrincipal,
        TipoContacto tipoContacto,
        TipoTelefono tipoTelefono,
        String personaId,
        String observacion
    ) {}
}
