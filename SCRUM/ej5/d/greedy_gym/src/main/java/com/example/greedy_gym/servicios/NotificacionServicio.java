package com.example.greedy_gym.servicios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificacionServicio {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacionServicio.class);

    private final JavaMailSender mailSender;
    private final String smtpUser;

    public NotificacionServicio(JavaMailSender mailSender,
                                @Value("${spring.mail.username:}") String smtpUser) {
        this.mailSender = mailSender;
        this.smtpUser = smtpUser;
    }

    @Async
    public void enviar(String cuerpo, String titulo, String email) {
        if (!StringUtils.hasText(email)) {
            LOGGER.warn("Se omitió notificación sin correo destino (titulo: {})", titulo);
            return;
        }
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            if (StringUtils.hasText(smtpUser)) {
                mensaje.setFrom(smtpUser);
            } else {
                mensaje.setFrom("noreply@greedy-gym.com");
            }
            mensaje.setSubject(titulo);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
            LOGGER.info("Email enviado a {} con título '{}'", email, titulo);
        } catch (Exception e) {
            LOGGER.warn("Fallo enviando email a {}: {}", email, e.getMessage());
        }
    }
}
