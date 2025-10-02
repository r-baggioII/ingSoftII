package com.example.greedy_empresa.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.junit.jupiter.api.Test;
import jakarta.mail.internet.MimeMessage;

/**
 * Test para envío REAL de correo a f.julian2617@gmail.com
 */
@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.org.springframework.mail=DEBUG",
    "logging.level.jakarta.mail=DEBUG"
})
public class EnvioRealTest {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void enviarCorreoReal() {
        System.out.println("=== 🚀 INICIANDO ENVÍO REAL DE CORREO ===");
        
        try {
            // Crear mensaje HTML profesional
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("greedyteam0@gmail.com");
            helper.setTo("f.julian2617@gmail.com");
            helper.setSubject("🎉 Sistema de Notificaciones Greedy Empresa - Prueba Real");
            
            String htmlContent = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Greedy Empresa</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #f4f4f4;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                "<div style='text-align: center; padding: 20px 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 10px; margin-bottom: 30px;'>" +
                "<h1 style='margin: 0; font-size: 28px;'>🏢 Greedy Empresa</h1>" +
                "<p style='margin: 10px 0 0 0; font-size: 16px;'>Sistema de Notificaciones</p>" +
                "</div>" +
                "<div style='padding: 0 20px;'>" +
                "<h2 style='color: #333; border-bottom: 2px solid #667eea; padding-bottom: 10px;'>✅ ¡Envío Real Exitoso!</h2>" +
                "<p style='font-size: 16px; color: #555;'>Estimado Julian,</p>" +
                "<p style='font-size: 16px; color: #555;'>Este es un <strong>correo electrónico REAL</strong> enviado desde el sistema de notificaciones de Greedy Empresa. Si estás leyendo esto, significa que la configuración de Gmail está funcionando correctamente.</p>" +
                "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #28a745;'>" +
                "<h3 style='color: #28a745; margin-top: 0;'>🎯 Funcionalidades Implementadas:</h3>" +
                "<ul style='color: #555;'>" +
                "<li>Envío automático cada día 5 del mes a las 15:00 (publicidad)</li>" +
                "<li>Envío automático cada 31 de diciembre a las 15:00 (fin de año)</li>" +
                "<li>Envío manual desde interfaz web</li>" +
                "<li>Templates HTML profesionales</li>" +
                "<li>Integración con base de datos de proveedores</li>" +
                "</ul>" +
                "</div>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='https://www.uncuyo.edu.ar/' style='display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 16px;'>🌐 Visitar Universidad Nacional de Cuyo</a>" +
                "</div>" +
                "<div style='background: #e3f2fd; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<p style='margin: 0; color: #1976d2; font-weight: bold;'>📊 Detalles Técnicos:</p>" +
                "<ul style='color: #1976d2; margin: 10px 0 0 0;'>" +
                "<li>Enviado desde: greedyteam0@gmail.com</li>" +
                "<li>Servidor SMTP: smtp.gmail.com:587</li>" +
                "<li>Protocolo: TLS/STARTTLS</li>" +
                "<li>Fecha: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</li>" +
                "</ul>" +
                "</div>" +
                "<p style='font-size: 14px; color: #777; text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;'>Este correo fue generado automáticamente por el sistema de notificaciones de Greedy Empresa.<br>Si no esperabas recibir este correo, puedes ignorarlo de forma segura.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
            
            helper.setText(htmlContent, true);
            
            System.out.println("📧 Enviando correo real a: f.julian2617@gmail.com");
            System.out.println("📨 Asunto: " + message.getSubject());
            
            // ENVÍO REAL
            mailSender.send(message);
            
            System.out.println("✅ ¡CORREO REAL ENVIADO EXITOSAMENTE!");
            System.out.println("📬 Revisa tu bandeja de entrada (o spam) en: f.julian2617@gmail.com");
            System.out.println("==========================================");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar correo real:");
            System.out.println("   " + e.getClass().getSimpleName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Causa: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            
            // Si hay error de autenticación, dar instrucciones específicas
            if (e.getMessage() != null && e.getMessage().contains("Authentication")) {
                System.out.println("\n🔑 SOLUCIÓN PARA ERROR DE AUTENTICACIÓN:");
                System.out.println("1. Ve a https://myaccount.google.com/");
                System.out.println("2. Seguridad → Verificación en 2 pasos");
                System.out.println("3. Contraseñas de aplicaciones");
                System.out.println("4. Genera una nueva contraseña para 'Greedy Empresa'");
                System.out.println("5. Reemplaza 'Greedy123' en MailConfigAlternativo.java");
            }
        }
    }
}