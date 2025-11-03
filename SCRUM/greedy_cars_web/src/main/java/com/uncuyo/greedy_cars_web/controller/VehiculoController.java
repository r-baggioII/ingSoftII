package com.uncuyo.greedy_cars_web.controller;

import com.uncuyo.greedy_cars_web.dto.VehiculoDTO;
import com.uncuyo.greedy_cars_web.exception.ErrorServiceException;
import com.uncuyo.greedy_cars_web.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para Vehículos
 * Maneja la navegación y renderización de vistas HTML
 * NO retorna JSON, solo vistas Thymeleaf
 */
@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    /**
     * Listar todos los vehículos
     * GET http://localhost:8081/vehiculos
     */
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("vehiculos", vehiculoService.listarTodos());
            model.addAttribute("titulo", "Lista de Vehículos");
            return "vehiculo/lista"; // Retorna templates/vehiculo/lista.html
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "error"; // Retorna templates/error.html
        }
    }

    /**
     * Listar vehículos por estado
     * GET http://localhost:8081/vehiculos/estado/DISPONIBLE
     */
    @GetMapping("/estado/{estado}")
    public String listarPorEstado(@PathVariable String estado, Model model) {
        try {
            model.addAttribute("vehiculos", vehiculoService.listarPorEstado(estado));
            model.addAttribute("titulo", "Vehículos - Estado: " + estado);
            model.addAttribute("estadoFiltro", estado);
            return "vehiculo/lista";
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Mostrar detalle de un vehículo
     * GET http://localhost:8081/vehiculos/ver/{id}
     */
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable String id, Model model) {
        try {
            VehiculoDTO vehiculo = vehiculoService.obtenerPorId(id);
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("titulo", "Detalle del Vehículo");
            return "vehiculo/detalle"; // Retorna templates/vehiculo/detalle.html
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Mostrar formulario para crear nuevo vehículo
     * GET http://localhost:8081/vehiculos/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("vehiculo", new VehiculoDTO());
        model.addAttribute("titulo", "Crear Nuevo Vehículo");
        model.addAttribute("accion", "crear");
        return "vehiculo/formulario"; // Retorna templates/vehiculo/formulario.html
    }

    /**
     * Procesar creación de vehículo
     * POST http://localhost:8081/vehiculos/crear
     */
    @PostMapping("/crear")
    public String crear(@ModelAttribute VehiculoDTO vehiculoDTO, 
                       RedirectAttributes redirectAttributes) {
        try {
            VehiculoDTO vehiculoCreado = vehiculoService.crear(vehiculoDTO);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Vehículo creado exitosamente con patente: " + vehiculoCreado.getPatente());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/vehiculos";
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/vehiculos/nuevo";
        }
    }

    /**
     * Mostrar formulario para editar vehículo
     * GET http://localhost:8081/vehiculos/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable String id, Model model) {
        try {
            VehiculoDTO vehiculo = vehiculoService.obtenerPorId(id);
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("titulo", "Editar Vehículo");
            model.addAttribute("accion", "editar");
            return "vehiculo/formulario";
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Procesar actualización de vehículo
     * POST http://localhost:8081/vehiculos/actualizar/{id}
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable String id,
                            @ModelAttribute VehiculoDTO vehiculoDTO,
                            RedirectAttributes redirectAttributes) {
        try {
            vehiculoService.actualizar(id, vehiculoDTO);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Vehículo actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/vehiculos/ver/" + id;
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/vehiculos/editar/" + id;
        }
    }

    /**
     * Eliminar vehículo
     * GET http://localhost:8081/vehiculos/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            vehiculoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Vehículo eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/vehiculos";
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/vehiculos";
        }
    }

    /**
     * Buscar vehículo por patente
     * GET http://localhost:8081/vehiculos/buscar?patente=ABC123
     */
    @GetMapping("/buscar")
    public String buscarPorPatente(@RequestParam String patente, Model model) {
        try {
            VehiculoDTO vehiculo = vehiculoService.buscarPorPatente(patente);
            return "redirect:/vehiculos/ver/" + vehiculo.getId();
        } catch (ErrorServiceException e) {
            try {
                model.addAttribute("error", "No se encontró vehículo con patente: " + patente);
                model.addAttribute("vehiculos", vehiculoService.listarTodos());
                return "vehiculo/lista";
            } catch (ErrorServiceException ex) {
                model.addAttribute("error", ex.getMessage());
                return "error";
            }
        }
    }
}
