package com.example.greedy_empresa.notificaciones;

import lombok.Builder;
import lombok.Data;

/**
 * Clase que representa los datos de una notificación
 */
@Data
@Builder
public class NotificacionData {
    private String destinatario;
    private String nombreCompleto;
    private String asunto;
    private String contenidoHtml;
    private TipoNotificacion tipo;
}