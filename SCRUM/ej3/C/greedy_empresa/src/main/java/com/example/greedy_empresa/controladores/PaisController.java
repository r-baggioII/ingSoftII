package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.servicios.PaisService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/paises")
public class PaisController extends BaseController<Pais, PaisService> {

    @Override
    protected String getNombreEntidad() {
        return "Pais";
    }
}