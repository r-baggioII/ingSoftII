package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Mensaje;
import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.TipoMensaje;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CorreoMasivoSocioServicio {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorreoMasivoSocioServicio.class);

    private final SocioRepositorio socioRepositorio;
    private final NotificacionServicio notificacionServicio;
    private final MensajeServicio mensajeServicio;

    public CorreoMasivoSocioServicio(SocioRepositorio socioRepositorio,
                                     NotificacionServicio notificacionServicio,
                                     MensajeServicio mensajeServicio) {
        this.socioRepositorio = socioRepositorio;
        this.notificacionServicio = notificacionServicio;
        this.mensajeServicio = mensajeServicio;
    }

    public int enviarSaludoGeneral() {
        List<Socio> socios = socioRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc();
        if (socios.isEmpty()) {
            LOGGER.info("No hay socios activos para el envío masivo");
            return 0;
        }

        Mensaje plantilla = mensajeServicio.buscarMensajeActivoPorTipo(TipoMensaje.OTROS)
                .or(() -> mensajeServicio.buscarMensajeActivoPorTipo(TipoMensaje.PROMOCION))
                .orElse(null);
        String asunto = plantilla != null && StringUtils.hasText(plantilla.getTitulo())
                ? plantilla.getTitulo()
                : "Saludos de Greedy Gym";
        String textoBase = plantilla != null && StringUtils.hasText(plantilla.getTexto())
                ? plantilla.getTexto()
                : "Hola {{nombre_completo}}, ¿cómo estás? Recordá que Greedy Gym está para acompañarte en tu entrenamiento.";

        int enviados = 0;
        for (Socio socio : socios) {
            if (!StringUtils.hasText(socio.getCorreoElectronico())) {
                LOGGER.warn("Se omitió a {} {} por no tener correo electrónico", socio.getNombre(), socio.getApellido());
                continue;
            }
            String cuerpo = textoBase
                    .replace("{{nombre}}", socio.getNombre())
                    .replace("{{apellido}}", socio.getApellido())
                    .replace("{{nombre_completo}}", socio.getNombre() + " " + socio.getApellido());
            notificacionServicio.enviar(cuerpo, asunto, socio.getCorreoElectronico());
            enviados++;
        }
        LOGGER.info("Se enviaron {} saludos generales a socios", enviados);
        return enviados;
    }
}
