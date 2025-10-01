package com.example.greedy_gym.config;

import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.UsuarioRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea el usuario administrador por defecto (admin / admin123) si no existe.
 * IMPORTANTE: La clave esta en texto plano porque actualmente el sistema
 * maneja claves sin encriptar. Si se agrega encriptación (BCrypt, etc.)
 * actualizar este seeder para codificar la clave antes de guardarla.
 */
@Component
public class StartupAdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupAdminSeeder.class);
    private final UsuarioRepositorio usuarioRepositorio;

    public StartupAdminSeeder(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String username = "admin";
        String password = "admin123";
        usuarioRepositorio.findByNombreUsuarioIgnoreCase(username)
                .ifPresentOrElse(u -> log.info("Usuario admin ya existe (id={})", u.getId()), () -> {
                    Usuario nuevo = new Usuario(username, password, RolUsuario.ADMINISTRATIVO);
                    usuarioRepositorio.save(nuevo);
                    log.warn("Usuario admin creado con clave por defecto 'admin123'. Cambiarla luego por seguridad.");
                });
    }
}
