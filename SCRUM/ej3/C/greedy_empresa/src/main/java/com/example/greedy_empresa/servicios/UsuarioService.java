package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Usuario;
import com.example.greedy_empresa.entidades.UsuarioPersona;
import com.example.greedy_empresa.repositorios.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio de Usuario que implementa el patrón Template Method.
 * Hereda la estructura común de BaseService y sobrescribe los hooks
 * para implementar lógica específica de Usuario (incluyendo encriptación de contraseña).
 */
@Service
public class UsuarioService extends BaseService<Usuario, UsuarioRepository> {

    private final PasswordService passwordService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordService passwordService) {
        super(usuarioRepository);
        this.passwordService = passwordService;
    }

    // ========== Implementación de métodos abstractos ==========

    @Override
    public Class<Usuario> getEntityClass() {
        return Usuario.class;
    }

    @Override
    protected String getEntityName() {
        return "Usuario";
    }

    // ========== Sobrescritura de hooks para lógica específica ==========

    /**
     * Hook sobrescrito: Búsqueda por username
     */
    @Override
    protected Page<Usuario> buscarConFiltro(String filtro, Pageable pageable) {
        return repositorio.findByUsernameContainingIgnoreCaseAndEliminadoFalse(filtro, pageable);
    }

    /**
     * Hook sobrescrito: Validar que username y password sean obligatorios
     */
    @Override
    protected void validarEntidad(Usuario usuario) {
        super.validarEntidad(usuario);
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        // Validar contraseña solo para usuarios nuevos
        if (usuario.getId() == null || usuario.getId().isBlank()) {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria");
            }
            validarPasswords(usuario.getPassword(), usuario.getConfirmPassword());
        }
    }

    /**
     * Hook sobrescrito: Normalizar username (trim)
     */
    @Override
    protected void normalizarDatos(Usuario usuario) {
        String usernameNormalizado = usuario.getUsername().trim();
        usuario.setUsername(usernameNormalizado);
    }

    /**
     * Hook sobrescrito: Validar que no exista otro usuario con el mismo username
     */
    @Override
    protected void validarUnicidad(Usuario usuario) {
        repositorio.findByUsernameIgnoreCaseAndEliminadoFalse(usuario.getUsername())
                .filter(existente -> !existente.getId().equals(usuario.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
                });
    }

    /**
     * Hook sobrescrito: Procesar persona asociada
     */
    @Override
    protected void procesarRelaciones(Usuario usuario) {
        // Crear o actualizar persona
        if (usuario.getPersona() != null) {
            if (usuario.getPersona().getId() == null) {
                // Crear nueva persona concreta
                UsuarioPersona persona = new UsuarioPersona();
                persona.setNombre(usuario.getPersona().getNombre());
                persona.setApellido(usuario.getPersona().getApellido());
                persona.setCorreoElectronico(usuario.getPersona().getCorreoElectronico());
                persona.setTelefono(usuario.getPersona().getTelefono());
                persona.setEliminado(false);
                usuario.setPersona(persona);
            }
        }
    }

    /**
     * Hook sobrescrito: Actualizar campos de usuario existente
     */
    @Override
    protected void actualizarCampos(Usuario existente, Usuario nueva) {
        existente.setUsername(nueva.getUsername());
        existente.setRol(nueva.getRol());
        existente.setPersona(nueva.getPersona());
        
        // Actualizar contraseña solo si se proporcionó una nueva
        if (nueva.getPassword() != null && !nueva.getPassword().isBlank()) {
            validarPasswords(nueva.getPassword(), nueva.getConfirmPassword());
            existente.setPasswordHash(passwordService.hash(nueva.getPassword()));
        }
    }

    /**
     * Hook sobrescrito: Configurar password hash antes de crear
     */
    @Override
    protected Usuario crearNuevaEntidad(Usuario usuario) {
        // Encriptar la contraseña
        usuario.setPasswordHash(passwordService.hash(usuario.getPassword()));
        if (usuario.getPersona() != null) {
            usuario.getPersona().setEliminado(false);
        }
        return super.crearNuevaEntidad(usuario);
    }

    /**
     * Método auxiliar para validar confirmación de contraseña
     */
    private void validarPasswords(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new IllegalArgumentException("Debe confirmar la contraseña");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("La contraseña y la confirmación no coinciden");
        }
    }

    // ========== Métodos adicionales específicos de Usuario ==========

    public long contarActivos() {
        return repositorio.countByEliminadoFalse();
    }
}
