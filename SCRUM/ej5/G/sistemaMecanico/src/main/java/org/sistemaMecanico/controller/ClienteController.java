package org.sistemaMecanico.controller;

import org.sistemaMecanico.entity.Cliente;
import org.sistemaMecanico.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController extends BaseController<Cliente, String> {

    @Autowired
    public ClienteController(ClienteService service) {
        super(service);
        initController(new Cliente(), "Listado de Clientes", "Gestión de Cliente");
    }

    // Aquí puedes agregar métodos personalizados si los necesitas
    // O sobrescribir los hooks del BaseController:

    /*
    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Lógica antes de ejecutar un caso de uso
        // Por ejemplo: cargar listas para dropdowns en el formulario
        switch(useCase) {
            case ALTA:
            case MODIFICACION:
                // Cargar datos adicionales para el formulario
                // this.model.addAttribute("vehiculos", vehiculoService.listarActivos());
                break;
        }
    }

    @Override
    protected void postUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Lógica después de ejecutar un caso de uso
        // Por ejemplo: enviar notificaciones, logs, etc.
    }

    @Override
    protected Cliente loadingEntityForFormByError(BaseUseCaseController useCase, Model model, Cliente entity) throws ErrorServiceException {
        // Cargar datos adicionales cuando hay un error en el formulario
        // this.model.addAttribute("vehiculos", vehiculoService.listarActivos());
        return entity;
    }
    */
}