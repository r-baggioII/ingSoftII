package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.ConfiguracionCorreoAutomatico;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoCorreoElectronico;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.entity.Promocion;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;
import com.uncuyo.greedy_cars.shared.template.repository.PromocionRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@EnableScheduling
public class NotificacionCorreoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacionCorreoService.class);
    private static final DateTimeFormatter FECHA_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final String defaultFrom;
    private final String promocionesSubject;
    private final ConfiguracionCorreoAutomaticoService configuracionCorreoAutomaticoService;
    private final ClienteRepository clienteRepository;
    private final PromocionRepository promocionRepository;

    public NotificacionCorreoService(
            JavaMailSender mailSender,
            ConfiguracionCorreoAutomaticoService configuracionCorreoAutomaticoService,
            ClienteRepository clienteRepository,
            PromocionRepository promocionRepository,
            @Value("${greedy.mail.default-from:noreply@greedy-cars.com}") String defaultFrom,
            @Value("${greedy.mail.promociones.asunto:Nueva promoción de Greedy Cars}") String promocionesSubject) {
        this.mailSender = mailSender;
        this.configuracionCorreoAutomaticoService = configuracionCorreoAutomaticoService;
        this.defaultFrom = defaultFrom;
        this.clienteRepository = clienteRepository;
        this.promocionRepository = promocionRepository;
        this.promocionesSubject = promocionesSubject;
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

    @Scheduled(cron = "${greedy.mail.promociones.cron:0 0 10 */7 * *}")
    public void enviarPromocionesActivas() {
        LocalDate hoy = LocalDate.now();
        List<Promocion> promociones = promocionRepository.findActivas(hoy);
        if (promociones.isEmpty()) {
            LOGGER.debug("No se encontraron promociones activas para la fecha {}", hoy);
            return;
        }

        List<Cliente> clientes = clienteRepository.findAllByEliminadoIsFalse();
        if (clientes.isEmpty()) {
            LOGGER.warn("No hay clientes activos para enviar promociones");
            return;
        }

        String descripcionPromos = construirDetallePromociones(promociones);
        for (Cliente cliente : clientes) {
            String correo = obtenerCorreoPersona(cliente);
            if (!StringUtils.hasText(correo)) {
                continue;
            }
            String cuerpo = armarCuerpoPromociones(cliente, descripcionPromos);
            enviarCorreoSimple(correo, promocionesSubject, cuerpo);
        }
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

    private String construirDetallePromociones(List<Promocion> promociones) {
        StringBuilder builder = new StringBuilder();
        for (Promocion promo : promociones) {
            builder.append("- Código: ")
                    .append(promo.getCodigoDescuento())
                    .append(" | Descuento: ")
                    .append(promo.getPorcentajeDescuento())
                    .append("%\n");
        }
        return builder.toString();
    }

    private String armarCuerpoPromociones(Cliente cliente, String detallePromos) {
        String nombre = nombreCompleto(cliente);
        return "Hola " + nombre + ",\n\n"
                + "Tenemos promociones vigentes que podés aprovechar en tu próximo alquiler:\n"
                + detallePromos
                + "\n¡Reservá tu vehículo y usá el código que más te guste en Greedy Cars!";
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
