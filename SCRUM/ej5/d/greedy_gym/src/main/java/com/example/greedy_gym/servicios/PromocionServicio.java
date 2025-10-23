package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Mensaje;
import com.example.greedy_gym.entidades.Promocion;
import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.TipoMensaje;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.PromocionRepositorio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class PromocionServicio {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromocionServicio.class);

    private final PromocionRepositorio promocionRepositorio;
    private final SocioRepositorio socioRepositorio;
    private final NotificacionServicio notificacionServicio;

    public Promocion crearPromocion(Usuario usuario, LocalDateTime fechaEnvio, String titulo, String texto,
                                    Set<String> socioIds, boolean enviarATodos) {
        validar(usuario, fechaEnvio, titulo, texto);
        if (!enviarATodos && CollectionUtils.isEmpty(socioIds)) {
            throw new ValidationException("Debe seleccionar destinatarios o enviar a todos los socios");
        }
        Promocion promocion = new Promocion();
        promocion.setUsuario(usuario);
        promocion.setTitulo(titulo.trim());
        promocion.setTexto(texto.trim());
        promocion.setTipoMensaje(TipoMensaje.PROMOCION);
        promocion.setFechaEnvioPromocion(fechaEnvio);
        promocion.setDestinatarios(enviarATodos ? new LinkedHashSet<>() : resolverDestinatarios(socioIds));
        promocion.setCantidadSociosEnviados(0L);
        return promocionRepositorio.save(promocion);
    }

    public Promocion modificarPromocion(String id, Usuario usuario, LocalDateTime fechaEnvio, String titulo,
                                        String texto, Set<String> socioIds, boolean enviarATodos) {
        Promocion promocion = promocionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoción no encontrada: " + id));
        if (promocion.isEnviada()) {
            throw new ValidationException("La promoción ya fue enviada y no puede modificarse");
        }
        validar(usuario, fechaEnvio, titulo, texto);
        if (!enviarATodos && CollectionUtils.isEmpty(socioIds)) {
            throw new ValidationException("Debe seleccionar destinatarios o enviar a todos los socios");
        }
        promocion.setUsuario(usuario);
        promocion.setTitulo(titulo.trim());
        promocion.setTexto(texto.trim());
        promocion.setTipoMensaje(TipoMensaje.PROMOCION);
        promocion.setFechaEnvioPromocion(fechaEnvio);
        promocion.setDestinatarios(enviarATodos ? new LinkedHashSet<>() : resolverDestinatarios(socioIds));
        return promocionRepositorio.save(promocion);
    }

    @Transactional(readOnly = true)
    public Promocion buscarPromocion(String id) {
        Promocion promocion = promocionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoción no encontrada: " + id));
        promocion.getDestinatarios().size();
        return promocion;
    }

    @Transactional(readOnly = true)
    public List<Promocion> listarPromociones() {
        List<Promocion> promociones = promocionRepositorio.findByEliminadoFalseOrderByCreadoEnDesc();
        promociones.forEach(p -> p.getDestinatarios().size());
        return promociones;
    }

    public void eliminarPromocion(String id) {
        Promocion promocion = promocionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoción no encontrada: " + id));
        promocion.setEliminado(true);
        promocionRepositorio.save(promocion);
    }

    public int enviarPromocionAhora(String id) {
        Promocion promocion = promocionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoción no encontrada: " + id));
        return procesarPromocion(promocion, LocalDateTime.now());
    }

    @Scheduled(cron = "${greedy.mail.promociones.cron:0 0/5 * * * ?}")
    public void procesarPromocionesPendientes() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Promocion> pendientes = promocionRepositorio
                .findByEliminadoFalseAndEnviadaFalseAndFechaEnvioPromocionLessThanEqual(ahora);
        if (pendientes.isEmpty()) {
            return;
        }
        for (Promocion promocion : pendientes) {
            procesarPromocion(promocion, ahora);
        }
    }

    private int procesarPromocion(Promocion promocion, LocalDateTime fechaEnvioReal) {
        if (promocion.isEnviada()) {
            return 0;
        }
        List<Socio> destinatarios = CollectionUtils.isEmpty(promocion.getDestinatarios())
                ? socioRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc()
                : List.copyOf(promocion.getDestinatarios());
        if (destinatarios.isEmpty()) {
            LOGGER.info("No hay destinatarios para la promoción {}", promocion.getId());
            promocion.setCantidadSociosEnviados(0L);
            promocion.setEnviada(true);
            promocion.setFechaEnvioReal(fechaEnvioReal);
            promocionRepositorio.save(promocion);
            return 0;
        }
        int enviados = 0;
        for (Socio socio : destinatarios) {
            if (!StringUtils.hasText(socio.getCorreoElectronico())) {
                continue;
            }
            String cuerpoPersonalizado = construirMensaje(promocion, socio);
            notificacionServicio.enviar(cuerpoPersonalizado, promocion.getTitulo(), socio.getCorreoElectronico());
            enviados++;
        }
        promocion.setCantidadSociosEnviados((long) enviados);
        promocion.setEnviada(true);
        promocion.setFechaEnvioReal(fechaEnvioReal);
        promocionRepositorio.save(promocion);
        LOGGER.info("Promoción {} enviada a {} destinatarios", promocion.getId(), enviados);
        return enviados;
    }

    private Set<Socio> resolverDestinatarios(Set<String> socioIds) {
        if (CollectionUtils.isEmpty(socioIds)) {
            return new LinkedHashSet<>();
        }
        return socioIds.stream()
                .map(id -> socioRepositorio.findByIdAndEliminadoFalse(id)
                        .orElseThrow(() -> new EntityNotFoundException("Socio no encontrado: " + id)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void validar(Usuario usuario, LocalDateTime fechaEnvio, String titulo, String texto) {
        if (usuario == null) {
            throw new ValidationException("El usuario es obligatorio");
        }
        if (fechaEnvio == null) {
            throw new ValidationException("La fecha de envío es obligatoria");
        }
        if (!StringUtils.hasText(titulo)) {
            throw new ValidationException("El título es obligatorio");
        }
        if (!StringUtils.hasText(texto)) {
            throw new ValidationException("El texto es obligatorio");
        }
    }

    private String construirMensaje(Mensaje mensaje, Socio socio) {
        String texto = mensaje.getTexto();
        if (!StringUtils.hasText(texto)) {
            return "\n";
        }
        return texto.replace("{{nombre}}", socio.getNombre())
                .replace("{{apellido}}", socio.getApellido())
                .replace("{{nombre_completo}}", socio.getNombre() + " " + socio.getApellido());
    }
}
