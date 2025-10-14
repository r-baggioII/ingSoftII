package com.example.greedy_empresa.notificaciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuración REAL de Gmail para envío de correos
 * IMPORTANTE: Necesitas configurar una contraseña de aplicación en Gmail
 */
@Configuration
public class MailConfigAlternativo {

    @Bean
    @Primary
    public JavaMailSender javaMailSenderReal() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Configuración REAL de Gmail
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("greedyteam0@gmail.com");
        
        // IMPORTANTE: Esta debe ser una contraseña de aplicación de Gmail, no la contraseña normal
        // Genera una en: https://myaccount.google.com/ > Seguridad > Verificación en 2 pasos > Contraseñas de aplicaciones
        mailSender.setPassword("upizbhvdorsspqls");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.debug", "false");

        return mailSender;
    }
}