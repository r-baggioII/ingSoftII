package com.example.tinder_mascotas.controladores;

import com.example.tinder_mascotas.servicios.NotificacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
public class MailControlador {

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> testGet(
            @RequestParam("to") String to,
            @RequestParam(value = "subject", defaultValue = "Prueba de correo") String subject,
            @RequestParam(value = "body", defaultValue = "Hola, este es un correo de prueba.") String body
    ) {
        if (to == null || to.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parametro 'to' es obligatorio\n");
        }
        notificacionServicio.enviar(body, subject, to);
        return ResponseEntity.ok("OK\n");
    }

    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> testPost(
            @RequestParam("to") String to,
            @RequestParam(value = "subject", defaultValue = "Prueba de correo") String subject,
            @RequestParam(value = "body", defaultValue = "Hola, este es un correo de prueba.") String body
    ) {
        if (to == null || to.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parametro 'to' es obligatorio\n");
        }
        notificacionServicio.enviar(body, subject, to);
        return ResponseEntity.ok("OK\n");
    }

    // Diagnóstico sin @Async: intenta enviar y devuelve el error si falla
    @GetMapping(value = "/diag", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> diag(
            @RequestParam("to") String to,
            @RequestParam(value = "subject", defaultValue = "Prueba de correo (diag)") String subject,
            @RequestParam(value = "body", defaultValue = "Hola, este es un correo de prueba.") String body
    ) {
        try {
            org.springframework.mail.SimpleMailMessage msg = new org.springframework.mail.SimpleMailMessage();
            msg.setTo(to);
            // Usar el usuario SMTP como remitente para evitar rechazos por 'from' no autorizado
            String user = null;
            try {
                java.util.Properties props = new java.util.Properties();
                // Spring Boot guarda el username en el Environment; intentamos leerlo de system props también
                user = System.getProperty("spring.mail.username");
            } catch (Exception ignored) {}
            if (user == null || user.isBlank()) {
                user = "greedyteam0@gmail.com"; // fallback conocido del properties
            }
            msg.setFrom(user);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            return ResponseEntity.ok("OK (diag)\n");
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR: ").append(e.getClass().getSimpleName()).append(" - ").append(String.valueOf(e.getMessage())).append("\n");
            Throwable c = e.getCause();
            int depth = 0;
            while (c != null && depth < 5) {
                sb.append("CAUSE ").append(depth + 1).append(": ").append(c.getClass().getSimpleName()).append(" - ").append(String.valueOf(c.getMessage())).append("\n");
                c = c.getCause();
                depth++;
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sb.toString());
        }
    }
}
