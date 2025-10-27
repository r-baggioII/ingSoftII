package org.sistemaMecanico.controller;

import org.sistemaMecanico.entity.HistorialArreglo;
import org.sistemaMecanico.service.HistorialArregloService;
import org.sistemaMecanico.service.VehiculoService;
import org.sistemaMecanico.service.MecanicoService;
import org.sistemaMecanico.enums.BaseUseCaseController;
import org.sistemaMecanico.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/historialarreglo")
@Transactional(readOnly = true)
public class HistorialArregloController extends BaseController<HistorialArreglo, String> {

    private final VehiculoService vehiculoService;
    private final MecanicoService mecanicoService;

    @Autowired
    public HistorialArregloController(HistorialArregloService service, 
                                      VehiculoService vehiculoService,
                                      MecanicoService mecanicoService) {
        super(service);
        this.vehiculoService = vehiculoService;
        this.mecanicoService = mecanicoService;
        initController(new HistorialArreglo(), "Listado de Historial de Arreglos", "Gestión de Historial de Arreglo");
    }

    /**
     * Sobrescribir el método list para inicializar las colecciones lazy
     * dentro de la transacción activa
     */
    @Override
    @GetMapping("/list")
    public String list(Model model) {
        try {
            this.model = model;
            List<HistorialArreglo> listEntity = ((HistorialArregloService) service).listarActivos();
            
            // Inicializar las colecciones lazy dentro de la transacción
            listEntity.forEach(historial -> {
                // Forzar la inicialización de la colección mecanicos
                historial.getMecanicos().size();
            });
            
            this.model.addAttribute("items", listEntity);
            this.model.addAttribute("titleList", titleList);
            this.model.addAttribute("nameEntityLower", nameEntityLower);

        } catch (ErrorServiceException e) {
            showErrorMenssage(e.getMessage(), BaseUseCaseController.LISTAR);
        } catch (Exception e) {
            this.model.addAttribute("msgError", "Error de Sistema");
            showErrorMenssage(e.getMessage(), BaseUseCaseController.LISTAR);
        }

        return viewList;
    }

    /**
     * Sobrescribir editar para inicializar la colección de mecánicos
     */
    @Override
    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            this.model = model;
            this.entity = service.obtener(id).orElseThrow(() -> new IllegalArgumentException("No encontrado: " + id));
            
            // Inicializar la colección de mecánicos dentro de la transacción
            this.entity.getMecanicos().size();
            
            this.model.addAttribute("item", entity);
            this.model.addAttribute("isDisabled", false);
            this.model.addAttribute("titleEdit", titleEdit);
            this.model.addAttribute("nameEntityLower", nameEntityLower);
            preUseCase(BaseUseCaseController.MODIFICACION);

            return viewEdit;

        } catch (ErrorServiceException e) {
            showErrorMenssage(e.getMessage(), BaseUseCaseController.MODIFICACION);
            return viewEdit;
        } catch (Exception e) {
            showErrorMenssage(e.getMessage(), BaseUseCaseController.MODIFICACION);
            return viewEdit;
        }
    }

    /**
     * Sobrescribir consultar para inicializar la colección de mecánicos
     */
    @Override
    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            this.model = model;
            this.entity = service.obtener(id).orElseThrow(() -> new IllegalArgumentException("No encontrado: " + id));
            
            // Inicializar la colección de mecánicos dentro de la transacción
            this.entity.getMecanicos().size();
            
            this.model.addAttribute("item", entity);
            this.model.addAttribute("isDisabled", true);
            this.model.addAttribute("titleEdit", titleEdit);
            this.model.addAttribute("nameEntityLower", nameEntityLower);

            preUseCase(BaseUseCaseController.CONSULTAR);

            return viewEdit;

        } catch (ErrorServiceException e) {
            showErrorMenssage(e.getMessage(), BaseUseCaseController.CONSULTAR);
            return viewEdit;
        } catch (Exception e) {
            showErrorMenssage(e.getMessage(), BaseUseCaseController.CONSULTAR);
            return viewEdit;
        }
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Cargar listas para dropdowns en el formulario
        switch(useCase) {
            case ALTA:
            case MODIFICACION:
                // Cargar vehículos y mecánicos para los selectores
                this.model.addAttribute("vehiculos", vehiculoService.listarActivos());
                this.model.addAttribute("mecanicos", mecanicoService.listarActivos());
                break;
            default:
                break;
        }
    }

    /**
     * Hook para procesar la entidad antes de guardarla
     * Convierte el ID del vehículo en un objeto Vehiculo
     */
    @Override
    protected void postUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        if (useCase == BaseUseCaseController.ACTUALIZAR) {
            // El binding de Spring debería manejar esto automáticamente
            // pero si hay problemas, aquí podemos procesarlo
        }
    }

    @Override
    protected HistorialArreglo loadingEntityForFormByError(BaseUseCaseController useCase, Model model, HistorialArreglo entity) throws ErrorServiceException {
        // Cargar datos adicionales cuando hay un error en el formulario
        this.model.addAttribute("vehiculos", vehiculoService.listarActivos());
        this.model.addAttribute("mecanicos", mecanicoService.listarActivos());
        return entity;
    }

    /**
     * Método personalizado para actualizar historial de arreglo con conversión manual
     * de fecha y vehículo desde el formulario HTML
     */
    @Transactional
    @PostMapping("/actualizarPersonalizado")
    public String actualizarPersonalizado(
            @RequestParam("id") String id,
            @RequestParam("fechaArreglo") String fechaArreglo,
            @RequestParam("vehiculo.id") String vehiculoId,
            @RequestParam("detalleArreglo") String detalleArreglo,
            @RequestParam(value = "mecanicosIds", required = false) List<String> mecanicosIds,
            RedirectAttributes attributes,
            Model model) {
        
        try {
            this.model = model;
            
            // Crear o recuperar la entidad
            HistorialArreglo historial;
            if (id == null || id.isEmpty()) {
                historial = new HistorialArreglo();
            } else {
                historial = service.obtenerEntidad(id);
            }
            
            // Configurar los campos
            historial.setFechaArreglo(java.sql.Date.valueOf(fechaArreglo));
            historial.setDetalleArreglo(detalleArreglo);
            
            // Configurar el vehículo
            if (vehiculoId != null && !vehiculoId.isEmpty()) {
                historial.setVehiculo(vehiculoService.obtenerEntidad(vehiculoId));
            }
            
            // Configurar los mecánicos
            if (mecanicosIds != null && !mecanicosIds.isEmpty()) {
                historial.getMecanicos().clear();
                for (String mecanicoId : mecanicosIds) {
                    historial.getMecanicos().add(mecanicoService.obtenerEntidad(mecanicoId));
                }
            } else {
                // Si no hay mecánicos seleccionados, limpiar la colección
                historial.getMecanicos().clear();
            }
            
            // Guardar
            if (id == null || id.isEmpty()) {
                service.alta(historial);
            } else {
                service.modificar(id, historial);
            }
            
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return redirectList;
            
        } catch (Exception e) {
            this.model.addAttribute("msgError", e.getMessage());
            this.model.addAttribute("vehiculos", vehiculoService.listarActivos());
            this.model.addAttribute("mecanicos", mecanicoService.listarActivos());
            return viewEdit;
        }
    }
}
