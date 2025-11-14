# 📧 Configuración de Gmail para Envío Real

## ⚠️ Estado Actual
El sistema está configurado con **envío simulado** para pruebas. Los emails se muestran en la consola pero no se envían realmente.

## 🔧 Para Activar Envío Real con Gmail

### 1. Generar Contraseña de Aplicación en Gmail

1. Ve a **Google Account Settings**: https://myaccount.google.com/
2. Navega a **Seguridad** → **Verificación en 2 pasos**
3. **Activa la verificación en 2 pasos** si no está habilitada
4. Ve a **Contraseñas de aplicaciones**
5. Selecciona:
   - **Aplicación**: Correo
   - **Dispositivo**: Otra (nombre personalizado)
6. Escribe: `Greedy Empresa Notifications`
7. Google generará una contraseña de 16 caracteres: `abcd efgh ijkl mnop`

### 2. Actualizar Configuración

En el archivo `MailConfig.java`, reemplaza:

```java
// ANTES (configuración simulada)
@Primary
public JavaMailSender javaMailSenderMock() {

// DESPUÉS (configuración real)
@Primary  
public JavaMailSender javaMailSenderGmail() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);
    mailSender.setUsername("greedyteam0@gmail.com");
    mailSender.setPassword("TU_CONTRASEÑA_DE_APLICACION_AQUI"); // Los 16 caracteres
    
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    
    return mailSender;
}
```

### 3. Eliminar Configuración Simulada

Comenta o elimina el archivo `MailConfigAlternativo.java` para que use la configuración real.

### 4. Recompilar y Probar

```bash
cd /home/julian/Documentos/juli/facultad/s6/is2/ingSoftII/SCRUM/ej1/greedy_empresa
mvn clean package -DskipTests
java -jar target/greedy_empresa-0.0.1-SNAPSHOT.jar
```

## 📊 Funcionalidades del Sistema

### Envío Automático Programado
- **Día 5 de cada mes a las 15:00**: Email publicitario
- **31 de diciembre a las 15:00**: Saludo de fin de año

### Envío Manual
- Accede a: http://localhost:8080/notificaciones
- Botones para enviar cada tipo de notificación
- **Test Email** hardcodeado a: `f.julian2617@gmail.com`

### Contenido de Emails
- **HTML responsive** con diseño profesional
- **Botón CTA** que lleva a: https://www.uncuyo.edu.ar/
- **Branding corporativo** de Greedy Empresa

## 🔍 Verificación de Funcionamiento

1. **Logs de la aplicación**: Verás mensajes de envío exitoso
2. **Consola web**: La interfaz muestra estadísticas de envío  
3. **Bandeja de entrada**: Los emails llegarán al destinatario

## 🛠️ Troubleshooting

### Error de Autenticación
- Verifica que la cuenta tenga verificación en 2 pasos activada
- Usa la contraseña de aplicación, NO la contraseña normal
- Verifica que la cuenta no tenga "Acceso de aplicaciones menos seguras"

### Emails no llegan
- Revisa la carpeta de spam
- Verifica la dirección de email del proveedor en la base de datos
- Comprueba los logs de la aplicación para errores

### Límites de Gmail
- Gmail tiene límites diarios de envío
- Para alto volumen considera usar servicios como SendGrid o AWS SES

---

📝 **Nota**: El sistema actual funciona perfectamente en modo simulación. Para producción, simplemente sigue estos pasos para activar el envío real.