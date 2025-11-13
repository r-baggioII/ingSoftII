package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoTelefonico;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WhatsAppService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final WhatsAppGateway gateway;
    private final String fromNumber;
    private final String supportPhone;

    public WhatsAppService(
        WhatsAppGateway gateway,
        @Value("${twilio.whatsapp.from}") String fromNumber,
        @Value("${greedy.support.contact.phone:+54 9 261 555 1234}") String supportPhone
    ) {
        this.gateway = gateway;
        this.fromNumber = fromNumber;
        this.supportPhone = supportPhone;
    }

    public WhatsAppSendResult enviarRecordatorioDevolucion(Alquiler alquiler, boolean manual) throws ErrorServiceException {
        if (alquiler == null) {
            throw new ErrorServiceException("No se indicó el alquiler para el recordatorio de WhatsApp");
        }
        Cliente cliente = alquiler.getCliente();
        String destino = obtenerDestinoWhatsApp(cliente)
                .orElseThrow(() -> new ErrorServiceException("El cliente no tiene un contacto telefónico válido"));

        String mensaje = construirMensaje(alquiler, manual);
        try {
            String sid = gateway.send(fromNumber, destino, mensaje);
            LOGGER.info("WhatsApp enviado al cliente {} para el alquiler {}. SID={}", cliente.getId(), alquiler.getId(), sid);
            return new WhatsAppSendResult(true, sid, null);
        } catch (Exception ex) {
            LOGGER.error("Error enviando WhatsApp para el alquiler {}: {}", alquiler.getId(), ex.getMessage(), ex);
            return new WhatsAppSendResult(false, null, ex.getMessage());
        }
    }

    private String construirMensaje(Alquiler alquiler, boolean manual) {
        Cliente cliente = alquiler.getCliente();
        Vehiculo vehiculo = alquiler.getVehiculo();
        CaracteristicaVehiculo caracteristica = vehiculo != null ? vehiculo.getCaracteristicaVehiculo() : null;
        LocalDate fechaDevolucion = alquiler.getFechaHasta();

        StringBuilder builder = new StringBuilder();
        builder.append("Hola ").append(nombreCompleto(cliente)).append(", ");
        builder.append("recordamos que la devolución del vehículo ");
        if (caracteristica != null) {
            builder.append(caracteristica.getMarca()).append(" ").append(caracteristica.getModelo());
        } else if (vehiculo != null && StringUtils.hasText(vehiculo.getPatente())) {
            builder.append("patente ").append(vehiculo.getPatente());
        } else {
            builder.append("asignado");
        }
        if (fechaDevolucion != null) {
            builder.append(" es el ").append(DATE_FORMATTER.format(fechaDevolucion)).append(".");
        } else {
            builder.append(" se encuentra programada para las próximas horas.");
        }
        builder.append(" Por favor presentate con 15 minutos de anticipación.");
        builder.append(" Ante dudas contactanos al ").append(supportPhone).append(".");
        if (manual) {
            builder.append(" (Recordatorio enviado manualmente por nuestro equipo).");
        }
        return builder.toString();
    }

    private Optional<String> obtenerDestinoWhatsApp(Cliente cliente) {
        if (cliente == null || cliente.getContactos() == null) {
            return Optional.empty();
        }
        return cliente.getContactos().stream()
                .filter(ContactoTelefonico.class::isInstance)
                .map(ContactoTelefonico.class::cast)
                .filter(contacto -> StringUtils.hasText(contacto.getTelefono()))
                .map(ContactoTelefonico::getTelefono)
                .map(this::normalizarTelefono)
                .filter(StringUtils::hasText)
                .findFirst();
    }

    private String normalizarTelefono(String telefono) {
        if (!StringUtils.hasText(telefono)) {
            return null;
        }
        String sanitized = telefono.replaceAll("[^0-9+]", "");
        if (!sanitized.startsWith("+")) {
            sanitized = "+54" + sanitized;
        }
        if (!sanitized.startsWith("whatsapp:")) {
            sanitized = "whatsapp:" + sanitized;
        }
        return sanitized;
    }

    private String nombreCompleto(Cliente cliente) {
        if (cliente == null) {
            return "cliente";
        }
        return (StringUtils.hasText(cliente.getNombre()) ? cliente.getNombre() : "")
                + " "
                + (StringUtils.hasText(cliente.getApellido()) ? cliente.getApellido() : "");
    }

    public record WhatsAppSendResult(boolean enviado, String sid, String error) {}
}
