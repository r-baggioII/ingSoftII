package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.servicios.PaisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/paises")
public class PaisController extends BaseController<Pais, PaisService> {

    @Override
    protected String getNombreEntidad() {
        return "Pais";
    }

    @GetMapping
    public String listar(@RequestParam(value = "filtro", required = false) String filtro,
                         @PageableDefault(size = 10) Pageable pageable,
                         Model model) {
        return super.listar(filtro, pageable, model);
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        return super.nuevo(model);
    }
}