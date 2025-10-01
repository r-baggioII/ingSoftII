package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.servicios.EmpresaService;
import com.example.greedy_empresa.servicios.PaisService;
import com.example.greedy_empresa.servicios.ProveedorService;
import com.example.greedy_empresa.servicios.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final ProveedorService proveedorService;
    private final PaisService paisService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("usuariosActivos", usuarioService.contarActivos());
        model.addAttribute("empresasActivas", empresaService.contarActivas());
        model.addAttribute("proveedoresActivos", proveedorService.contarActivos());
        model.addAttribute("paisesActivos", paisService.contarActivos());
        model.addAttribute("activeMenu", "dashboard");
        return "dashboard/dashboard";
    }
}
