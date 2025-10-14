package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Direccion;
import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.entidades.Persona;
import com.example.greedy_empresa.servicios.LocalidadService;
import com.example.greedy_empresa.servicios.ProveedorPdfService;
import com.example.greedy_empresa.servicios.ProveedorService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador de Proveedor que implementa el patrón Template Method.
 * Hereda la estructura común de BaseController y sobrescribe los hooks
 * para implementar lógica específica de Proveedor.
 */
@Controller
@RequestMapping("/proveedores")
public class ProveedorController extends BaseController<Proveedor, ProveedorService> {

    private final LocalidadService localidadService;
    private final ProveedorPdfService proveedorPdfService;

    public ProveedorController(ProveedorService proveedorService,
                              LocalidadService localidadService,
                              ProveedorPdfService proveedorPdfService) {
        super(proveedorService);
        this.localidadService = localidadService;
        this.proveedorPdfService = proveedorPdfService;
    }

    // ========== Implementación de métodos abstractos ==========

    @Override
    protected String getActiveMenu() {
        return "proveedores";
    }

    @Override
    protected String getBasePath() {
        return "proveedores";
    }

    @Override
    protected String getModelAttributeName() {
        return "proveedor";
    }

    @Override
    protected Proveedor crearNuevaEntidad() {
        Proveedor proveedor = new Proveedor();
        proveedor.setPersona(new Persona());
        proveedor.getDirecciones().add(new Direccion());
        return proveedor;
    }

    // ========== Sobrescritura de hooks para lógica específica ==========

    @Override
    protected void agregarDatosAdicionalesFormulario(Model model) {
        model.addAttribute("localidades", 
            localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
    }

    @Override
    protected void validacionesAdicionales(Proveedor proveedor, BindingResult bindingResult) {
        // Asegurar que persona esté inicializada
        if (proveedor.getPersona() == null) {
            proveedor.setPersona(new Persona());
        }
        
        // Asegurar que haya al menos una dirección
        if (proveedor.getDirecciones() == null || proveedor.getDirecciones().isEmpty()) {
            proveedor.getDirecciones().add(new Direccion());
        }
    }

    @Override
    protected void prepararEntidadParaEdicion(Proveedor proveedor) {
        // Asegurar que persona esté inicializada correctamente
        if (proveedor.getPersona() == null) {
            proveedor.setPersona(new Persona());
        }
        
        // Asegurar que haya al menos una dirección para el formulario
        if (proveedor.getDirecciones() == null || proveedor.getDirecciones().isEmpty()) {
            proveedor.getDirecciones().add(new Direccion());
        }
    }

    // ========== Métodos adicionales específicos de Proveedor ==========

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf() {
        try {
            List<Proveedor> proveedores = service.obtenerTodosParaPdf();
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
