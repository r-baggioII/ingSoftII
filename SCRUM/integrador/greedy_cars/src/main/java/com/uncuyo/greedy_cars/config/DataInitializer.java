package com.uncuyo.greedy_cars.config;

import com.uncuyo.greedy_cars.shared.template.entity.Empleado;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.enums.Rol;
import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento;
import com.uncuyo.greedy_cars.shared.template.enums.TipoEmpleado;
import com.uncuyo.greedy_cars.shared.template.repository.PersonaRepository;
import com.uncuyo.greedy_cars.shared.template.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Inicializador de datos que se ejecuta al arrancar la aplicación.
 * Crea usuarios por defecto si no existen.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador por defecto
        if (!usuarioRepository.existsByNombreUsuarioAndEliminadoIsFalse("admin")) {
            Usuario admin = new Usuario();
            admin.setNombreUsuario("admin");
            admin.setClave(passwordEncoder.encode("GreedyAdmin123!"));
            admin.setRol(Rol.ADMINISTRATIVO);
            admin.setEliminado(false);
            
            usuarioRepository.save(admin);
            
            System.out.println("✅ Usuario administrador creado exitosamente");
            System.out.println("   Username: admin");
            System.out.println("   Password: GreedyAdmin123!");
        } else {
            System.out.println("ℹ️  Usuario administrador ya existe en la base de datos");
        }

        // Crear usuario jefe por defecto
        if (!usuarioRepository.existsByNombreUsuarioAndEliminadoIsFalse("jefe")) {
            try {
                // Crear persona para el jefe
                Empleado jefePersona = new Empleado();
                jefePersona.setNombre("Juan");
                jefePersona.setApellido("Pérez");
                jefePersona.setFechaNacimiento(LocalDate.of(1980, 1, 1));
                jefePersona.setTipoDocumento(TipoDocumento.DNI);
                jefePersona.setNumeroDocumento("12345678");
                jefePersona.setTipoEmpleado(TipoEmpleado.JEFE);
                jefePersona.setEliminado(false);
                
                // Guardar la persona primero
                Empleado savedPersona = personaRepository.save(jefePersona);
                System.out.println("   Persona jefe creada con ID: " + savedPersona.getId());
                
                // Crear usuario jefe
                Usuario jefe = new Usuario();
                jefe.setNombreUsuario("jefe");
                jefe.setClave(passwordEncoder.encode("GreedyJefe123!"));
                jefe.setEmail("jefe@greedy-cars.com");
                jefe.setRol(Rol.JEFE);
                jefe.setPersona(savedPersona);
                jefe.setEliminado(false);
                
                Usuario savedJefe = usuarioRepository.save(jefe);
                
                System.out.println("✅ Usuario jefe creado exitosamente");
                System.out.println("   Username: jefe");
                System.out.println("   Password: GreedyJefe123!");
                System.out.println("   Usuario ID: " + savedJefe.getId());
            } catch (Exception e) {
                System.err.println("❌ Error al crear usuario jefe: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ℹ️  Usuario jefe ya existe en la base de datos");
        }
    }
}
