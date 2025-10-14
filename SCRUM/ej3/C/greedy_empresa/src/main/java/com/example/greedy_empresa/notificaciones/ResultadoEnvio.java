package com.example.greedy_empresa.notificaciones;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Clase que representa el resultado de un envío de notificaciones
 */
@Data
@AllArgsConstructor
public class ResultadoEnvio {
    private int totalEnviados;
    private int totalFallidos;
    private int totalProveedores;
    private TipoNotificacion tipo;
    
    public boolean fueExitoso() {
        return totalFallidos == 0 && totalEnviados > 0;
    }
    
    public double getPorcentajeExito() {
        if (totalProveedores == 0) return 0.0;
        return (double) totalEnviados / totalProveedores * 100;
    }
}