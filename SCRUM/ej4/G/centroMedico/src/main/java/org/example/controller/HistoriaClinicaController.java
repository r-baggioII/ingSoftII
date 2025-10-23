package org.example.controller;

import org.example.entity.HistoriaClinica;
import org.example.service.HistoriaClinicaService;
import org.example.service.PacienteService;
import org.example.enums.BaseUseCaseController;
import org.example.exception.ErrorServiceException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/historiaclinica")
public class HistoriaClinicaController extends BaseController<HistoriaClinica, String> {

    private final PacienteService pacienteService;

    public HistoriaClinicaController(HistoriaClinicaService service, 
                                     PacienteService pacienteService,
                                     ApplicationContext context) {
        super(service, context);
        this.pacienteService = pacienteService;
        // Inicializar configuración específica del controlador
        HistoriaClinica historiaClinica = new HistoriaClinica();
        initController(historiaClinica, "Gestión de Historias Clínicas", "Formulario de Historia Clínica");
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        if (useCase == BaseUseCaseController.ALTA) {
            // Cargar lista de pacientes para el formulario
            this.model.addAttribute("pacientes", pacienteService.listarActivos());
        }
    }

    @Override
    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        // Llamar al método padre pero también cargar los detalles ordenados
        return super.consultar(id, model);
    }
}
