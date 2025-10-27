package org.sistemaMecanico.controller;

import org.sistemaMecanico.service.MecanicoService;
import org.sistemaMecanico.service.ClienteService;
import org.sistemaMecanico.service.VehiculoService;
import org.sistemaMecanico.service.HistorialArregloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @Autowired
    private MecanicoService mecanicoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private HistorialArregloService historialArregloService;

    /**
     * Página de bienvenida/index principal
     * Mapea la ruta raíz "/"
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Página de inicio con estadísticas y módulos del sistema
     * Mapea la ruta "/inicio"
     */
    @GetMapping("/inicio")
    public String inicio(Model model) {
        try {
            // Obtener estadísticas del sistema
            long totalMecanicos = mecanicoService.listarActivos().size();
            long totalClientes = clienteService.listarActivos().size();
            long totalVehiculos = vehiculoService.listarActivos().size();
            long totalArreglos = historialArregloService.listarActivos().size();

            // Agregar estadísticas al modelo
            model.addAttribute("totalMecanicos", totalMecanicos);
            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("totalVehiculos", totalVehiculos);
            model.addAttribute("totalArreglos", totalArreglos);

        } catch (Exception e) {
            // En caso de error, mostrar ceros en las estadísticas
            model.addAttribute("totalMecanicos", 0);
            model.addAttribute("totalClientes", 0);
            model.addAttribute("totalVehiculos", 0);
            model.addAttribute("totalArreglos", 0);
            model.addAttribute("msgError", "No se pudieron cargar las estadísticas del sistema");
        }

        return "inicio";
    }

    /**
     * Ruta alternativa para home
     */
    @GetMapping("/home")
    public String home() {
        return "redirect:/inicio";
    }
}
