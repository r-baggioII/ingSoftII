package com.is.biblioteca.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar contraseñas encriptadas con BCrypt
 * Útil para crear usuarios directamente en la base de datos
 * 
 * Uso: Ejecutar el main() y cambiar la contraseña que deseas encriptar
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Contraseña a encriptar
        String plainPassword = "password123";
        
        // Generar hash
        String hashedPassword = encoder.encode(plainPassword);
        
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           GENERADOR DE CONTRASEÑAS BCRYPT                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🔑 Contraseña original: " + plainPassword);
        System.out.println("🔐 Hash BCrypt generado:");
        System.out.println(hashedPassword);
        System.out.println();
        System.out.println("📋 Para usar en SQL:");
        System.out.println("INSERT INTO Usuario (id, nombre, email, password, rol, eliminado)");
        System.out.println("VALUES ('user-id', 'Nombre Usuario', 'email@example.com',");
        System.out.println("        '" + hashedPassword + "', 'USER', false);");
        System.out.println();
        System.out.println("✅ Verificación:");
        boolean matches = encoder.matches(plainPassword, hashedPassword);
        System.out.println("   Contraseña coincide: " + (matches ? "SÍ ✅" : "NO ❌"));
        System.out.println();
        
        // Ejemplos adicionales
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("EJEMPLOS DE CONTRASEÑAS COMUNES:");
        System.out.println("════════════════════════════════════════════════════════════════");
        
        String[] passwords = {"admin123", "user123", "test123"};
        for (String pwd : passwords) {
            String hash = encoder.encode(pwd);
            System.out.println();
            System.out.println("Password: " + pwd);
            System.out.println("Hash:     " + hash);
        }
    }
}
