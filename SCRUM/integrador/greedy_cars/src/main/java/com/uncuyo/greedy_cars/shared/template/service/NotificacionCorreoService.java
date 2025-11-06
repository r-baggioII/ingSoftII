package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.ConfiguracionCorreoAutomatico;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoCorreoElectronico;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificacionCorreoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacionCorreoService.class);
    private static final DateTimeFormatter FECHA_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final String defaultFrom;
    private final ConfiguracionCorreoAutomaticoService configuracionCorreoAutomaticoService;

    public NotificacionCorreoService(
            JavaMailSender mailSender,
            ConfiguracionCorreoAutomaticoService configuracionCorreoAutomaticoService,
            @Value("${greedy.mail.default-from:noreply@greedy-cars.com}") String defaultFrom) {
        this.mailSender = mailSender;
        this.configuracionCorreoAutomaticoService = configuracionCorreoAutomaticoService;
        this.defaultFrom = defaultFrom;
    }

    @Async
    public void enviarCorreoBienvenida(String correoDestino, String nombre) {
        enviarCorreoBienvenida(correoDestino, nombre, null);
    }

    @Async
    public void enviarCorreoBienvenida(String correoDestino, String nombre, String empresaId) {
        if (!StringUtils.hasText(correoDestino)) {
            LOGGER.warn("No se pudo enviar correo de bienvenida: destino vacío");
            return;
        }
        String cuerpo = "Hola " + (StringUtils.hasText(nombre) ? nombre : "cliente")
                + ", te damos la bienvenida a Greedy Cars. Pronto recibirás nuestras novedades.";
        enviarCorreoSimple(correoDestino, "Bienvenido a Greedy Cars", cuerpo, empresaId);
    }

    @Async
    public void enviarRecordatorioDevolucion(Alquiler alquiler) {
        enviarRecordatorioDevolucion(alquiler, null);
    }

    @Async
    public void enviarRecordatorioDevolucion(Alquiler alquiler, String empresaId) {
        if (alquiler == null) {
            LOGGER.warn("No se indicó un alquiler para el recordatorio");
            return;
        }
        Cliente cliente = alquiler.getCliente();
        String correo = obtenerCorreoPersona(cliente);
        if (!StringUtils.hasText(correo)) {
            LOGGER.warn("El cliente del alquiler {} no tiene correo registrado", alquiler.getId());
            return;
        }
        String asunto = "Recordatorio de devolución de vehículo";
        String cuerpo = "Hola "
                + nombreCompleto(cliente)
                + ", te recordamos que debes devolver el vehículo el día "
                + FECHA_FORMAT.format(alquiler.getFechaHasta())
                + ".";
        // TODO: derivar empresaId desde el alquiler cuando el dominio la propague.
        enviarCorreoSimple(correo, asunto, cuerpo, empresaId);
    }

    @Async
    public void enviarPromocion(Cliente cliente, String codigoPromocion, Integer porcentaje) {
        enviarPromocion(cliente, codigoPromocion, porcentaje, null);
    }

    @Async
    public void enviarPromocion(Cliente cliente, String codigoPromocion, Integer porcentaje, String empresaId) {
        if (cliente == null) {
            LOGGER.warn("Cliente nulo para el envío de promoción");
            return;
        }
        String correo = obtenerCorreoPersona(cliente);
        if (!StringUtils.hasText(correo)) {
            LOGGER.warn("El cliente {} no tiene correo para enviar promoción", cliente.getId());
            return;
        }
        String asunto = "Nueva promoción para vos";
        String cuerpo = "Hola "
                + nombreCompleto(cliente)
                + ", usa el código "
                + (StringUtils.hasText(codigoPromocion) ? codigoPromocion : "GREEDY10")
                + " y obtené un "
                + (porcentaje == null ? "descuento especial" : porcentaje + "% de descuento")
                + " en tu próximo alquiler.";
        enviarCorreoSimple(correo, asunto, cuerpo, empresaId);
    }

    public void enviarCorreoSimple(String correoDestino, String asunto, String cuerpo) {
        enviarCorreoSimple(correoDestino, asunto, cuerpo, null);
    }

    public void enviarCorreoSimple(String correoDestino, String asunto, String cuerpo, String empresaId) {
        if (!StringUtils.hasText(correoDestino)) {
            LOGGER.warn("No se envió correo: destino vacío (asunto: {})", asunto);
            return;
        }
        try {
            MailSenderContext context = prepararContexto(empresaId);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(correoDestino);
            mailMessage.setFrom(context.from());
            mailMessage.setSubject(asunto);
            mailMessage.setText(cuerpo);
            context.sender().send(mailMessage);
            LOGGER.info("Correo enviado a {} con asunto '{}'", correoDestino, asunto);
        } catch (Exception e) {
            LOGGER.error("No se pudo enviar el correo a {}: {}", correoDestino, e.getMessage());
        }
    }

    private String obtenerCorreoPersona(Persona persona) {
        if (persona == null || persona.getContactos() == null) {
            return null;
        }
        return persona.getContactos().stream()
                .filter(Objects::nonNull)
                .filter(contacto -> contacto instanceof ContactoCorreoElectronico)
                .map(contacto -> (ContactoCorreoElectronico) contacto)
                .map(ContactoCorreoElectronico::getMail)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private String nombreCompleto(Persona persona) {
        if (persona == null) {
            return "cliente";
        }
        return (persona.getNombre() != null ? persona.getNombre() : "")
                + " "
                + (persona.getApellido() != null ? persona.getApellido() : "");
    }

    private MailSenderContext prepararContexto(String empresaId) {
        Optional<ConfiguracionCorreoAutomatico> configuracion =
                configuracionCorreoAutomaticoService.buscarActivaPorEmpresa(empresaId);
        if (configuracion.isEmpty()) {
            return new MailSenderContext(mailSender, defaultFrom);
        }

        ConfiguracionCorreoAutomatico config = configuracion.get();
        try {
            JavaMailSenderImpl customSender = new JavaMailSenderImpl();
            customSender.setHost(config.getSmtp());
            customSender.setPort(parsePort(config.getPuerto()));
            customSender.setUsername(config.getCorreo());
            customSender.setPassword(config.getClave());

            Properties props = customSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            boolean tls = config.getTls() == null || Boolean.TRUE.equals(config.getTls());
            props.put("mail.smtp.starttls.enable", Boolean.toString(tls));
            props.put("mail.smtp.starttls.required", Boolean.toString(tls));

            LOGGER.debug("Usando SMTP personalizado para empresa {}",
                    config.getEmpresa() != null ? config.getEmpresa().getId() : "desconocida");
            return new MailSenderContext(customSender, config.getCorreo());
        } catch (Exception e) {
            LOGGER.warn("No se pudo preparar SMTP personalizado para empresa {}: {}. Se usará la configuración global.",
                    config.getEmpresa() != null ? config.getEmpresa().getId() : "desconocida", e.getMessage());
            return new MailSenderContext(mailSender, defaultFrom);
        }
    }

    private int parsePort(String puerto) {
        try {
            return Integer.parseInt(puerto);
        } catch (NumberFormatException ex) {
            return 587;
        }
    }

    private record MailSenderContext(JavaMailSender sender, String from) {}
}
