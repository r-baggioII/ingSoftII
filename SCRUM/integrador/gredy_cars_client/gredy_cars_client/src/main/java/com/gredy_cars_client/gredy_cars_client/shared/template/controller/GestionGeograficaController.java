package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DepartamentoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.LocalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PaisDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ProvinciaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DepartamentoService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.DireccionService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.LocalidadService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.PaisService;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.ProvinciaService;

@Controller
@RequestMapping("/gestion")
public class GestionGeograficaController {

    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;
    private final DireccionService direccionService;

    public GestionGeograficaController(
        PaisService paisService,
        ProvinciaService provinciaService,
        DepartamentoService departamentoService,
        LocalidadService localidadService,
        DireccionService direccionService
    ) {
        this.paisService = paisService;
        this.provinciaService = provinciaService;
        this.departamentoService = departamentoService;
        this.localidadService = localidadService;
        this.direccionService = direccionService;
    }

    @GetMapping("/direcciones")
    public String gestionarDirecciones(
        @RequestParam(value = "editPais", required = false) Long editPaisId,
        @RequestParam(value = "editProvincia", required = false) Long editProvinciaId,
        @RequestParam(value = "editDepartamento", required = false) Long editDepartamentoId,
        @RequestParam(value = "editLocalidad", required = false) Long editLocalidadId,
        @RequestParam(value = "editDireccion", required = false) Long editDireccionId,
        Model model
    ) throws ErrorServiceException {
        // Listas
        model.addAttribute("paises", paisService.listarActivos());
        model.addAttribute("provincias", provinciaService.listarActivos());
        model.addAttribute("departamentos", departamentoService.listarActivos());
        model.addAttribute("localidades", localidadService.listarActivos());
        model.addAttribute("direcciones", direccionService.listarActivos());

        // Formularios (crear/editar)
        model.addAttribute("paisForm", editPaisId != null ?
            paisService.obtener(editPaisId).orElseGet(PaisDTO::new) : new PaisDTO());

        ProvinciaDTO provForm = editProvinciaId != null ?
            provinciaService.obtener(editProvinciaId).orElseGet(ProvinciaDTO::new) : new ProvinciaDTO();
        if (provForm.getPais() == null) provForm.setPais(new PaisDTO());
        model.addAttribute("provinciaForm", provForm);

        DepartamentoDTO depForm = editDepartamentoId != null ?
            departamentoService.obtener(editDepartamentoId).orElseGet(DepartamentoDTO::new) : new DepartamentoDTO();
        if (depForm.getProvincia() == null) depForm.setProvincia(new ProvinciaDTO());
        model.addAttribute("departamentoForm", depForm);

        LocalidadDTO locForm = editLocalidadId != null ?
            localidadService.obtener(editLocalidadId).orElseGet(LocalidadDTO::new) : new LocalidadDTO();
        if (locForm.getDepartamento() == null) locForm.setDepartamento(new DepartamentoDTO());
        model.addAttribute("localidadForm", locForm);

        DireccionDTO dirForm = editDireccionId != null ?
            direccionService.obtener(editDireccionId).orElseGet(DireccionDTO::new) : new DireccionDTO();
        if (dirForm.getLocalidad() == null) dirForm.setLocalidad(new LocalidadDTO());
        model.addAttribute("direccionForm", dirForm);

        return "gestion/gestion-direcciones";
    }

    // País
    @PostMapping("/direcciones/paises")
    public String guardarPais(@ModelAttribute("paisForm") PaisDTO pais,
                              RedirectAttributes ra) {
        try {
            if (pais.getId() == null) paisService.alta(pais); else paisService.modificar(pais.getId(), pais);
            ra.addFlashAttribute("success", "País guardado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/direcciones";
    }

    @PostMapping("/direcciones/paises/{id}/eliminar")
    public String eliminarPais(@PathVariable Long id, RedirectAttributes ra) {
        try { paisService.baja(id); ra.addFlashAttribute("success", "País eliminado"); }
        catch (ErrorServiceException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/gestion/direcciones";
    }

    // Provincia
    @PostMapping("/direcciones/provincias")
    public String guardarProvincia(@ModelAttribute("provinciaForm") ProvinciaDTO provincia,
                                   RedirectAttributes ra) {
        try {
            if (provincia.getId() == null) provinciaService.alta(provincia); else provinciaService.modificar(provincia.getId(), provincia);
            ra.addFlashAttribute("success", "Provincia guardada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/direcciones";
    }

    @PostMapping("/direcciones/provincias/{id}/eliminar")
    public String eliminarProvincia(@PathVariable Long id, RedirectAttributes ra) {
        try { provinciaService.baja(id); ra.addFlashAttribute("success", "Provincia eliminada"); }
        catch (ErrorServiceException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/gestion/direcciones";
    }

    // Departamento
    @PostMapping("/direcciones/departamentos")
    public String guardarDepartamento(@ModelAttribute("departamentoForm") DepartamentoDTO departamento,
                                      RedirectAttributes ra) {
        try {
            if (departamento.getId() == null) departamentoService.alta(departamento); else departamentoService.modificar(departamento.getId(), departamento);
            ra.addFlashAttribute("success", "Departamento guardado correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/direcciones";
    }

    @PostMapping("/direcciones/departamentos/{id}/eliminar")
    public String eliminarDepartamento(@PathVariable Long id, RedirectAttributes ra) {
        try { departamentoService.baja(id); ra.addFlashAttribute("success", "Departamento eliminado"); }
        catch (ErrorServiceException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/gestion/direcciones";
    }

    // Localidad
    @PostMapping("/direcciones/localidades")
    public String guardarLocalidad(@ModelAttribute("localidadForm") LocalidadDTO localidad,
                                   RedirectAttributes ra) {
        try {
            if (localidad.getId() == null) localidadService.alta(localidad); else localidadService.modificar(localidad.getId(), localidad);
            ra.addFlashAttribute("success", "Localidad guardada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/direcciones";
    }

    @PostMapping("/direcciones/localidades/{id}/eliminar")
    public String eliminarLocalidad(@PathVariable Long id, RedirectAttributes ra) {
        try { localidadService.baja(id); ra.addFlashAttribute("success", "Localidad eliminada"); }
        catch (ErrorServiceException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/gestion/direcciones";
    }

    // Dirección
    @PostMapping("/direcciones/direcciones")
    public String guardarDireccion(@ModelAttribute("direccionForm") DireccionDTO direccion,
                                   RedirectAttributes ra) {
        try {
            if (direccion.getId() == null) direccionService.alta(direccion); else direccionService.modificar(direccion.getId(), direccion);
            ra.addFlashAttribute("success", "Dirección guardada correctamente");
        } catch (ErrorServiceException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gestion/direcciones";
    }

    @PostMapping("/direcciones/direcciones/{id}/eliminar")
    public String eliminarDireccion(@PathVariable Long id, RedirectAttributes ra) {
        try { direccionService.baja(id); ra.addFlashAttribute("success", "Dirección eliminada"); }
        catch (ErrorServiceException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/gestion/direcciones";
    }
}
