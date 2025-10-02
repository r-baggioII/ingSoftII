# 📧 Correo de Prueba Hardcodeado - Cambios Realizados

## 🎯 Modificaciones Implementadas

### **Correo de Destino Cambiado**
- **Anterior:** `greedyteam0@gmail.com`
- **Nuevo:** `f.julian2617@gmail.com`

## 📝 Archivos Modificados

### 1. **NotificacionService.java**
```java
// Línea actualizada en el método enviarCorreoPrueba()
.destinatario("f.julian2617@gmail.com")
.nombreCompleto("Julian - Administrador de Prueba")

// Logging actualizado
logger.info("📧 Enviando correo de prueba a: f.julian2617@gmail.com");
logger.info("✅ Correo de prueba enviado exitosamente a f.julian2617@gmail.com");
```

### 2. **Contenido del Correo HTML**
```html
<h3>Estimado Julian,</h3>
<li><strong>Destinatario de Prueba:</strong> f.julian2617@gmail.com</li>
```

### 3. **NotificacionController.java**
```java
// Mensaje de éxito actualizado
"✅ Correo de prueba enviado exitosamente a f.julian2617@gmail.com. " +
"Revisa la bandeja de entrada (y spam) para confirmar que la configuración funciona."
```

### 4. **Templates HTML (notificaciones/index.html)**
```html
<!-- Texto del botón -->
<small class="help-block">El correo de prueba se enviará a f.julian2617@gmail.com</small>

<!-- Información de configuración -->
<p><strong>El correo de prueba se envía a:</strong> <code>f.julian2617@gmail.com</code></p>
```

## 🚀 Cómo Probar

1. **Ejecutar la aplicación:**
   ```bash
   cd /home/julian/Documentos/juli/facultad/s6/is2/ingSoftII/SCRUM/ej1/greedy_empresa
   java -jar target/greedy_empresa-0.0.1-SNAPSHOT.jar
   ```

2. **Acceder a la aplicación:**
   - URL: http://localhost:8080/notificaciones
   - Hacer clic en **"Enviar Correo de Prueba"**

3. **Verificar el envío:**
   - Revisar los logs de la aplicación para errores
   - Revisar la bandeja de entrada de `f.julian2617@gmail.com`
   - **No olvides revisar la carpeta de spam**

## 📊 Qué Esperar

### ✅ **Si funciona correctamente:**
- Mensaje verde: "Correo de prueba enviado exitosamente a f.julian2617@gmail.com"
- Correo recibido en la bandeja de entrada de Julian
- Logs sin errores de autenticación

### ❌ **Si hay errores:**
- Mensaje rojo con detalles del error
- Logs detallados para diagnóstico
- Posibles causas: credenciales Gmail, conectividad, firewall

## 🔧 Configuración Actual

**Remitente:** `greedyteam0@gmail.com` (configurado en application.properties)
**Destinatario de Prueba:** `f.julian2617@gmail.com` (hardcodeado)
**Servidor SMTP:** `smtp.gmail.com:587`
**Protocolo:** STARTTLS

## 📋 Próximos Pasos

1. **Probar el botón** y verificar si llega el correo
2. **Si funciona:** El sistema está listo para envíos masivos
3. **Si no funciona:** Revisar logs y aplicar soluciones de autenticación Gmail
4. **Una vez estable:** Probar envíos a proveedores reales

## 💡 Ventajas de este Cambio

- ✅ **Prueba independiente** del correo remitente
- ✅ **Fácil verificación** en correo conocido
- ✅ **Debugging simplificado** con destinatario específico
- ✅ **No interfiere** con configuración de producción