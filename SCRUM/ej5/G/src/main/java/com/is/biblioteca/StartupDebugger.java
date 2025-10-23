package com.is.biblioteca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.persistence.repository.UsuarioRepository;

import java.util.List;

@Component
public class StartupDebugger implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║       🚀 APLICACIÓN INICIADA - DEBUG DE USUARIOS             ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            System.out.println("\n📊 Total de usuarios en la base de datos: " + usuarios.size());
            System.out.println("─────────────────────────────────────────────────────────────────");
            
            if (usuarios.isEmpty()) {
                System.out.println("⚠️  NO HAY USUARIOS EN LA BASE DE DATOS");
                System.out.println("   Ejecuta: mysql -u root -padminAdmin mecanico_db < poblar_bd.sql");
            } else {
                for (Usuario u : usuarios) {
                    System.out.println("\n👤 Usuario: " + u.getId());
                    System.out.println("   📧 Email: " + u.getEmail());
                    System.out.println("   👔 Nombre: " + u.getNombre());
                    System.out.println("   🎭 Rol: " + u.getRol());
                    System.out.println("   ❌ Eliminado: " + u.getEliminado());
                    System.out.println("   🔑 Password (primeros 30): " + 
                        (u.getPassword() != null ? u.getPassword().substring(0, Math.min(30, u.getPassword().length())) + "..." : "NULL"));
                }
                
                System.out.println("\n─────────────────────────────────────────────────────────────────");
                System.out.println("✅ Puedes usar estas credenciales para login:");
                System.out.println("   📧 Email: admin@mecanico.com");
                System.out.println("   🔑 Password: password123");
                System.out.println("   🌐 URL: http://localhost:9000/usuario/login");
            }
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║              APLICACIÓN LISTA PARA USAR                       ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
