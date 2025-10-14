package com.example.greedy_empresa.notificaciones;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generador de contenido HTML para diferentes tipos de notificaciones
 */
public class GeneradorContenidoHtml {

    /**
     * Genera el contenido HTML para correos publicitarios
     */
    public static String generarContenidoPublicitario(String nombreCompleto) {
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Greedy Empresa - Oportunidades Exclusivas</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { display: inline-block; background-color: #28a745; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background-color: #6c757d; color: white; padding: 15px; text-align: center; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🚀 Greedy Empresa</h1>
                    <h2>¡Oportunidades Exclusivas te Esperan!</h2>
                </div>
                
                <div class="content">
                    <h3>Estimado/a %s,</h3>
                    
                    <p>¡Esperamos que tengas un excelente día! Desde <strong>Greedy Empresa</strong> queremos presentarte nuestras increíbles oportunidades de negocio.</p>
                    
                    <h4>🎯 ¿Por qué elegirnos?</h4>
                    <ul>
                        <li>✅ <strong>Calidad garantizada</strong> en todos nuestros productos</li>
                        <li>✅ <strong>Precios competitivos</strong> del mercado</li>
                        <li>✅ <strong>Atención personalizada</strong> 24/7</li>
                        <li>✅ <strong>Entrega rápida</strong> y confiable</li>
                        <li>✅ <strong>Soporte técnico</strong> especializado</li>
                    </ul>
                    
                    <p>🌟 <em>¡No pierdas esta oportunidad única de hacer crecer tu negocio con nosotros!</em></p>
                    
                    <div style="text-align: center;">
                        <a href="https://www.uncuyo.edu.ar/" class="button" target="_blank">
                            🎓 Conoce más sobre nosotros
                        </a>
                    </div>
                    
                    <p>Para más información, no dudes en contactarnos. ¡Estamos aquí para ayudarte a alcanzar el éxito!</p>
                </div>
                
                <div class="footer">
                    <p><strong>Greedy Empresa</strong> | Fecha: %s</p>
                    <p>📧 greedyteam0@gmail.com | 🌐 <a href="https://www.uncuyo.edu.ar/" style="color: #fff;">www.uncuyo.edu.ar</a></p>
                    <p><em>Este es un correo publicitario. Si no deseas recibir más correos, contacta con nosotros.</em></p>
                </div>
            </body>
            </html>
            """.formatted(nombreCompleto, fechaActual);
    }

    /**
     * Genera el contenido HTML para correos de fin de año
     */
    public static String generarContenidoFinDeAno(String nombreCompleto) {
        int anoActual = LocalDateTime.now().getYear();
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Greedy Empresa - Feliz Año Nuevo</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }
                    .header { background: linear-gradient(135deg, #ff6b6b, #ffd93d); color: white; padding: 30px; text-align: center; }
                    .content { padding: 25px; background-color: #f8f9fa; }
                    .button { display: inline-block; background-color: #ff6b6b; color: white; padding: 15px 35px; text-decoration: none; border-radius: 25px; margin: 20px 0; }
                    .footer { background-color: #2c3e50; color: white; padding: 20px; text-align: center; font-size: 12px; }
                    .celebration { font-size: 24px; text-align: center; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🎉 Greedy Empresa 🎉</h1>
                    <h2>¡Feliz Año Nuevo %d!</h2>
                </div>
                
                <div class="content">
                    <h3>Querido/a %s,</h3>
                    
                    <div class="celebration">
                        🥳 ✨ 🎊 🎆 🥂 ✨ 🥳
                    </div>
                    
                    <p>En este momento tan especial, queremos expresar nuestro más sincero <strong>agradecimiento</strong> por haber sido parte de nuestra familia empresarial durante este año.</p>
                    
                    <h4>🌟 Reflexionando sobre este año:</h4>
                    <p>Juntos hemos construido relaciones sólidas basadas en la <strong>confianza, calidad y excelencia</strong>. Tu apoyo y colaboración han sido fundamentales para nuestro crecimiento mutuo.</p>
                    
                    <h4>🚀 Mirando hacia el futuro:</h4>
                    <ul>
                        <li>💫 <strong>Nuevas oportunidades</strong> de negocio</li>
                        <li>💫 <strong>Innovaciones</strong> en nuestros servicios</li>
                        <li>💫 <strong>Crecimiento conjunto</strong> y sostenible</li>
                        <li>💫 <strong>Alianzas estratégicas</strong> más fuertes</li>
                    </ul>
                    
                    <p>🎯 <em>Que este nuevo año esté lleno de prosperidad, salud y éxito para ti y tu empresa!</em></p>
                    
                    <div style="text-align: center;">
                        <a href="https://www.uncuyo.edu.ar/" class="button" target="_blank">
                            🎓 Visita nuestro sitio web
                        </a>
                    </div>
                    
                    <div class="celebration">
                        🌟 ¡Que tengas un próspero Año Nuevo! 🌟
                    </div>
                </div>
                
                <div class="footer">
                    <p><strong>Greedy Empresa</strong> | Año %d</p>
                    <p>📧 greedyteam0@gmail.com | 🌐 <a href="https://www.uncuyo.edu.ar/" style="color: #fff;">www.uncuyo.edu.ar</a></p>
                    <p><em>Con cariño y mejores deseos para el nuevo año que comienza.</em></p>
                </div>
            </body>
            </html>
            """.formatted(anoActual, nombreCompleto, anoActual);
    }
}