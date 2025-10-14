package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.BaseEntity;
import com.example.greedy_empresa.servicios.BaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Clase base abstracta que implementa el patrón Template Method para controladores CRUD.
 * Define el flujo común para operaciones de listar, crear, editar y eliminar entidades.
 */
public abstract class BaseController<T extends BaseEntity, S extends BaseService<T, ?>> {

    protected final S service;

    public BaseController(S service) {
        this.service = service;
    }

    // ========== Métodos abstractos - Las subclases DEBEN implementar ==========

    /**
     * Retorna el nombre del menú activo para la navegación
     */
    protected abstract String getActiveMenu();

    /**
     * Retorna la ruta base de las vistas (ej: "empresas", "proveedores", "usuarios")
     */
    protected abstract String getBasePath();

    /**
     * Retorna el nombre del atributo del modelo (ej: "empresa", "proveedor", "usuario")
     */
    protected abstract String getModelAttributeName();

    /**
     * Inicializa una nueva entidad con valores por defecto
     */
    protected abstract T crearNuevaEntidad();

    // ========== Hook Methods - Las subclases pueden sobrescribir ==========

    /**
     * Hook: Agregar datos adicionales al modelo para el listado
     */
    protected void agregarDatosAdicionalesListado(Model model) {
        // Por defecto no hace nada
    }

    /**
     * Hook: Agregar datos adicionales al modelo para el formulario
     */
    protected void agregarDatosAdicionalesFormulario(Model model) {
        // Por defecto no hace nada
    }

    /**
     * Hook: Validaciones adicionales antes de guardar
     */
    protected void validacionesAdicionales(T entidad, BindingResult bindingResult) {
        // Por defecto no hace nada
    }

    /**
     * Hook: Preparar entidad antes de mostrar en el formulario de edición
     */
    protected void prepararEntidadParaEdicion(T entidad) {
        // Por defecto no hace nada
    }

    /**
     * Hook: Mensaje de éxito personalizado al crear
     */
    protected String getMensajeExitoCrear() {
        return getModelAttributeName() + " guardado/a correctamente";
    }

    /**
     * Hook: Mensaje de éxito personalizado al actualizar
     */
    protected String getMensajeExitoActualizar() {
        return getModelAttributeName() + " actualizado/a correctamente";
    }

    /**
     * Hook: Mensaje de éxito personalizado al eliminar
     */
    protected String getMensajeExitoEliminar() {
        return getModelAttributeName() + " eliminado/a correctamente";
    }

    // ========== Template Methods - Definen el flujo de operaciones ==========

    /**
     * Template Method: Listar entidades con paginación y filtro
     */
    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
                        @PageableDefault(size = 10) Pageable pageable,
                        Model model) {
        model.addAttribute("page", service.buscar(filtro, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("activeMenu", getActiveMenu());
        agregarDatosAdicionalesListado(model);
        return getBasePath() + "/list";
    }

    /**
     * Template Method: Mostrar formulario para nueva entidad
     */
    @GetMapping("/new")
    public String nuevo(Model model) {
        T entidad = crearNuevaEntidad();
        model.addAttribute(getModelAttributeName(), entidad);
        model.addAttribute("activeMenu", getActiveMenu());
        agregarDatosAdicionalesFormulario(model);
        return getBasePath() + "/form";
    }

    /**
     * Template Method: Crear nueva entidad
     */
    @PostMapping
    public String crear(@Valid @ModelAttribute T entidad,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        
        // Validaciones adicionales
        validacionesAdicionales(entidad, bindingResult);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", getActiveMenu());
            agregarDatosAdicionalesFormulario(model);
            return getBasePath() + "/form";
        }
        
        try {
            service.guardar(entidad);
            redirectAttributes.addFlashAttribute("successMessage", getMensajeExitoCrear());
            return "redirect:/" + getBasePath();
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", getActiveMenu());
            agregarDatosAdicionalesFormulario(model);
            return getBasePath() + "/form";
        }
    }

    /**
     * Template Method: Mostrar formulario de edición
     */
    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        T entidad = service.buscarPorId(id);
        prepararEntidadParaEdicion(entidad);
        model.addAttribute(getModelAttributeName(), entidad);
        model.addAttribute("activeMenu", getActiveMenu());
        agregarDatosAdicionalesFormulario(model);
        return getBasePath() + "/form";
    }

    /**
     * Template Method: Actualizar entidad existente
     */
    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
                            @Valid @ModelAttribute T entidad,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        // Validaciones adicionales
        validacionesAdicionales(entidad, bindingResult);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", getActiveMenu());
            agregarDatosAdicionalesFormulario(model);
            return getBasePath() + "/form";
        }
        
        try {
            entidad.setId(id);
            service.guardar(entidad);
            redirectAttributes.addFlashAttribute("successMessage", getMensajeExitoActualizar());
            return "redirect:/" + getBasePath();
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("activeMenu", getActiveMenu());
            agregarDatosAdicionalesFormulario(model);
            return getBasePath() + "/form";
        }
    }

    /**
     * Template Method: Eliminar entidad
     */
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        service.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", getMensajeExitoEliminar());
        return "redirect:/" + getBasePath();
    }

    /**
     * Método auxiliar para agregar atributos comunes en todos los métodos
     */
    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("activeMenu", getActiveMenu());
    }
}
