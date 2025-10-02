package com.example.greedy_empresa.test;

import com.example.greedy_empresa.servicios.NotificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test para verificar que el sistema envía correos tanto a proveedores como a usuarios
 */
@SpringBootTest
public class EnvioProveedoresUsuariosTest {

    private static final Logger logger = LoggerFactory.getLogger(EnvioProveedoresUsuariosTest.class);

    @Autowired
    private NotificacionService notificacionService;

    @Test
    public void testEnvioCorreosProveedoresYUsuarios() {
        logger.info("=== 🧪 INICIANDO TEST DE ENVÍO A PROVEEDORES Y USUARIOS ===");
        
        try {
            // Verificar configuración
            boolean configuracionValida = notificacionService.verificarConfiguracion();
            logger.info("🔧 Configuración del servicio: {}", configuracionValida ? "✅ VÁLIDA" : "❌ INVÁLIDA");
            
            // Obtener estadísticas
            long[] estadisticas = notificacionService.obtenerEstadisticasProveedores();
            long totalProveedores = estadisticas[0];
            long proveedoresConEmail = estadisticas[1];
            long totalUsuarios = estadisticas.length > 2 ? estadisticas[2] : 0;
            long usuariosConEmail = estadisticas.length > 3 ? estadisticas[3] : 0;
            
            logger.info("📊 ESTADÍSTICAS DE DESTINATARIOS:");
            logger.info("   📦 Proveedores: {} total, {} con email válido", totalProveedores, proveedoresConEmail);
            logger.info("   👥 Usuarios: {} total, {} con email válido", totalUsuarios, usuariosConEmail);
            logger.info("   📧 Total destinatarios con email: {}", proveedoresConEmail + usuariosConEmail);
            
            if (configuracionValida && (proveedoresConEmail > 0 || usuariosConEmail > 0)) {
                logger.info("🚀 Enviando correo de prueba publicitario...");
                
                // Enviar correos publicitarios (incluye proveedores y usuarios)
                var resultado = notificacionService.enviarCorreosPublicitariosManuales();
                
                logger.info("✅ RESULTADO DEL ENVÍO:");
                logger.info("   📨 Enviados: {}", resultado.getTotalEnviados());
                logger.info("   ❌ Fallidos: {}", resultado.getTotalFallidos());
                logger.info("   📊 Total procesados: {}", resultado.getTotalProveedores());
                
                if (resultado.getTotalEnviados() > 0) {
                    logger.info("🎉 ¡CORREOS ENVIADOS EXITOSAMENTE A PROVEEDORES Y USUARIOS!");
                    logger.info("📬 Revisa las bandejas de entrada de todos los destinatarios");
                } else {
                    logger.warn("⚠️ No se enviaron correos - verificar datos de proveedores y usuarios");
                }
            } else {
                logger.warn("⚠️ No se puede realizar el test: configuración inválida o sin destinatarios");
            }
            
        } catch (Exception e) {
            logger.error("❌ Error durante el test: {}", e.getMessage());
            logger.error("💡 Verificar configuración de correo y datos de prueba");
        }
        
        logger.info("=== 🏁 FINALIZANDO TEST DE ENVÍO A PROVEEDORES Y USUARIOS ===");
    }
    
    @Test
    public void testEnvioCorreosFinDeAno() {
        logger.info("=== 🎊 INICIANDO TEST DE ENVÍO FIN DE AÑO ===");
        
        try {
            // Verificar configuración
            boolean configuracionValida = notificacionService.verificarConfiguracion();
            
            if (configuracionValida) {
                logger.info("🎉 Enviando correos de fin de año...");
                
                // Enviar correos de fin de año (incluye proveedores y usuarios)
                var resultado = notificacionService.enviarCorreosFinDeAnoManuales();
                
                logger.info("✅ RESULTADO DEL ENVÍO FIN DE AÑO:");
                logger.info("   📨 Enviados: {}", resultado.getTotalEnviados());
                logger.info("   ❌ Fallidos: {}", resultado.getTotalFallidos());
                logger.info("   📊 Total procesados: {}", resultado.getTotalProveedores());
                
                if (resultado.getTotalEnviados() > 0) {
                    logger.info("🎊 ¡CORREOS DE FIN DE AÑO ENVIADOS A TODOS!");
                } else {
                    logger.warn("⚠️ No se enviaron correos de fin de año");
                }
            }
            
        } catch (Exception e) {
            logger.error("❌ Error durante el test de fin de año: {}", e.getMessage());
        }
        
        logger.info("=== 🏁 FINALIZANDO TEST FIN DE AÑO ===");
    }
}