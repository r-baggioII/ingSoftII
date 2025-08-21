package com.minimarket.controlador;

import com.minimarket.modelo.Articulo;
import com.minimarket.modelo.Categoria;
import com.minimarket.servicio.ArticuloServicio;
import com.minimarket.servicio.CategoriaServicio;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/articulos")
public class ArticuloControlador {

    private final ArticuloServicio articuloServicio;
    private final CategoriaServicio categoriaServicio;

    public ArticuloControlador(ArticuloServicio articuloServicio, CategoriaServicio categoriaServicio) {
        this.articuloServicio = articuloServicio;
        this.categoriaServicio = categoriaServicio;
    }

    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam Optional<String> busqueda,
            @RequestParam Optional<Integer> pagina
    ) {
        int numeroPagina = pagina.orElse(0);
        Pageable pageable = PageRequest.of(numeroPagina, 10);

        Page<Articulo> pageArticulos;
        if (busqueda.isPresent() && !busqueda.get().isBlank()) {
            pageArticulos = articuloServicio.buscarPorDenominacion(busqueda.get(), pageable);
        } else {
            pageArticulos = articuloServicio.listarArticulos(pageable);
        }

        List<Categoria> categorias = categoriaServicio.listarCategorias();

        modelo.addAttribute("articulos", pageArticulos.getContent());
        modelo.addAttribute("paginas", pageArticulos.getTotalPages());
        modelo.addAttribute("paginaActual", numeroPagina);
        modelo.addAttribute("busqueda", busqueda.orElse(null));
        modelo.addAttribute("categorias", categorias);
        return "articulos/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("articulo", new Articulo());
        modelo.addAttribute("categorias", categoriaServicio.listarCategorias());
        return "articulos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Articulo articulo, BindingResult resultado, Model modelo) {
        if (resultado.hasErrors()) {
            modelo.addAttribute("categorias", categoriaServicio.listarCategorias());
            return "articulos/formulario";
        }
        articuloServicio.guardarArticulo(articulo);
        return "redirect:/articulos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model modelo) {
        Optional<Articulo> articuloOpt = articuloServicio.buscarPorId(id);
        if (articuloOpt.isEmpty()) {
            return "redirect:/articulos";
        }
        modelo.addAttribute("articulo", articuloOpt.get());
        modelo.addAttribute("categorias", categoriaServicio.listarCategorias());
        return "articulos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        articuloServicio.eliminarArticulo(id);
        return "redirect:/articulos";
    }
}
