package com.is.biblioteca.controller.view;

import com.is.biblioteca.business.domain.entity.Vehiculo;
import com.is.biblioteca.business.logic.service.VehiculoService;
import com.is.biblioteca.business.logic.service.ClienteService;
import com.is.biblioteca.business.domain.enumeration.BaseUseCaseController;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController extends BaseController<Vehiculo, String> {
    
    @Autowired
    private ClienteService clienteService;
    
    public VehiculoController(VehiculoService service, ApplicationContext context) {
        super(service, context);
        initController(new Vehiculo(), "Lista de Vehículos", "Vehículo");
    }
    
    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Cargar lista de clientes para el combo box en el formulario
        if (useCase == BaseUseCaseController.ALTA || useCase == BaseUseCaseController.MODIFICACION) {
            model.addAttribute("clientes", clienteService.listarActivos());
        }
    }
}
