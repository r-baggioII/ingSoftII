package com.example.greedy_gym.config;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.UsuarioRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea el usuario administrador por defecto (admin / admin123) si no existe.
 * La clave se encripta usando BCrypt antes de guardarla en la base de datos.
 */
@Component
public class StartupAdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupAdminSeeder.class);
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public StartupAdminSeeder(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String username = "admin";
        String plainPassword = "admin123";
        
        usuarioRepositorio.findByNombreUsuarioIgnoreCase(username)
                .ifPresentOrElse(
                    usuario -> {
                        // Si el usuario existe, verificar si la contraseña necesita ser actualizada
                        String claveActual = usuario.getClave();
                        if (claveActual != null && !claveActual.startsWith("$2a$")) {
                            // La contraseña está en texto plano, necesita ser encriptada
                            log.warn("Usuario admin encontrado con contraseña en texto plano. Actualizando a BCrypt...");
                            usuario.setClave(passwordEncoder.encode(plainPassword));
                            usuarioRepositorio.save(usuario);
                            log.info("Contraseña del usuario admin actualizada a BCrypt (id={})", usuario.getId());
                        } else {
                            log.info("Usuario admin ya existe con contraseña encriptada (id={})", usuario.getId());
                        }
                    },
                    () -> {
                        // Crear nuevo usuario con contraseña encriptada
                        String encryptedPassword = passwordEncoder.encode(plainPassword);
                        Usuario nuevo = new Usuario(username, encryptedPassword, RolUsuario.ADMINISTRATIVO);
                        usuarioRepositorio.save(nuevo);
                        log.warn("Usuario admin creado con clave encriptada BCrypt. Usuario: 'admin', Contraseña: 'admin123'");
                    }
                );
    }
}
