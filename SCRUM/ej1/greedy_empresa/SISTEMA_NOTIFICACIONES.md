# Sistema de Notificaciones por Correo Electrónico

## Resumen
Se ha creado un sistema completo de notificaciones por correo electrónico para la aplicación Greedy Empresa. El sistema permite enviar correos publicitarios y de fin de año a los proveedores de manera automática y manual.

## Arquitectura del Sistema

### 📦 Paquete `com.example.greedy_empresa.notificaciones`

#### 1. **Notificacion.java** - Clase Principal
- **Responsabilidad**: Contiene toda la lógica de envío de correos electrónicos
- **Funcionalidades**:
  - Envío masivo de notificaciones a listas de proveedores
  - Envío individual de notificaciones
  - Construcción de datos de notificación
  - Validación de configuración
  - Conteo de proveedores con email válido

#### 2. **TipoNotificacion.java** - Enumeración
- **PUBLICITARIO**: Correos promocionales con oportunidades de negocio
- **FIN_DE_ANO**: Correos de saludos y agradecimientos de fin de año

#### 3. **NotificacionData.java** - Modelo de Datos
- Contiene: destinatario, nombreCompleto, asunto, contenidoHtml, tipo
- Utiliza patrón Builder para construcción

#### 4. **ResultadoEnvio.java** - Modelo de Resultado
- Estadísticas del envío: totalEnviados, totalFallidos, totalProveedores
- Métodos de utilidad: fueExitoso(), getPorcentajeExito()

#### 5. **GeneradorContenidoHtml.java** - Generador de Contenido
- Genera contenido HTML responsivo para cada tipo de notificación
- Incluye estilos CSS inline y enlaces a https://www.uncuyo.edu.ar/

### 📧 Configuración de Correo

#### application.properties
```properties
# Email configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=greedyteam0@gmail.com
spring.mail.password=Greedy123
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

### 🔄 Servicios y Controladores

#### NotificacionService.java
- **Envíos Programados**:
  - Correos publicitarios: Día 5 de cada mes a las 15:00
  - Correos de fin de año: 31 de diciembre a las 15:00
- **Envíos Manuales**: Métodos para envío manual desde la interfaz web
- **Estadísticas**: Información sobre proveedores y configuración

#### NotificacionController.java
- **Rutas**:
  - `GET /notificaciones`: Página principal del sistema
  - `POST /notificaciones/enviar-publicitarios`: Envío manual de correos publicitarios
  - `POST /notificaciones/enviar-fin-ano`: Envío manual de correos de fin de año

### 🎨 Interfaz Web

#### /notificaciones/index.html
- **Dashboard informativo** con estadísticas de proveedores
- **Botones de envío manual** para cada tipo de correo
- **Información del sistema** y configuración
- **Alertas** para mostrar resultados de envíos
- **Confirmación** antes de envíos masivos

### ⏰ Programación Automática

Los envíos automáticos están configurados con anotaciones `@Scheduled`:

```java
@Scheduled(cron = "0 0 15 5 * ?")    // Día 5 de cada mes a las 15:00
@Scheduled(cron = "0 0 15 31 12 ?")  // 31 de diciembre a las 15:00
```

### 📱 Contenido de los Correos

#### Correos Publicitarios
- **Asunto**: "¡Oportunidades exclusivas con Greedy Empresa!"
- **Contenido**: 
  - Saludo personalizado
  - Lista de beneficios y servicios
  - Botón con enlace a la universidad
  - Información de contacto

#### Correos de Fin de Año
- **Asunto**: "¡Feliz Año Nuevo desde Greedy Empresa!"
- **Contenido**:
  - Mensaje de agradecimiento
  - Reflexión sobre el año
  - Deseos para el nuevo año
  - Botón con enlace a la universidad

### 🔧 Características Técnicas

- **Pausa entre envíos**: 1 segundo para no sobrecargar el servidor
- **Validación de emails**: Solo se envía a proveedores con email válido
- **Logging completo**: Registro detallado de todos los envíos y errores
- **Manejo de errores**: Captura y reporte de errores individuales
- **Diseño responsivo**: Los correos se ven bien en móviles y desktop

### 🚀 Funcionalidades Implementadas

✅ **Envío automático** programado para fechas específicas
✅ **Envío manual** desde la interfaz web
✅ **Contenido HTML** atractivo y profesional
✅ **Enlaces externos** a https://www.uncuyo.edu.ar/
✅ **Estadísticas** de envío en tiempo real
✅ **Validación** de configuración del sistema
✅ **Integración** con el sistema de proveedores existente
✅ **Interfaz web** intuitiva y responsiva
✅ **Logging** completo para auditoría

### 📋 Menú de Navegación

Se agregó la opción "Notificaciones" en el menú lateral bajo la sección "HERRAMIENTAS", junto con la opción existente de "Migración".

### 🔒 Seguridad y Configuración

- Las credenciales de correo están configuradas en `application.properties`
- El sistema valida la configuración antes de permitir envíos
- Los errores se manejan graciosamente sin interrumpir la aplicación

## Uso del Sistema

1. **Acceder** a `/notificaciones` desde el menú lateral
2. **Verificar** que la configuración esté correcta (indicador verde)
3. **Revisar** las estadísticas de proveedores
4. **Hacer clic** en el botón de envío deseado
5. **Confirmar** el envío en el diálogo
6. **Monitorear** el resultado en las alertas de la página

El sistema está completamente integrado y listo para usar, siguiendo las mejores prácticas de Spring Boot y manteniendo la consistencia con el resto de la aplicación.