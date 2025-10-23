package org.example.controller;

import org.example.entity.DetalleHistoriaClinica;
import org.example.service.DetalleHistoriaClinicaService;
import org.example.service.HistoriaClinicaService;
import org.example.service.MedicoService;
import org.example.enums.BaseUseCaseController;
import org.example.exception.ErrorServiceException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/detallehistoriaclinica")
public class DetalleHistoriaClinicaController extends BaseController<DetalleHistoriaClinica, String> {

    private final HistoriaClinicaService historiaClinicaService;
    private final MedicoService medicoService;

    public DetalleHistoriaClinicaController(DetalleHistoriaClinicaService service,
                                           HistoriaClinicaService historiaClinicaService,
                                           MedicoService medicoService,
                                           ApplicationContext context) {
        super(service, context);
        this.historiaClinicaService = historiaClinicaService;
        this.medicoService = medicoService;
        // Inicializar configuración específica del controlador
        DetalleHistoriaClinica detalle = new DetalleHistoriaClinica();
        initController(detalle, "Gestión de Detalles de Historia Clínica", "Formulario de Detalle");
    }

    @Override
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {
        if (useCase == BaseUseCaseController.ALTA) {
            // Cargar listas para los combos del formulario
            this.model.addAttribute("historiasClinicas", historiaClinicaService.listarActivos());
            this.model.addAttribute("medicos", medicoService.listarActivos());
        }
    }
}
