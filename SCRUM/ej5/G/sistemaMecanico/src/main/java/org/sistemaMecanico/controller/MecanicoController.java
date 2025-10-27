package org.sistemaMecanico.controller;

import org.sistemaMecanico.entity.Mecanico;
import org.sistemaMecanico.service.MecanicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mecanico")
public class MecanicoController extends BaseController<Mecanico, String> {

    @Autowired
    public MecanicoController(MecanicoService service) {
        super(service);
        initController(new Mecanico(), "Listado de Mecánicos", "Gestión de Mecánico");
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
                break;
        }
    }

    @Override
    protected void postUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        // Lógica después de ejecutar un caso de uso
        // Por ejemplo: enviar notificaciones, logs, etc.
    }

    @Override
    protected Mecanico loadingEntityForFormByError(BaseUseCaseController useCase, Model model, Mecanico entity) throws ErrorServiceException {
        // Cargar datos adicionales cuando hay un error en el formulario
        return entity;
    }
    */
}
