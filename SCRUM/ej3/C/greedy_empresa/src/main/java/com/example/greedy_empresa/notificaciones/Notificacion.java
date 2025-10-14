package com.example.greedy_empresa.notificaciones;

import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

/**
 * Clase principal para el manejo de notificaciones por correo electrónico
 * Contiene toda la lógica de envío de correos
 */
@Component
public class Notificacion {

    private static final Logger logger = LoggerFactory.getLogger(Notificacion.class);
    private static final String REMITENTE = "greedyteam0@gmail.com";
    private static final long PAUSA_ENTRE_ENVIOS = 1000; // 1 segundo

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía notificaciones a una lista de proveedores
     * @param proveedores Lista de proveedores destinatarios
     * @param tipo Tipo de notificación a enviar
     * @return Resultado del envío con estadísticas
     */
    public ResultadoEnvio enviarNotificaciones(List<Proveedor> proveedores, TipoNotificacion tipo) {
        logger.info("Iniciando envío de notificaciones tipo: {}", tipo.getDescripcion());
        
        int totalEnviados = 0;
        int totalFallidos = 0;
        int totalProveedores = proveedores.size();

        for (Proveedor proveedor : proveedores) {
            if (tieneEmailValido(proveedor)) {
                try {
                    NotificacionData notificacion = construirNotificacion(proveedor, tipo);
                    enviarNotificacionIndividual(notificacion);
                    totalEnviados++;
                    
                    // Pausa entre envíos para no sobrecargar el servidor de correo
                    Thread.sleep(PAUSA_ENTRE_ENVIOS);
                    
                } catch (Exception e) {
                    logger.error("Error al enviar notificación a {}: {}", 
                        proveedor.getPersona().getCorreoElectronico(), e.getMessage());
                    totalFallidos++;
                }
            } else {
                logger.warn("Proveedor sin email válido: {}", proveedor.getCuit());
                totalFallidos++;
            }
        }

        ResultadoEnvio resultado = new ResultadoEnvio(totalEnviados, totalFallidos, totalProveedores, tipo);
        logger.info("Envío completado. Enviados: {}, Fallidos: {}, Total: {}", 
                   totalEnviados, totalFallidos, totalProveedores);
        
        return resultado;
    }

    /**
     * Envía notificaciones a una lista de usuarios
     * @param usuarios Lista de usuarios destinatarios
     * @param tipo Tipo de notificación a enviar
     * @return Resultado del envío con estadísticas
     */
    public ResultadoEnvio enviarNotificacionesUsuarios(List<Usuario> usuarios, TipoNotificacion tipo) {
        logger.info("Iniciando envío de notificaciones tipo: {} a usuarios", tipo.getDescripcion());
        
        int totalEnviados = 0;
        int totalFallidos = 0;
        int totalUsuarios = usuarios.size();

        for (Usuario usuario : usuarios) {
            if (tieneEmailValidoUsuario(usuario)) {
                try {
                    NotificacionData notificacion = construirNotificacionUsuario(usuario, tipo);
                    enviarNotificacionIndividual(notificacion);
                    totalEnviados++;
                    
                    // Pausa entre envíos para no sobrecargar el servidor de correo
                    Thread.sleep(PAUSA_ENTRE_ENVIOS);
                    
                } catch (Exception e) {
                    logger.error("Error al enviar notificación a usuario {}: {}", 
                        usuario.getPersona().getCorreoElectronico(), e.getMessage());
                    totalFallidos++;
                }
            } else {
                logger.warn("Usuario sin email válido: {}", usuario.getUsername());
                totalFallidos++;
            }
        }

        ResultadoEnvio resultado = new ResultadoEnvio(totalEnviados, totalFallidos, totalUsuarios, tipo);
        logger.info("Envío a usuarios completado. Enviados: {}, Fallidos: {}, Total: {}", 
                   totalEnviados, totalFallidos, totalUsuarios);
        
        return resultado;
    }

    /**
     * Envía una notificación individual
     * @param notificacion Datos de la notificación a enviar
     * @throws MessagingException Si ocurre un error en el envío
     */
    public void enviarNotificacionIndividual(NotificacionData notificacion) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(REMITENTE);
        helper.setTo(notificacion.getDestinatario());
        helper.setSubject(notificacion.getAsunto());
        helper.setText(notificacion.getContenidoHtml(), true);

        mailSender.send(message);
        
        logger.info("Notificación {} enviada exitosamente a: {}", 
                   notificacion.getTipo().getDescripcion(), 
                   notificacion.getDestinatario());
    }

    /**
     * Construye los datos de una notificación basada en el proveedor y tipo
     * @param proveedor Proveedor destinatario
     * @param tipo Tipo de notificación
     * @return Datos de la notificación construida
     */
    private NotificacionData construirNotificacion(Proveedor proveedor, TipoNotificacion tipo) {
        String nombreCompleto = proveedor.getPersona().getNombre() + " " + proveedor.getPersona().getApellido();
        String contenidoHtml = generarContenidoHtml(nombreCompleto, tipo);
        
        return NotificacionData.builder()
                .destinatario(proveedor.getPersona().getCorreoElectronico())
                .nombreCompleto(nombreCompleto)
                .asunto(tipo.getAsuntoDefault())
                .contenidoHtml(contenidoHtml)
                .tipo(tipo)
                .build();
    }

    /**
     * Construye los datos de una notificación basada en el usuario y tipo
     * @param usuario Usuario destinatario
     * @param tipo Tipo de notificación
     * @return Datos de la notificación construida
     */
    private NotificacionData construirNotificacionUsuario(Usuario usuario, TipoNotificacion tipo) {
        String nombreCompleto = usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido();
        String contenidoHtml = generarContenidoHtml(nombreCompleto, tipo);
        
        return NotificacionData.builder()
                .destinatario(usuario.getPersona().getCorreoElectronico())
                .nombreCompleto(nombreCompleto)
                .asunto(tipo.getAsuntoDefault())
                .contenidoHtml(contenidoHtml)
                .tipo(tipo)
                .build();
    }

    /**
     * Genera el contenido HTML según el tipo de notificación
     * @param nombreCompleto Nombre completo del destinatario
     * @param tipo Tipo de notificación
     * @return Contenido HTML generado
     */
    private String generarContenidoHtml(String nombreCompleto, TipoNotificacion tipo) {
        return switch (tipo) {
            case PUBLICITARIO -> GeneradorContenidoHtml.generarContenidoPublicitario(nombreCompleto);
            case FIN_DE_ANO -> GeneradorContenidoHtml.generarContenidoFinDeAno(nombreCompleto);
        };
    }

    /**
     * Verifica si un proveedor tiene un email válido
     * @param proveedor Proveedor a verificar
     * @return true si tiene email válido, false en caso contrario
     */
    private boolean tieneEmailValido(Proveedor proveedor) {
        return proveedor.getPersona() != null && 
               proveedor.getPersona().getCorreoElectronico() != null && 
               !proveedor.getPersona().getCorreoElectronico().trim().isEmpty();
    }

    /**
     * Verifica si un usuario tiene un email válido
     * @param usuario Usuario a verificar
     * @return true si tiene email válido, false en caso contrario
     */
    private boolean tieneEmailValidoUsuario(Usuario usuario) {
        return usuario.getPersona() != null && 
               usuario.getPersona().getCorreoElectronico() != null && 
               !usuario.getPersona().getCorreoElectronico().trim().isEmpty();
    }

    /**
     * Cuenta cuántos proveedores de una lista tienen email válido
     * @param proveedores Lista de proveedores
     * @return Cantidad de proveedores con email válido
     */
    public long contarProveedoresConEmail(List<Proveedor> proveedores) {
        return proveedores.stream()
                .filter(this::tieneEmailValido)
                .count();
    }

    /**
     * Cuenta cuántos usuarios de una lista tienen email válido
     * @param usuarios Lista de usuarios
     * @return Cantidad de usuarios con email válido
     */
    public long contarUsuariosConEmail(List<Usuario> usuarios) {
        return usuarios.stream()
                .filter(this::tieneEmailValidoUsuario)
                .count();
    }

    /**
     * Valida la configuración del servicio de correo
     * @return true si la configuración es válida
     */
    public boolean validarConfiguracion() {
        try {
            // Verificar que el mailSender esté configurado
            if (mailSender == null) {
                logger.error("❌ JavaMailSender no está configurado");
                return false;
            }
            
            logger.info("✅ JavaMailSender configurado correctamente");
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Error en la configuración del servicio de correo: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida la configuración de correo intentando una conexión de prueba
     * @return true si puede conectarse al servidor SMTP
     */
    public boolean validarConexionSMTP() {
        try {
            // Crear una sesión de prueba
            jakarta.mail.Session session = mailSender.createMimeMessage().getSession();
            if (session != null) {
                logger.info("✅ Sesión SMTP creada exitosamente");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("❌ Error al validar conexión SMTP: {}", e.getMessage());
            return false;
        }
    }
}