package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Direccion;
import com.example.greedy_empresa.entidades.Empresa;
import com.example.greedy_empresa.servicios.EmpresaExcelService;
import com.example.greedy_empresa.servicios.EmpresaService;
import com.example.greedy_empresa.servicios.LocalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.web.PageableDefault;
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
 * Controlador de Empresa que implementa el patrón Template Method.
 * Hereda la estructura común de BaseController y sobrescribe los hooks
 * para implementar lógica específica de Empresa.
 */
@Controller
@RequestMapping("/empresas")
public class EmpresaController extends BaseController<Empresa, EmpresaService> {

    private final EmpresaExcelService empresaExcelService;
    private final LocalidadService localidadService;

    public EmpresaController(EmpresaService empresaService, 
                            EmpresaExcelService empresaExcelService,
                            LocalidadService localidadService) {
        super(empresaService);
        this.empresaExcelService = empresaExcelService;
        this.localidadService = localidadService;
    }

    // ========== Implementación de métodos abstractos ==========

    @Override
    protected String getActiveMenu() {
        return "empresas";
    }

    @Override
    protected String getBasePath() {
        return "empresas";
    }

    @Override
    protected String getModelAttributeName() {
        return "empresa";
    }

    @Override
    protected Empresa crearNuevaEntidad() {
        Empresa empresa = new Empresa();
        empresa.getDirecciones().add(new Direccion());
        return empresa;
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        return super.nuevo(model);
    }

    // ========== Sobrescritura de hooks para lógica específica ==========

    @Override
    protected void agregarDatosAdicionalesFormulario(Model model) {
        model.addAttribute("localidades", 
            localidadService.buscar(null, null, null, null, Pageable.unpaged()).getContent());
    }

    @Override
    protected void validacionesAdicionales(Empresa empresa, BindingResult bindingResult) {
        // Asegurar que haya al menos una dirección
        if (empresa.getDirecciones() == null || empresa.getDirecciones().isEmpty()) {
            empresa.getDirecciones().add(new Direccion());
        }
    }

    // ========== Métodos adicionales específicos de Empresa ==========

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
                         @PageableDefault(size = 10) Pageable pageable,
                         Model model) {
        return super.listar(filtro, pageable, model);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> descargarExcel() {
        try {
            List<Empresa> empresas = service.obtenerTodasParaExcel();
            byte[] excelBytes = empresaExcelService.generateEmpresasExcel(empresas);
            
            String fileName = "empresas_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
                    
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

