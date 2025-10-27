package org.sistemaMecanico.controller;

import org.sistemaMecanico.entity.Vehiculo;
import org.sistemaMecanico.service.VehiculoService;
import org.sistemaMecanico.service.ClienteService;
import org.sistemaMecanico.enums.BaseUseCaseController;
import org.sistemaMecanico.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController extends BaseController<Vehiculo, String> {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    public VehiculoController(VehiculoService service) {
        super(service);
        initController(new Vehiculo(), "Listado de Vehículos", "Gestión de Vehículo");
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Cargar lista de clientes para el formulario
        switch(useCase) {
            case ALTA:
            case MODIFICACION:
            case CONSULTAR:
                this.model.addAttribute("clientes", clienteService.listarActivos());
                break;
            default:
                break;
        }
    }

    @Override
    protected Vehiculo loadingEntityForFormByError(BaseUseCaseController useCase, Model model, Vehiculo entity) throws ErrorServiceException {
        // Cargar datos adicionales cuando hay un error en el formulario
        this.model.addAttribute("clientes", clienteService.listarActivos());
        return entity;
    }
}
