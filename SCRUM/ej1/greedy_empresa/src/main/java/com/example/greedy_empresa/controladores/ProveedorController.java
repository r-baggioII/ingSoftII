package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Direccion;
import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.entidades.Persona;
import com.example.greedy_empresa.servicios.LocalidadService;
import com.example.greedy_empresa.servicios.ProveedorPdfService;
import com.example.greedy_empresa.servicios.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final LocalidadService localidadService;
    private final ProveedorPdfService proveedorPdfService;

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
            @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", proveedorService.buscar(filtro, pageable));
        model.addAttribute("filtro", filtro);
        model.addAttribute("activeMenu", "proveedores");
        return "proveedores/list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        Proveedor proveedor = new Proveedor();
        proveedor.setPersona(new Persona()); // Inicializar persona
        proveedor.getDirecciones().add(new Direccion()); // Agregar una dirección vacía por defecto
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("localidades", localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
        model.addAttribute("activeMenu", "proveedores");
        return "proveedores/form";
    }

    @PostMapping
    public String crear(@Valid Proveedor proveedor,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Asegurar que persona esté inicializada
        if (proveedor.getPersona() == null) {
            proveedor.setPersona(new Persona());
        }
        
        // Asegurar que haya al menos una dirección
        if (proveedor.getDirecciones() == null || proveedor.getDirecciones().isEmpty()) {
            proveedor.getDirecciones().add(new Direccion());
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("localidades", localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
        try {
            proveedorService.guardar(proveedor);
            redirectAttributes.addFlashAttribute("successMessage", "Proveedor guardado correctamente");
            return "redirect:/proveedores";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("localidades", localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable String id, Model model) {
        Proveedor proveedor = proveedorService.buscarPorId(id);
        
        // Asegurar que persona esté inicializada correctamente
        // Si la persona es null o no es del tipo correcto, crear una nueva
        if (proveedor.getPersona() == null) {
            proveedor.setPersona(new Persona());
        }
        
        // Asegurar que haya al menos una dirección para el formulario
        if (proveedor.getDirecciones() == null || proveedor.getDirecciones().isEmpty()) {
            proveedor.getDirecciones().add(new Direccion());
        }
        
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("localidades", localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
        model.addAttribute("activeMenu", "proveedores");
        return "proveedores/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
            @Valid Proveedor proveedor,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Asegurar que persona esté inicializada
        if (proveedor.getPersona() == null) {
            proveedor.setPersona(new Persona());
        }
        
        // Asegurar que haya al menos una dirección
        if (proveedor.getDirecciones() == null || proveedor.getDirecciones().isEmpty()) {
            proveedor.getDirecciones().add(new Direccion());
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("localidades", localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
        try {
            proveedor.setId(id);
            proveedorService.guardar(proveedor);
            redirectAttributes.addFlashAttribute("successMessage", "Proveedor actualizado correctamente");
            return "redirect:/proveedores";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("error.general", ex.getMessage());
            model.addAttribute("localidades", localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
            model.addAttribute("activeMenu", "proveedores");
            return "proveedores/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        proveedorService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Proveedor eliminado correctamente");
        return "redirect:/proveedores";
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf() {
        try {
            List<Proveedor> proveedores = proveedorService.obtenerTodosParaPdf();
            byte[] pdfBytes = proveedorPdfService.generateProveedoresPdf(proveedores);
            
            String fileName = "proveedores_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
                    
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
