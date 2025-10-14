package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.notificaciones.ResultadoEnvio;
import com.example.greedy_empresa.servicios.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para manejar las notificaciones por correo electrónico
 */
@Controller
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Muestra la página principal de notificaciones
     */
    @GetMapping("/notificaciones")
    public String mostrarNotificaciones(Model model) {
        model.addAttribute("activeMenu", "notificaciones");
        
        // Obtener estadísticas
        long[] estadisticas = notificacionService.obtenerEstadisticasProveedores();
        model.addAttribute("totalProveedores", estadisticas[0]);
        model.addAttribute("proveedoresConEmail", estadisticas[1]);
        model.addAttribute("configurationOk", notificacionService.verificarConfiguracion());
        
        return "notificaciones/index";
    }

    /**
     * Envía correos publicitarios de forma manual
     */
    @PostMapping("/notificaciones/enviar-publicitarios")
    public String enviarCorreosPublicitarios(RedirectAttributes redirectAttributes) {
        try {
            ResultadoEnvio resultado = notificacionService.enviarCorreosPublicitariosManuales();
            
            if (resultado.fueExitoso()) {
                redirectAttributes.addFlashAttribute("success", 
                    String.format("✅ Correos publicitarios enviados exitosamente. " +
                                 "Total enviados: %d de %d proveedores (%.1f%% de éxito).",
                                 resultado.getTotalEnviados(),
                                 resultado.getTotalProveedores(),
                                 resultado.getPorcentajeExito()));
            } else {
                redirectAttributes.addFlashAttribute("warning", 
                    String.format("⚠️ Envío completado con algunos errores. " +
                                 "Enviados: %d, Fallidos: %d de %d proveedores.",
                                 resultado.getTotalEnviados(),
                                 resultado.getTotalFallidos(),
                                 resultado.getTotalProveedores()));
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "❌ Error al enviar correos publicitarios: " + e.getMessage());
        }

        return "redirect:/notificaciones";
    }

    /**
     * Envía correos de fin de año de forma manual
     */
    @PostMapping("/notificaciones/enviar-fin-ano")
    public String enviarCorreosFinDeAno(RedirectAttributes redirectAttributes) {
        try {
            ResultadoEnvio resultado = notificacionService.enviarCorreosFinDeAnoManuales();
            
            if (resultado.fueExitoso()) {
                redirectAttributes.addFlashAttribute("success", 
                    String.format("🎉 Correos de fin de año enviados exitosamente. " +
                                 "Total enviados: %d de %d proveedores (%.1f%% de éxito).",
                                 resultado.getTotalEnviados(),
                                 resultado.getTotalProveedores(),
                                 resultado.getPorcentajeExito()));
            } else {
                redirectAttributes.addFlashAttribute("warning", 
                    String.format("⚠️ Envío completado con algunos errores. " +
                                 "Enviados: %d, Fallidos: %d de %d proveedores.",
                                 resultado.getTotalEnviados(),
                                 resultado.getTotalFallidos(),
                                 resultado.getTotalProveedores()));
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "❌ Error al enviar correos de fin de año: " + e.getMessage());
        }

        return "redirect:/notificaciones";
    }

    /**
     * Envía un correo de prueba al administrador para verificar la configuración
     */
    @PostMapping("/notificaciones/enviar-prueba")
    public String enviarCorreoPrueba(RedirectAttributes redirectAttributes) {
        try {
            boolean resultado = notificacionService.enviarCorreoPrueba();
            
            if (resultado) {
                redirectAttributes.addFlashAttribute("success", 
                    "✅ Correo de prueba enviado exitosamente a f.julian2617@gmail.com. " +
                    "Revisa la bandeja de entrada (y spam) para confirmar que la configuración funciona.");
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "❌ Error al enviar el correo de prueba. Verifica la configuración del sistema.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "❌ Error al enviar correo de prueba: " + e.getMessage());
        }

        return "redirect:/notificaciones";
    }
}