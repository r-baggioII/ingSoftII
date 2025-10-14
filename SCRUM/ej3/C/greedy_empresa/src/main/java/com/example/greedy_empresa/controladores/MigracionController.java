package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.servicios.MigracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MigracionController {

    @Autowired
    private MigracionService migracionService;

    @GetMapping("/migracion")
    public String mostrarFormulario(Model model) {
        model.addAttribute("activeMenu", "migracion");
        return "migracion/index";
    }

    @PostMapping("/migracion/procesar")
    public String procesarArchivo(@RequestParam("archivo") MultipartFile archivo, 
                                 RedirectAttributes redirectAttributes) {
        try {
            if (archivo.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor seleccione un archivo");
                return "redirect:/migracion";
            }

            if (!archivo.getOriginalFilename().endsWith(".txt")) {
                redirectAttributes.addFlashAttribute("error", "Solo se permiten archivos .txt");
                return "redirect:/migracion";
            }

            int proveedoresCreados = migracionService.procesarArchivoMigracion(archivo);
            
            redirectAttributes.addFlashAttribute("success", 
                "Migración completada exitosamente. Se crearon " + proveedoresCreados + " proveedores.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar el archivo: " + e.getMessage());
        }

        return "redirect:/migracion";
    }
}