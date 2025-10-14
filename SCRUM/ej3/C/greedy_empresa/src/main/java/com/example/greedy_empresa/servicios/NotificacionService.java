package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.entidades.Usuario;
import com.example.greedy_empresa.notificaciones.Notificacion;
import com.example.greedy_empresa.notificaciones.ResultadoEnvio;
import com.example.greedy_empresa.notificaciones.TipoNotificacion;
import com.example.greedy_empresa.repositorios.ProveedorRepository;
import com.example.greedy_empresa.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import java.util.List;

/**
 * Servicio que maneja el envío de notificaciones y los envíos programados
 */
@Service
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    @Autowired
    private Notificacion notificacion;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Envía correos publicitarios programados el día 5 de cada mes a las 15:00
     */
    @Scheduled(cron = "0 0 15 5 * ?") // segundo, minuto, hora, día del mes, mes, día de la semana
    public void enviarCorreosPublicitariosAutomaticos() {
        logger.info("🚀 Iniciando envío automático de correos publicitarios");
        
        try {
            // Enviar a proveedores
            List<Proveedor> proveedores = obtenerProveedoresActivos();
            ResultadoEnvio resultadoProveedores = notificacion.enviarNotificaciones(proveedores, TipoNotificacion.PUBLICITARIO);
            
            // Enviar a usuarios
            List<Usuario> usuarios = obtenerUsuariosActivos();
            ResultadoEnvio resultadoUsuarios = notificacion.enviarNotificacionesUsuarios(usuarios, TipoNotificacion.PUBLICITARIO);
            
            // Calcular totales
            int totalEnviados = resultadoProveedores.getTotalEnviados() + resultadoUsuarios.getTotalEnviados();
            int totalFallidos = resultadoProveedores.getTotalFallidos() + resultadoUsuarios.getTotalFallidos();
            int totalDestinatarios = resultadoProveedores.getTotalProveedores() + resultadoUsuarios.getTotalProveedores();
            
            logger.info("✅ Envío automático de correos publicitarios completado. " +
                       "Enviados: {}, Fallidos: {}, Total: {} (Proveedores: {}, Usuarios: {})", 
                       totalEnviados, totalFallidos, totalDestinatarios,
                       resultadoProveedores.getTotalProveedores(), resultadoUsuarios.getTotalProveedores());
            
        } catch (Exception e) {
            logger.error("❌ Error en el envío automático de correos publicitarios: {}", e.getMessage());
        }
    }

    /**
     * Envía correos de fin de año programados el 31 de diciembre a las 15:00
     */
    @Scheduled(cron = "0 0 15 31 12 ?") // 31 de diciembre a las 15:00
    public void enviarCorreosFinDeAnoAutomaticos() {
        logger.info("🎉 Iniciando envío automático de correos de fin de año");
        
        try {
            // Enviar a proveedores
            List<Proveedor> proveedores = obtenerProveedoresActivos();
            ResultadoEnvio resultadoProveedores = notificacion.enviarNotificaciones(proveedores, TipoNotificacion.FIN_DE_ANO);
            
            // Enviar a usuarios
            List<Usuario> usuarios = obtenerUsuariosActivos();
            ResultadoEnvio resultadoUsuarios = notificacion.enviarNotificacionesUsuarios(usuarios, TipoNotificacion.FIN_DE_ANO);
            
            // Calcular totales
            int totalEnviados = resultadoProveedores.getTotalEnviados() + resultadoUsuarios.getTotalEnviados();
            int totalFallidos = resultadoProveedores.getTotalFallidos() + resultadoUsuarios.getTotalFallidos();
            int totalDestinatarios = resultadoProveedores.getTotalProveedores() + resultadoUsuarios.getTotalProveedores();
            
            logger.info("✅ Envío automático de correos de fin de año completado. " +
                       "Enviados: {}, Fallidos: {}, Total: {} (Proveedores: {}, Usuarios: {})", 
                       totalEnviados, totalFallidos, totalDestinatarios,
                       resultadoProveedores.getTotalProveedores(), resultadoUsuarios.getTotalProveedores());
            
        } catch (Exception e) {
            logger.error("❌ Error en el envío automático de correos de fin de año: {}", e.getMessage());
        }
    }

    /**
     * Envía correos publicitarios de forma manual
     * @return Resultado del envío
     * @throws MessagingException Si ocurre un error en el envío
     */
    public ResultadoEnvio enviarCorreosPublicitariosManuales() throws MessagingException {
        logger.info("📧 Iniciando envío manual de correos publicitarios");
        
        if (!notificacion.validarConfiguracion()) {
            throw new MessagingException("La configuración del servicio de correo no es válida");
        }
        
        // Enviar a proveedores
        List<Proveedor> proveedores = obtenerProveedoresActivos();
        ResultadoEnvio resultadoProveedores = notificacion.enviarNotificaciones(proveedores, TipoNotificacion.PUBLICITARIO);
        
        // Enviar a usuarios
        List<Usuario> usuarios = obtenerUsuariosActivos();
        ResultadoEnvio resultadoUsuarios = notificacion.enviarNotificacionesUsuarios(usuarios, TipoNotificacion.PUBLICITARIO);
        
        // Calcular totales
        int totalEnviados = resultadoProveedores.getTotalEnviados() + resultadoUsuarios.getTotalEnviados();
        int totalFallidos = resultadoProveedores.getTotalFallidos() + resultadoUsuarios.getTotalFallidos();
        int totalDestinatarios = resultadoProveedores.getTotalProveedores() + resultadoUsuarios.getTotalProveedores();
        
        logger.info("📊 Envío manual de correos publicitarios completado. " +
                   "Enviados: {}, Fallidos: {}, Total: {} (Proveedores: {}, Usuarios: {})", 
                   totalEnviados, totalFallidos, totalDestinatarios,
                   resultadoProveedores.getTotalProveedores(), resultadoUsuarios.getTotalProveedores());
        
        // Retornar un resultado combinado
        return new ResultadoEnvio(totalEnviados, totalFallidos, totalDestinatarios, TipoNotificacion.PUBLICITARIO);
    }

    /**
     * Envía correos de fin de año de forma manual
     * @return Resultado del envío
     * @throws MessagingException Si ocurre un error en el envío
     */
    public ResultadoEnvio enviarCorreosFinDeAnoManuales() throws MessagingException {
        logger.info("🎊 Iniciando envío manual de correos de fin de año");
        
        if (!notificacion.validarConfiguracion()) {
            throw new MessagingException("La configuración del servicio de correo no es válida");
        }
        
        // Enviar a proveedores
        List<Proveedor> proveedores = obtenerProveedoresActivos();
        ResultadoEnvio resultadoProveedores = notificacion.enviarNotificaciones(proveedores, TipoNotificacion.FIN_DE_ANO);
        
        // Enviar a usuarios
        List<Usuario> usuarios = obtenerUsuariosActivos();
        ResultadoEnvio resultadoUsuarios = notificacion.enviarNotificacionesUsuarios(usuarios, TipoNotificacion.FIN_DE_ANO);
        
        // Calcular totales
        int totalEnviados = resultadoProveedores.getTotalEnviados() + resultadoUsuarios.getTotalEnviados();
        int totalFallidos = resultadoProveedores.getTotalFallidos() + resultadoUsuarios.getTotalFallidos();
        int totalDestinatarios = resultadoProveedores.getTotalProveedores() + resultadoUsuarios.getTotalProveedores();
        
        logger.info("📊 Envío manual de correos de fin de año completado. " +
                   "Enviados: {}, Fallidos: {}, Total: {} (Proveedores: {}, Usuarios: {})", 
                   totalEnviados, totalFallidos, totalDestinatarios,
                   resultadoProveedores.getTotalProveedores(), resultadoUsuarios.getTotalProveedores());
        
        // Retornar un resultado combinado
        return new ResultadoEnvio(totalEnviados, totalFallidos, totalDestinatarios, TipoNotificacion.FIN_DE_ANO);
    }

    /**
     * Obtiene estadísticas de proveedores y usuarios para notificaciones
     * @return Array con [total_proveedores, proveedores_con_email, total_usuarios, usuarios_con_email]
     */
    public long[] obtenerEstadisticasProveedores() {
        List<Proveedor> proveedores = obtenerProveedoresActivos();
        List<Usuario> usuarios = obtenerUsuariosActivos();
        
        long totalProveedores = proveedores.size();
        long proveedoresConEmail = notificacion.contarProveedoresConEmail(proveedores);
        long totalUsuarios = usuarios.size();
        long usuariosConEmail = notificacion.contarUsuariosConEmail(usuarios);
        
        return new long[]{totalProveedores, proveedoresConEmail, totalUsuarios, usuariosConEmail};
    }

    /**
     * Verifica si el servicio de notificaciones está configurado correctamente
     * @return true si está configurado, false en caso contrario
     */
    public boolean verificarConfiguracion() {
        return notificacion.validarConfiguracion();
    }

    /**
     * Envía un correo de prueba para verificar la configuración
     * @return true si se envió exitosamente, false en caso contrario
     */
    public boolean enviarCorreoPrueba() {
        logger.info("🧪 Enviando correo de prueba para verificar configuración");
        
        try {
            // Validar configuración básica
            if (!notificacion.validarConfiguracion()) {
                logger.error("❌ La configuración básica del servicio de correo no es válida");
                return false;
            }
            
            // Validar conexión SMTP
            if (!notificacion.validarConexionSMTP()) {
                logger.error("❌ No se puede establecer conexión con el servidor SMTP");
                return false;
            }

            // Crear datos de prueba
            logger.info("📧 Enviando correo de prueba a: f.julian2617@gmail.com");
            
            com.example.greedy_empresa.notificaciones.NotificacionData prueba = 
                com.example.greedy_empresa.notificaciones.NotificacionData.builder()
                    .destinatario("f.julian2617@gmail.com")
                    .nombreCompleto("Julian - Administrador de Prueba")
                    .asunto("🧪 Prueba de Configuración - Greedy Empresa")
                    .contenidoHtml(generarContenidoPrueba())
                    .tipo(com.example.greedy_empresa.notificaciones.TipoNotificacion.PUBLICITARIO)
                    .build();

            // Enviar correo de prueba
            notificacion.enviarNotificacionIndividual(prueba);
            
            logger.info("✅ Correo de prueba enviado exitosamente a f.julian2617@gmail.com");
            return true;
            
        } catch (jakarta.mail.AuthenticationFailedException e) {
            logger.error("❌ Error de autenticación SMTP: {}", e.getMessage());
            logger.error("💡 Verifica las credenciales de correo. Gmail requiere una 'Contraseña de aplicación' si tienes 2FA habilitado.");
            return false;
        } catch (jakarta.mail.MessagingException e) {
            logger.error("❌ Error de mensajería: {}", e.getMessage());
            logger.error("💡 Verifica la configuración SMTP y la conectividad de red.");
            return false;
        } catch (Exception e) {
            logger.error("❌ Error inesperado al enviar correo de prueba: {}", e.getMessage());
            logger.error("💡 Revisa la configuración completa del sistema de correo.");
            return false;
        }
    }

    /**
     * Genera el contenido HTML para el correo de prueba
     * @return Contenido HTML del correo de prueba
     */
    private String generarContenidoPrueba() {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        String fechaHora = ahora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Prueba de Configuración - Greedy Empresa</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }
                    .header { background-color: #17a2b8; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { display: inline-block; background-color: #28a745; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background-color: #6c757d; color: white; padding: 15px; text-align: center; font-size: 12px; }
                    .success-box { background-color: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🧪 Greedy Empresa</h1>
                    <h2>Prueba de Configuración del Sistema</h2>
                </div>
                
                <div class="content">
                    <div class="success-box">
                        <h3>✅ ¡Configuración Exitosa!</h3>
                        <p><strong>El sistema de notificaciones está funcionando correctamente.</strong></p>
                    </div>
                    
                    <h3>Estimado Julian,</h3>
                    
                    <p>Este es un correo de prueba automatizado para verificar que la configuración del sistema de notificaciones de <strong>Greedy Empresa</strong> está funcionando correctamente.</p>
                    
                    <h4>📊 Detalles de la Prueba:</h4>
                    <ul>
                        <li><strong>Fecha y Hora:</strong> %s</li>
                        <li><strong>Destinatario de Prueba:</strong> f.julian2617@gmail.com</li>
                        <li><strong>Servidor SMTP:</strong> smtp.gmail.com:587</li>
                        <li><strong>Remitente:</strong> greedyteam0@gmail.com</li>
                        <li><strong>Protocolo:</strong> SMTP con STARTTLS</li>
                        <li><strong>Estado:</strong> ✅ Operativo</li>
                    </ul>
                    
                    <h4>🎯 Funcionalidades Verificadas:</h4>
                    <ul>
                        <li>✅ Conexión al servidor de correo</li>
                        <li>✅ Autenticación SMTP</li>
                        <li>✅ Envío de correos HTML</li>
                        <li>✅ Codificación UTF-8</li>
                        <li>✅ Enlaces externos</li>
                    </ul>
                    
                    <p>🌟 <em>El sistema está listo para enviar notificaciones a los proveedores.</em></p>
                    
                    <div style="text-align: center;">
                        <a href="https://www.uncuyo.edu.ar/" class="button" target="_blank">
                            🎓 Visitar Universidad Nacional de Cuyo
                        </a>
                    </div>
                    
                    <p><strong>Próximos pasos:</strong></p>
                    <p>Ahora puedes usar el sistema para enviar correos publicitarios y de fin de año a todos los proveedores registrados.</p>
                </div>
                
                <div class="footer">
                    <p><strong>Greedy Empresa - Sistema de Notificaciones</strong></p>
                    <p>📧 greedyteam0@gmail.com | 🌐 <a href="https://www.uncuyo.edu.ar/" style="color: #fff;">www.uncuyo.edu.ar</a></p>
                    <p><em>Este es un correo de prueba generado automáticamente por el sistema.</em></p>
                </div>
            </body>
            </html>
            """.formatted(fechaHora);
    }

    /**
     * Obtiene la lista de proveedores activos
     * @return Lista de proveedores activos ordenados por CUIT
     */
    private List<Proveedor> obtenerProveedoresActivos() {
        return proveedorRepository.findByEliminadoFalseOrderByCuit();
    }

    /**
     * Obtiene la lista de usuarios activos
     * @return Lista de usuarios activos ordenados por username
     */
    private List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> !usuario.isEliminado())
                .sorted((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()))
                .toList();
    }
}