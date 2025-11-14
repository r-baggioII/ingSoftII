package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.ConfiguracionCorreoAutomatico;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoCorreoElectronico;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.entity.Promocion;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;
import com.uncuyo.greedy_cars.shared.template.repository.PromocionRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class NotificacionCorreoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacionCorreoService.class);
    private static final DateTimeFormatter FECHA_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final String defaultFrom;
    private final String promocionesSubject;
    private final ConfiguracionCorreoAutomaticoService configuracionCorreoAutomaticoService;
    private final ClienteRepository clienteRepository;
    private final PromocionRepository promocionRepository;
    private final String supportPhone;

    public NotificacionCorreoService(
            JavaMailSender mailSender,
            ConfiguracionCorreoAutomaticoService configuracionCorreoAutomaticoService,
            ClienteRepository clienteRepository,
            PromocionRepository promocionRepository,
            @Value("${greedy.mail.default-from:noreply@greedy-cars.com}") String defaultFrom,
            @Value("${greedy.mail.promociones.asunto:Nueva promoción de Greedy Cars}") String promocionesSubject,
            @Value("${greedy.support.contact.phone:+54 9 261 555 1234}") String supportPhone) {
        this.mailSender = mailSender;
        this.configuracionCorreoAutomaticoService = configuracionCorreoAutomaticoService;
        this.defaultFrom = defaultFrom;
        this.clienteRepository = clienteRepository;
        this.promocionRepository = promocionRepository;
        this.promocionesSubject = promocionesSubject;
        this.supportPhone = supportPhone;
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
        String asunto = "Recordatorio de Devolución de Vehículo";
        String cuerpo = construirCuerpoRecordatorio(alquiler);
        enviarCorreoSimple(correo, asunto, cuerpo, empresaId);
    }

    private String construirCuerpoRecordatorio(Alquiler alquiler) {
        Cliente cliente = alquiler.getCliente();
        Vehiculo vehiculo = alquiler.getVehiculo();
        CaracteristicaVehiculo caracteristica = vehiculo != null ? vehiculo.getCaracteristicaVehiculo() : null;
        String descripcionVehiculo;
        if (caracteristica != null) {
            descripcionVehiculo = caracteristica.getMarca() + " " + caracteristica.getModelo();
        } else if (vehiculo != null && StringUtils.hasText(vehiculo.getPatente())) {
            descripcionVehiculo = "patente " + vehiculo.getPatente();
        } else {
            descripcionVehiculo = "vehículo reservado";
        }
        String fecha = alquiler.getFechaHasta() != null
                ? FECHA_FORMAT.format(alquiler.getFechaHasta())
                : "la fecha pactada";

        return """
Hola %s:

Te recordamos que la devolución del %s está programada para el %s a las 09:00 hs. Te pedimos presentarte 15 minutos antes con la documentación y llaves entregadas.

Si necesitás reprogramar o tenés dudas podés escribirnos a %s o comunicarte al %s.

Muchas gracias,
Equipo Greedy Cars
""".formatted(
                nombreCompleto(cliente),
                descripcionVehiculo,
                fecha,
                defaultFrom,
                supportPhone
        );
    }

    @Async
    @Transactional(readOnly = true)
    public void enviarNuevaPromocion(Promocion promocion) {
        if (promocion == null) {
            LOGGER.warn("No se indicó la promoción para enviar correos");
            return;
        }
        Promocion promocionPersistida = promocion.getId() != null
                ? promocionRepository.findByIdAndEliminadoIsFalse(promocion.getId()).orElse(promocion)
                : promocion;
        List<Cliente> destinatarios = obtenerDestinatariosParaPromocion(promocionPersistida).stream()
                .filter(cliente -> promocionPersistida.isAplicaATodos() || perteneceAClientesDestino(promocionPersistida, cliente))
                .collect(Collectors.toList());
        if (destinatarios.isEmpty()) {
            LOGGER.info("La promoción {} no tiene destinatarios con correo disponible", promocionPersistida.getCodigoDescuento());
            return;
        }
        for (Cliente cliente : destinatarios) {
            String correo = obtenerCorreoPersona(cliente);
            if (!StringUtils.hasText(correo)) {
                continue;
            }
            String cuerpo = construirCuerpoPromocion(cliente, promocionPersistida);
            enviarCorreoSimple(correo, promocionesSubject, cuerpo);
        }
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

    // TODO: evaluar si este job sigue siendo necesario una vez que los envíos al crear promociones cubran el negocio.
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

    private List<Cliente> obtenerDestinatariosParaPromocion(Promocion promocion) {
        if (promocion == null) {
            return Collections.emptyList();
        }
        if (promocion.isAplicaATodos()) {
            return clienteRepository.findAllByEliminadoIsFalse();
        }
        if (promocion.getClientesDestino() == null || promocion.getClientesDestino().isEmpty()) {
            return Collections.emptyList();
        }
        return promocion.getClientesDestino().stream()
                .collect(Collectors.toList());
    }

    private boolean perteneceAClientesDestino(Promocion promocion, Cliente cliente) {
        if (promocion == null || cliente == null || !StringUtils.hasText(cliente.getId())) {
            return false;
        }
        return promocion.getClientesDestino().stream()
                .map(Cliente::getId)
                .filter(StringUtils::hasText)
                .anyMatch(id -> id.equals(cliente.getId()));
    }

    private String construirCuerpoPromocion(Cliente cliente, Promocion promocion) {
        String nombre = nombreCompleto(cliente);
        return "Hola " + nombre + ",\n\n"
                + "Ya podés usar el código "
                + promocion.getCodigoDescuento()
                + " y obtener "
                + formatearDescuento(promocion)
                + " en tu próximo alquiler."
                + "\nVigencia: "
                + formatearVigencia(promocion)
                + ".\n\nUsá este código al crear tu próximo alquiler en Greedy Cars.";
    }

    private String formatearDescuento(Promocion promocion) {
        Double porcentaje = promocion.getPorcentajeDescuento();
        if (porcentaje == null) {
            return "un descuento especial";
        }
        boolean esEntero = Math.floor(porcentaje) == porcentaje;
        return esEntero
                ? String.format("%.0f%% de descuento", porcentaje)
                : String.format("%.2f%% de descuento", porcentaje);
    }

    private String formatearVigencia(Promocion promocion) {
        LocalDate inicio = promocion.getFechaInicioPromocion();
        LocalDate fin = promocion.getFechaFinPromocion();
        if (inicio != null && fin != null) {
            return "del " + FECHA_FORMAT.format(inicio) + " al " + FECHA_FORMAT.format(fin);
        }
        if (inicio != null) {
            return "a partir del " + FECHA_FORMAT.format(inicio);
        }
        if (fin != null) {
            return "hasta el " + FECHA_FORMAT.format(fin);
        }
        return "por tiempo limitado";
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
