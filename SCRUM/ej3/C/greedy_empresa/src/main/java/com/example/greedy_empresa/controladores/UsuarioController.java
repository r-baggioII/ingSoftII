package com.example.greedy_empresa.controladores;

import com.example.greedy_empresa.entidades.Usuario;
import com.example.greedy_empresa.entidades.UsuarioPersona;
import com.example.greedy_empresa.entidades.enums.UsuarioRol;
import com.example.greedy_empresa.servicios.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador de Usuario que implementa el patrón Template Method.
 * Hereda la estructura común de BaseController y sobrescribe los hooks
 * para implementar lógica específica de Usuario.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<Usuario, UsuarioService> {

    public UsuarioController(UsuarioService usuarioService) {
        super(usuarioService);
    }

    // ========== Implementación de métodos abstractos ==========

    @Override
    protected String getActiveMenu() {
        return "usuarios";
    }

    @Override
    protected String getBasePath() {
        return "usuarios";
    }

    @Override
    protected String getModelAttributeName() {
        return "usuario";
    }

    @Override
    protected Usuario crearNuevaEntidad() {
        Usuario usuario = new Usuario();
        usuario.setRol(UsuarioRol.USER);
        usuario.setPersona(new UsuarioPersona());
        return usuario;
    }

    // ========== Sobrescritura de hooks para lógica específica ==========

    @Override
    protected void validacionesAdicionales(Usuario usuario, BindingResult bindingResult) {
        // Asegurar que persona esté inicializada
        if (usuario.getPersona() == null) {
            usuario.setPersona(new UsuarioPersona());
        }
    }

    @Override
    protected void prepararEntidadParaEdicion(Usuario usuario) {
        // Asegurar que persona esté inicializada correctamente
        if (usuario.getPersona() == null) {
            usuario.setPersona(new UsuarioPersona());
        }
        
        // Limpiar contraseña para que no se muestre en el formulario
        usuario.setPassword("");
        usuario.setConfirmPassword("");
    }

    // ========== Métodos auxiliares ==========

    /**
     * Proporciona los roles disponibles para el formulario
     */
    @ModelAttribute("roles")
    public UsuarioRol[] roles() {
        return UsuarioRol.values();
    }
}
