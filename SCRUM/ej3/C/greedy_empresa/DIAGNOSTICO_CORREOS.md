# 📧 Diagnóstico del Sistema de Correos - Greedy Empresa

## 🎯 Correo de Destino para Pruebas

**El sistema está configurado para enviar correos de prueba a:** `greedyteam0@gmail.com`

**Remitente configurado:** `greedyteam0@gmail.com` (es decir, se envía a sí mismo)

## ❌ Error Reportado

```
Error al enviar el correo de prueba. Verifica la configuración del sistema.
```

## 🔍 Posibles Causas del Error

### 1. **Problema de Autenticación de Gmail** (Más Probable)
- Gmail **NO** acepta contraseñas normales para aplicaciones externas
- Necesitas una **"Contraseña de aplicación"** si tienes 2FA habilitado
- La contraseña actual `Greedy123` probablemente sea incorrecta

### 2. **Configuración de Seguridad de Google**
- Gmail requiere autenticación moderna
- Las aplicaciones "menos seguras" ya no están permitidas

### 3. **Problemas de Conectividad**
- Firewall bloqueando puerto 587
- Sin acceso a internet
- Proxy corporativo interfiriendo

## 🛠️ Soluciones

### Opción 1: Generar Contraseña de Aplicación (Recomendado)

1. **Ve a tu cuenta de Google:** https://myaccount.google.com/
2. **Seguridad → Autenticación de 2 factores**
3. **Contraseñas de aplicación**
4. **Generar nueva contraseña** para "Aplicación personalizada"
5. **Reemplazar** `Greedy123` con la nueva contraseña en `application.properties`

### Opción 2: Usar un Servicio SMTP Alternativo

```properties
# Configuración alternativa con Gmail App Password
spring.mail.username=greedyteam0@gmail.com
spring.mail.password=TU_CONTRASEÑA_DE_APLICACION_AQUI

# O usar otro proveedor como SendGrid, Mailgun, etc.
```

### Opción 3: Configuración Local para Desarrollo

```properties
# Para desarrollo local, usar un servidor SMTP de prueba
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

## 🔧 Mejoras Implementadas

### 1. **Logging Detallado**
- Agregado logging específico para mail: `logging.level.org.springframework.mail=DEBUG`
- Logging para Jakarta Mail: `logging.level.jakarta.mail=DEBUG`
- Mensajes de error más específicos

### 2. **Validación Mejorada**
- Validación de configuración básica
- Validación de conexión SMTP
- Manejo específico de errores de autenticación

### 3. **Información en la UI**
- Callout en la interfaz explicando la configuración de Gmail
- Instrucciones paso a paso para contraseñas de aplicación
- Indicador del correo de destino para pruebas

## 📋 Pasos para Depurar

1. **Ejecutar la aplicación** y verificar los logs de inicio
2. **Hacer clic en "Enviar Correo de Prueba"**
3. **Revisar los logs** para ver el error específico
4. **Aplicar la solución** según el tipo de error

## 🚀 Comando para Ver Logs en Tiempo Real

```bash
cd /home/julian/Documentos/juli/facultad/s6/is2/ingSoftII/SCRUM/ej1/greedy_empresa
java -jar target/greedy_empresa-0.0.1-SNAPSHOT.jar | grep -E "(MAIL|ERROR|NotificacionService)"
```

## 📊 Verificación del Estado

Una vez aplicada la solución, el sistema mostrará:
- ✅ "Estado Configuración: OK" (en verde)
- ✅ "Correo de prueba enviado exitosamente"
- 📧 El correo debería llegar a `greedyteam0@gmail.com`

## 🔐 Configuración de Seguridad Recomendada

Para producción, considera:
- Usar variables de entorno para credenciales
- Implementar un servicio de correo dedicado (SendGrid, AWS SES)
- Configurar dominios propios para el remitente
- Implementar rate limiting para evitar spam

## 📝 Próximos Pasos

1. **Aplicar una de las soluciones** de autenticación
2. **Probar el correo de prueba** nuevamente
3. **Una vez funcionando**, probar los envíos masivos
4. **Configurar los envíos programados** para producción