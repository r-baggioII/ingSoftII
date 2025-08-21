package com.minimarket.controlador;

import com.minimarket.modelo.Categoria;
import com.minimarket.servicio.CategoriaServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaControlador {

    private final CategoriaServicio categoriaServicio;

    public CategoriaControlador(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }

    @GetMapping
    public String listar(Model modelo, @RequestParam(required = false) String busqueda) {
        List<Categoria> categorias;
        if (busqueda != null && !busqueda.isBlank()) {
            categorias = categoriaServicio.buscarPorDenominacion(busqueda);
        } else {
            categorias = categoriaServicio.listarCategorias();
        }
        modelo.addAttribute("categorias", categorias);
        modelo.addAttribute("busqueda", busqueda);
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("categoria", new Categoria());
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Categoria categoria, BindingResult resultado, Model modelo) {
        if (resultado.hasErrors()) {
            return "categorias/formulario";
        }
        categoriaServicio.guardarCategoria(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model modelo) {
        java.util.Optional<com.minimarket.modelo.Categoria> categoriaOpt = categoriaServicio.buscarPorId(id);
        if (categoriaOpt.isEmpty()) {
            return "redirect:/categorias";
        }
        modelo.addAttribute("categoria", categoriaOpt.get());
        return "categorias/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        categoriaServicio.eliminarCategoria(id);
        return "redirect:/categorias";
    }
}
