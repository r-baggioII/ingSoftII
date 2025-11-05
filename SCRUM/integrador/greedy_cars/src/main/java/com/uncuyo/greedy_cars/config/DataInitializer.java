package com.uncuyo.greedy_cars.config;

import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.enums.Rol;
import com.uncuyo.greedy_cars.shared.template.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos que se ejecuta al arrancar la aplicación.
 * Crea un usuario administrador por defecto si no existe.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existe el usuario admin
        if (!usuarioRepository.existsByNombreUsuarioAndEliminadoIsFalse("admin")) {
            // Crear usuario administrador por defecto
            Usuario admin = new Usuario();
            admin.setNombreUsuario("admin");
            admin.setClave(passwordEncoder.encode("GreedyAdmin123!")); // Contraseña encriptada
            admin.setRol(Rol.ADMINISTRATIVO);
            admin.setEliminado(false);
            
            usuarioRepository.save(admin);
            
            System.out.println("✅ Usuario administrador creado exitosamente");
            System.out.println("   Username: admin");
            System.out.println("   Password: GreedyAdmin123!");
        } else {
            System.out.println("ℹ️  Usuario administrador ya existe en la base de datos");
        }
    }
}
