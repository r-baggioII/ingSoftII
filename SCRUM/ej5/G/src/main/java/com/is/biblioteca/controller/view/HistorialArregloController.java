package com.is.biblioteca.controller.view;

import com.is.biblioteca.business.domain.entity.HistorialArreglo;
import com.is.biblioteca.business.logic.service.HistorialArregloService;
import com.is.biblioteca.business.logic.service.VehiculoService;
import com.is.biblioteca.business.logic.service.MecanicoService;
import com.is.biblioteca.business.domain.enumeration.BaseUseCaseController;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/historialarreglo")
public class HistorialArregloController extends BaseController<HistorialArreglo, String> {
    
    @Autowired
    private VehiculoService vehiculoService;
    
    @Autowired
    private MecanicoService mecanicoService;
    
    public HistorialArregloController(HistorialArregloService service, ApplicationContext context) {
        super(service, context);
        initController(new HistorialArreglo(), "Historial de Arreglos", "Historial de Arreglo");
    }
    
    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Cargar listas para los combo boxes en el formulario
        if (useCase == BaseUseCaseController.ALTA || useCase == BaseUseCaseController.MODIFICACION) {
            model.addAttribute("vehiculos", vehiculoService.listarActivos());
            model.addAttribute("mecanicos", mecanicoService.listarActivos());
        }
    }
}
