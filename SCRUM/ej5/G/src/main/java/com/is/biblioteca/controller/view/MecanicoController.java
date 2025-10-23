package com.is.biblioteca.controller.view;

import com.is.biblioteca.business.domain.entity.Mecanico;
import com.is.biblioteca.business.logic.service.MecanicoService;
import com.is.biblioteca.business.logic.service.UsuarioService;
import com.is.biblioteca.business.domain.enumeration.BaseUseCaseController;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mecanico")
public class MecanicoController extends BaseController<Mecanico, String> {
    
    @Autowired
    private UsuarioService usuarioService;
    
    public MecanicoController(MecanicoService service, ApplicationContext context) {
        super(service, context);
        initController(new Mecanico(), "Lista de Mecánicos", "Mecánico");
    }
    
    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Cargar lista de usuarios para el combo box en el formulario
        if (useCase == BaseUseCaseController.ALTA || useCase == BaseUseCaseController.MODIFICACION) {
            model.addAttribute("usuarios", usuarioService.listarActivos());
        }
    }
}
