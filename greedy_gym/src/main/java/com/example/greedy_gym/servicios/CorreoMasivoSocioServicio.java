package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CorreoMasivoSocioServicio {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorreoMasivoSocioServicio.class);

    private final SocioRepositorio socioRepositorio;
    private final JavaMailSender javaMailSender;
    private final String correoRemitente;

    public CorreoMasivoSocioServicio(SocioRepositorio socioRepositorio,
                                     JavaMailSender javaMailSender,
                                     @Value("${spring.mail.username}") String correoRemitente) {
        this.socioRepositorio = socioRepositorio;
        this.javaMailSender = javaMailSender;
        this.correoRemitente = correoRemitente;
    }

    public int enviarSaludoGeneral() {
        List<Socio> socios = socioRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc();
        if (socios.isEmpty()) {
            LOGGER.info("No hay socios activos para el envío masivo");
            return 0;
        }

        int enviados = 0;
        for (Socio socio : socios) {
            if (!StringUtils.hasText(socio.getCorreoElectronico())) {
                LOGGER.warn("Se omitió a {} {} por no tener correo electrónico", socio.getNombre(), socio.getApellido());
                continue;
            }
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(correoRemitente);
            mensaje.setTo(socio.getCorreoElectronico());
            mensaje.setSubject("Saludos de Greedy Gym");
            mensaje.setText("Buenos días, ¿cómo estás? Recordá que Greedy Gym está para acompañarte en tu entrenamiento.");

            try {
                javaMailSender.send(mensaje);
                enviados++;
            } catch (MailException ex) {
                LOGGER.error("No se pudo enviar el saludo general a {}", socio.getCorreoElectronico(), ex);
            }
        }
        LOGGER.info("Se enviaron {} saludos generales a socios", enviados);
        return enviados;
    }
}
