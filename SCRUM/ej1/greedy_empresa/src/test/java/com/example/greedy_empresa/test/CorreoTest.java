package com.example.greedy_empresa.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.junit.jupiter.api.Test;
import jakarta.mail.internet.MimeMessage;

/**
 * Test simple para verificar la configuración de correos
 */
@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.org.springframework.mail=DEBUG",
    "logging.level.jakarta.mail=DEBUG"
})
public class CorreoTest {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Test
    public void verificarConfiguracionCorreo() {
        System.out.println("=== DIAGNÓSTICO DE CONFIGURACIÓN DE CORREO ===");
        
        // Verificar si JavaMailSender está configurado
        if (mailSender == null) {
            System.out.println("❌ ERROR: JavaMailSender NO está configurado");
            System.out.println("💡 Verifica que spring-boot-starter-mail esté en el classpath");
            return;
        }
        
        System.out.println("✅ JavaMailSender está configurado");
        
        try {
            // Intentar crear un mensaje de prueba
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("greedyteam0@gmail.com");
            helper.setTo("f.julian2617@gmail.com");
            helper.setSubject("🧪 Test de Configuración");
            helper.setText("<h1>Test OK</h1>", true);
            
            System.out.println("✅ Mensaje de prueba creado exitosamente");
            System.out.println("📧 Remitente: greedyteam0@gmail.com");
            System.out.println("📧 Destinatario: f.julian2617@gmail.com");
            
            // Intentar enviar (HABILITADO para prueba real)
            mailSender.send(message);
            System.out.println("✅ Email de prueba enviado exitosamente!");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al crear mensaje de prueba:");
            System.out.println("   " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        // Mostrar información de configuración
        System.out.println("\n=== INFORMACIÓN DE CONFIGURACIÓN ===");
        System.out.println("Tipo de JavaMailSender: " + mailSender.getClass().getName());
        
        System.out.println("\n=== RECOMENDACIONES ===");
        System.out.println("1. Verificar credenciales en application.properties");
        System.out.println("2. Si usas Gmail, necesitas una 'Contraseña de aplicación'");
        System.out.println("3. Verificar conectividad a smtp.gmail.com:587");
        System.out.println("4. Revisar logs de Spring Mail para errores detallados");
    }
}