package com.is.biblioteca.controller.view;

import com.is.biblioteca.business.domain.entity.Cliente;
import com.is.biblioteca.business.logic.service.ClienteService;
import com.is.biblioteca.business.domain.enumeration.BaseUseCaseController;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.controller.BaseController;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController extends BaseController<Cliente, String> {
    
    public ClienteController(ClienteService service, ApplicationContext context) {
        super(service, context);
        initController(new Cliente(), "Lista de Clientes", "Cliente");
    }
    
    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Aquí se pueden agregar validaciones o lógica adicional antes de cada operación
    }
}
