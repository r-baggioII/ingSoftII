package com.example.greedy_empresa.notificaciones;

/**
 * Enumeración que define los tipos de notificaciones disponibles
 */
public enum TipoNotificacion {
    PUBLICITARIO("Correo Publicitario", "¡Oportunidades exclusivas con Greedy Empresa!"),
    FIN_DE_ANO("Correo de Fin de Año", "¡Feliz Año Nuevo desde Greedy Empresa!");
    
    private final String descripcion;
    private final String asuntoDefault;
    
    TipoNotificacion(String descripcion, String asuntoDefault) {
        this.descripcion = descripcion;
        this.asuntoDefault = asuntoDefault;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public String getAsuntoDefault() {
        return asuntoDefault;
    }
}