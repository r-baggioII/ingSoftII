package com.example.tinder_mascotas.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Service
public class NotificacionServicio {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger log = LoggerFactory.getLogger(NotificacionServicio.class);

    @Value("${spring.mail.username:}")
    private String smtpUser;

    @Async
    public void enviar(String cuerpo, String titulo, String email) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            // Usar el usuario SMTP como remitente si está configurado (requerido por Gmail)
            if (smtpUser != null && !smtpUser.isBlank()) {
                mensaje.setFrom(smtpUser);
            } else {
                mensaje.setFrom("noreply@tinder-mascota.com");
            }
            mensaje.setSubject(titulo);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
            log.info("[NotificacionServicio] Email enviado a {} con titulo '{}'", email, titulo);
        } catch (Exception e) {
            // No bloquear el flujo de negocio por fallo de email
            log.warn("[NotificacionServicio] Fallo enviando email a {}: {}", email, e.getMessage());
        }
    }

}
