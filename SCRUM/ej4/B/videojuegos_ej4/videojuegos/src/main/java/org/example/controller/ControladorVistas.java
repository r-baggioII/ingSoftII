package org.example.controller;

import org.example.entity.Videojuego;
import org.example.service.ServicioCategoria;
import org.example.service.ServicioEstudio;
import org.example.service.ServicioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

/**
 * Controlador MVC para servir las vistas HTML
 * Las operaciones de datos se realizan mediante llamadas a la API REST
 */
@Controller
public class ControladorVistas {

    @Autowired 
    private ServicioVideojuego svcVideojuego;
    
    @Autowired 
    private ServicioCategoria svcCategoria;
    
    @Autowired 
    private ServicioEstudio svcEstudio;

    @Value("${app.images.base-path:${user.home}/Videojuegos/imagenes}")
    private String basePath;

    /**
     * Página de inicio - muestra todos los videojuegos activos
     */
    @GetMapping("/inicio")
    public String inicio(Model model) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.buscarTodosActivos();
            model.addAttribute("videojuegos", videojuegos);
            return "views/inicio";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Página de detalle de un videojuego
     */
    @GetMapping("/detalle/{id}")
    public String detalleVideojuego(Model model, @PathVariable("id") long id) {
        try {
            Videojuego videojuego = this.svcVideojuego.buscarPorIdYActivo(id);
            model.addAttribute("videojuego", videojuego);
            return "views/detalle";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Página de búsqueda
     */
    @GetMapping("/busqueda")
    public String busquedaVideojuego(Model model,
                                     @RequestParam(value = "query", required = false) String q) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.buscarPorTitulo(q);
            model.addAttribute("videojuegos", videojuegos);
            model.addAttribute("resultado", q);
            return "views/busqueda";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Página de administración CRUD
     */
    @GetMapping("/crud")
    public String crudVideojuego(Model model) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.listar();
            model.addAttribute("videojuegos", videojuegos);
            return "views/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Formulario para crear/editar videojuego
     */
    @GetMapping("/formulario/videojuego/{id}")
    public String formularioVideojuego(Model model, @PathVariable("id") long id) {
        try {
            model.addAttribute("categorias", this.svcCategoria.listar());
            model.addAttribute("estudios", this.svcEstudio.listar());
            if (id == 0) {
                model.addAttribute("videojuego", new Videojuego());
            } else {
                model.addAttribute("videojuego", this.svcVideojuego.obtener(id));
            }
            return "views/formulario/videojuego";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Guardar videojuego (crear o editar)
     */
    @PostMapping("/formulario/videojuego/{id}")
    public String guardarVideojuego(@RequestParam("archivo") MultipartFile archivo,
                                    @Valid @ModelAttribute("videojuego") Videojuego videojuego,
                                    BindingResult result,
                                    Model model,
                                    @PathVariable("id") long id) {
        try {
            model.addAttribute("categorias", this.svcCategoria.listar());
            model.addAttribute("estudios", this.svcEstudio.listar());

            if (result.hasErrors()) {
                return "views/formulario/videojuego";
            }

            boolean vieneUrl = isUrl(videojuego.getImagen());

            if (id == 0) {
                // CREAR
                if (!archivo.isEmpty()) {
                    if (!validarExtension(archivo)) {
                        model.addAttribute("errorImagenMsg", "La extensión no es válida");
                        return "views/formulario/videojuego";
                    }
                    if (archivo.getSize() >= 15_000_000) {
                        model.addAttribute("errorImagenMsg", "El peso excede 15MB");
                        return "views/formulario/videojuego";
                    }
                    Files.createDirectories(Paths.get(basePath));
                    String ext = extraerExtensionSeguro(archivo.getOriginalFilename());
                    String nombreFoto = Calendar.getInstance().getTimeInMillis() + ext;
                    Path destino = Paths.get(basePath, nombreFoto);
                    Files.write(destino, archivo.getBytes());
                    videojuego.setImagen(nombreFoto);
                } else if (vieneUrl) {
                    // Dejar la URL tal cual
                } else {
                    model.addAttribute("errorImagenMsg", "Cargá un archivo");
                    return "views/formulario/videojuego";
                }
                this.svcVideojuego.alta(videojuego);

            } else {
                // EDITAR
                if (!archivo.isEmpty()) {
                    if (!validarExtension(archivo)) {
                        model.addAttribute("errorImagenMsg", "La extensión no es válida");
                        return "views/formulario/videojuego";
                    }
                    if (archivo.getSize() >= 15_000_000) {
                        model.addAttribute("errorImagenMsg", "El peso excede 15MB");
                        return "views/formulario/videojuego";
                    }
                    Files.createDirectories(Paths.get(basePath));
                    String ext = extraerExtensionSeguro(archivo.getOriginalFilename());
                    String nombreFoto = Calendar.getInstance().getTimeInMillis() + ext;
                    Path destino = Paths.get(basePath, nombreFoto);
                    Files.write(destino, archivo.getBytes());
                    videojuego.setImagen(nombreFoto);
                } else if (vieneUrl) {
                    // Dejar la URL tal cual
                } else {
                    // Mantener imagen previa
                    Videojuego actual = this.svcVideojuego.obtener(id);
                    videojuego.setImagen(actual.getImagen());
                }
                this.svcVideojuego.modificar(videojuego, id);
            }

            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Eliminar videojuego
     */
    @GetMapping("/eliminar/videojuego/{id}")
    public String eliminarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            this.svcVideojuego.baja(id);
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /* ==================== HELPERS ==================== */

    private boolean isUrl(String s) {
        return s != null && (s.startsWith("http://") || s.startsWith("https://"));
    }

    private String extraerExtensionSeguro(String nombre) {
        if (nombre == null) return "";
        int i = nombre.lastIndexOf('.');
        return (i >= 0 ? nombre.substring(i) : "");
    }

    public boolean validarExtension(MultipartFile archivo) {
        try {
            return ImageIO.read(archivo.getInputStream()) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
