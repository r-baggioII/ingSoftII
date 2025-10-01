package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Empleado;
import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.TipoEmpleado;
import com.example.greedy_gym.repositorios.EmpleadoRepositorio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificadorCumpleaniosServicio {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificadorCumpleaniosServicio.class);

    private final SocioRepositorio socioRepositorio;
    private final EmpleadoRepositorio empleadoRepositorio;
    private final JavaMailSender javaMailSender;
    private final String correoRemitente;
    private final ZoneId zonaHorariaEnvio;

    public NotificadorCumpleaniosServicio(SocioRepositorio socioRepositorio,
                                          EmpleadoRepositorio empleadoRepositorio,
                                          JavaMailSender javaMailSender,
                                          @Value("${spring.mail.username}") String correoRemitente,
                                          @Value("${greedy.mail.cumpleanios.zone:America/Argentina/Buenos_Aires}") String zonaHoraria) {
        this.socioRepositorio = socioRepositorio;
        this.empleadoRepositorio = empleadoRepositorio;
        this.javaMailSender = javaMailSender;
        this.correoRemitente = correoRemitente;
        this.zonaHorariaEnvio = ZoneId.of(zonaHoraria);
    }

    @Scheduled(cron = "${greedy.mail.cumpleanios.cron:0 0 8 * * ?}",
            zone = "${greedy.mail.cumpleanios.zone:America/Argentina/Buenos_Aires}")
    public void enviarFelicitacionesProgramadas() {
        LocalDate hoy = LocalDate.now(zonaHorariaEnvio);
        List<DestinatarioCumpleanios> destinatarios = recopilarDestinatarios(hoy);
        if (destinatarios.isEmpty()) {
            LOGGER.info("No se encontraron cumpleaños para la fecha {}", hoy);
            return;
        }
        destinatarios.forEach(this::enviarCorreo);
    }

    private List<DestinatarioCumpleanios> recopilarDestinatarios(LocalDate fecha) {
        Map<String, DestinatarioCumpleanios> destinatarios = new LinkedHashMap<>();

        socioRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc().stream()
                .filter(socio -> esCumpleanios(fecha, socio.getFechaNacimiento()))
                .map(socio -> new DestinatarioCumpleanios(socio.getCorreoElectronico(),
                        socio.getNombre() + " " + socio.getApellido(),
                        "socio"))
                .forEach(destinatario -> destinatarios.putIfAbsent(destinatario.correo(), destinatario));

        empleadoRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc().stream()
                .filter(empleado -> empleado.getTipoEmpleado() == TipoEmpleado.ADMINISTRATIVO
                        || empleado.getTipoEmpleado() == TipoEmpleado.PROFESOR)
                .filter(empleado -> esCumpleanios(fecha, empleado.getFechaNacimiento()))
                .map(empleado -> new DestinatarioCumpleanios(empleado.getCorreoElectronico(),
                        empleado.getNombre() + " " + empleado.getApellido(),
                        tipoEmpleadoAetiqueta(empleado)))
                .forEach(destinatario -> destinatarios.putIfAbsent(destinatario.correo(), destinatario));

        return new ArrayList<>(destinatarios.values());
    }

    private void enviarCorreo(DestinatarioCumpleanios destinatario) {
        if (!StringUtils.hasText(destinatario.correo())) {
            LOGGER.warn("Se omitió el envío de correo de cumpleaños para {} porque no tiene correo válido",
                    destinatario.nombreCompleto());
            return;
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(correoRemitente);
        mensaje.setTo(destinatario.correo());
        mensaje.setSubject("¡Feliz cumpleaños!");
        mensaje.setText("Hola " + destinatario.nombreCompleto() + ", ¡muy feliz cumpleaños de parte de todo el equipo de Greedy Gym!");

        try {
            javaMailSender.send(mensaje);
            LOGGER.info("Se envió saludo de cumpleaños a {} ({})", destinatario.nombreCompleto(), destinatario.rol());
        } catch (MailException ex) {
            LOGGER.error("No se pudo enviar el correo de cumpleaños a {}", destinatario.correo(), ex);
        }
    }

    private boolean esCumpleanios(LocalDate hoy, LocalDate fechaNacimiento) {
        if (Objects.isNull(fechaNacimiento)) {
            return false;
        }
        return hoy.getMonthValue() == fechaNacimiento.getMonthValue()
                && hoy.getDayOfMonth() == fechaNacimiento.getDayOfMonth();
    }

    private String tipoEmpleadoAetiqueta(Empleado empleado) {
        return empleado.getTipoEmpleado() == TipoEmpleado.PROFESOR ? "profesor" : "administrativo";
    }

    private record DestinatarioCumpleanios(String correo, String nombreCompleto, String rol) {
    }
}
