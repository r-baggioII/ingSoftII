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

@Controller
public class ControladorVideojuego {

    @Autowired private ServicioVideojuego svcVideojuego;
    @Autowired private ServicioCategoria svcCategoria;
    @Autowired private ServicioEstudio svcEstudio;

    // Ruta base para guardar archivos subidos (solo si se sube un archivo)
    // Ej: C:/Videojuegos/imagenes
    @Value("${app.images.base-path:C:/Videojuegos/imagenes}")
    private String basePath;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findAllByActivo();
            model.addAttribute("videojuegos", videojuegos);
            return "views/inicio";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalleVideojuego(Model model, @PathVariable("id") long id) {
        try {
            Videojuego videojuego = this.svcVideojuego.findByIdAndActivo(id);
            model.addAttribute("videojuego", videojuego);
            return "views/detalle";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/busqueda")
    public String busquedaVideojuego(Model model,
                                     @RequestParam(value = "query", required = false) String q) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findByTitle(q);
            model.addAttribute("videojuegos", videojuegos);
            model.addAttribute("resultado", q);
            return "views/busqueda";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/crud")
    public String crudVideojuego(Model model) {
        try {
            List<Videojuego> videojuegos = this.svcVideojuego.findAll();
            model.addAttribute("videojuegos", videojuegos);
            return "views/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/formulario/videojuego/{id}")
    public String formularioVideojuego(Model model, @PathVariable("id") long id) {
        try {
            model.addAttribute("categorias", this.svcCategoria.findAll());
            model.addAttribute("estudios", this.svcEstudio.findAll());
            if (id == 0) {
                model.addAttribute("videojuego", new Videojuego());
            } else {
                model.addAttribute("videojuego", this.svcVideojuego.findById(id));
            }
            return "views/formulario/videojuego";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/formulario/videojuego/{id}")
    public String guardarVideojuego(@RequestParam("archivo") MultipartFile archivo,
                                    @Valid @ModelAttribute("videojuego") Videojuego videojuego,
                                    BindingResult result,
                                    Model model,
                                    @PathVariable("id") long id) {
        try {
            model.addAttribute("categorias", this.svcCategoria.findAll());
            model.addAttribute("estudios", this.svcEstudio.findAll());

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
                    videojuego.setImagen(nombreFoto); // guardamos SOLO el nombre del archivo
                } else if (vieneUrl) {
                    // Dejar la URL tal cual
                } else {
                    model.addAttribute("errorImagenMsg", "Cargá un archivo");
                    return "views/formulario/videojuego";
                }
                this.svcVideojuego.saveOne(videojuego);

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
                    // Dejar la URL tal cual (no escribimos archivo ni armamos Path)
                } else {
                    // Ni archivo ni URL nueva => mantener la imagen previa
                    Videojuego actual = this.svcVideojuego.findById(id);
                    videojuego.setImagen(actual.getImagen());
                }
                this.svcVideojuego.updateOne(videojuego, id);
            }

            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/eliminar/videojuego/{id}")
    public String eliminarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            model.addAttribute("videojuego", this.svcVideojuego.findById(id));
            return "views/formulario/eliminar";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/eliminar/videojuego/{id}")
    public String desactivarVideojuego(Model model, @PathVariable("id") long id) {
        try {
            this.svcVideojuego.deleteById(id);
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
