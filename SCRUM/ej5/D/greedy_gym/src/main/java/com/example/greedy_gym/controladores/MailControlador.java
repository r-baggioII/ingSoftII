package com.example.greedy_gym.controladores;

import com.example.greedy_gym.servicios.NotificacionServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailControlador {

    private final NotificacionServicio notificacionServicio;
    private final JavaMailSender mailSender;
    private final String smtpUser;

    public MailControlador(NotificacionServicio notificacionServicio,
                           JavaMailSender mailSender,
                           org.springframework.core.env.Environment environment) {
        this.notificacionServicio = notificacionServicio;
        this.mailSender = mailSender;
        this.smtpUser = environment.getProperty("spring.mail.username", "greedyteam0@gmail.com");
    }

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> testGet(@RequestParam("to") String to,
                                          @RequestParam(value = "subject", defaultValue = "Prueba de correo") String subject,
                                          @RequestParam(value = "body", defaultValue = "Hola, este es un correo de prueba.") String body) {
        if (!StringUtils.hasText(to)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parametro 'to' es obligatorio\n");
        }
        notificacionServicio.enviar(body, subject, to);
        return ResponseEntity.ok("OK\n");
    }

    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> testPost(@RequestParam("to") String to,
                                           @RequestParam(value = "subject", defaultValue = "Prueba de correo") String subject,
                                           @RequestParam(value = "body", defaultValue = "Hola, este es un correo de prueba.") String body) {
        if (!StringUtils.hasText(to)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parametro 'to' es obligatorio\n");
        }
        notificacionServicio.enviar(body, subject, to);
        return ResponseEntity.ok("OK\n");
    }

    @GetMapping(value = "/diag", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> diag(@RequestParam("to") String to,
                                       @RequestParam(value = "subject", defaultValue = "Prueba de correo (diag)") String subject,
                                       @RequestParam(value = "body", defaultValue = "Hola, este es un correo de prueba.") String body) {
        if (!StringUtils.hasText(to)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parametro 'to' es obligatorio\n");
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(smtpUser);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            return ResponseEntity.ok("OK (diag)\n");
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: ")
                    .append(e.getClass().getSimpleName())
                    .append(" - ")
                    .append(String.valueOf(e.getMessage()))
                    .append("\n");
            Throwable cause = e.getCause();
            int depth = 0;
            while (cause != null && depth < 5) {
                sb.append("CAUSE ")
                        .append(depth + 1)
                        .append(": ")
                        .append(cause.getClass().getSimpleName())
                        .append(" - ")
                        .append(String.valueOf(cause.getMessage()))
                        .append("\n");
                cause = cause.getCause();
                depth++;
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sb.toString());
        }
    }
}
