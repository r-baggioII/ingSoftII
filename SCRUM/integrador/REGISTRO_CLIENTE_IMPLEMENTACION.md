# Integración del Registro Completo de Cliente + Usuario

## ✅ Implementación Completada

Se ha implementado exitosamente el flujo completo de registro público de clientes en Greedy Cars.

## 📋 Componentes Implementados

### Backend (Servidor - greedy_cars)

1. **DTO de Registro**: `RegistroClienteDTO.java`
   - Ubicación: `/src/main/java/com/uncuyo/greedy_cars/shared/template/dto/`
   - Contiene todos los campos necesarios para crear Usuario, Cliente y entidades relacionadas
   - Incluye DTOs internos para Dirección, Contactos e Imagen

2. **Servicio de Registro**: `RegistroService.java`
   - Ubicación: `/src/main/java/com/uncuyo/greedy_cars/shared/template/service/`
   - Procesa transaccionalmente la creación de:
     - País, Provincia, Departamento, Localidad
     - Dirección
     - Nacionalidad
     - Contactos (Correo y Teléfono)
     - Imagen (opcional, en Base64)
     - Cliente
     - Usuario (con contraseña encriptada y rol CLIENTE)

3. **Controlador REST**: `RegistroController.java`
   - Ubicación: `/src/main/java/com/uncuyo/greedy_cars/shared/template/controller/`
   - Endpoint público: `POST /api/registro`
   - Endpoint auxiliar: `GET /api/registro/verificar-usuario/{nombreUsuario}`

4. **Configuración de Seguridad**: `SecurityConfig.java` (actualizado)
   - Se agregó excepción explícita para `/api/registro/**`
   - Permite acceso sin token JWT

5. **Repositorio actualizado**: `ClienteRepository.java`
   - Se agregó método `existsByNumeroDocumentoAndEliminadoIsFalse`

### Frontend (Cliente - gredy_cars_client)

1. **Vista de Registro**: `registro-cliente.html`
   - Ubicación: `/src/main/resources/templates/`
   - Formulario completo con secciones:
     - Datos de Usuario (username, contraseña)
     - Datos Personales
     - Dirección completa (con jerarquía geográfica)
     - Contactos (email y teléfono obligatorios)
     - Imagen de perfil (opcional)
     - Términos y condiciones
   - JavaScript integrado para:
     - Validación del formulario
     - Conversión de imagen a Base64
     - Envío AJAX al endpoint del servidor
     - Redirección automática al login tras éxito

2. **Controlador Web**: `RegistroWebController.java`
   - Ubicación: `/src/main/java/com/gredy_cars_client/gredy_cars_client/shared/template/controller/`
   - Ruta pública: `GET /registro`
   - Sirve la página de registro

3. **Vista de Login**: `login.html` (actualizado)
   - Enlaces actualizados para apuntar a `/registro`

## 🚀 Cómo Probar el Flujo

### 1. Compilar y Ejecutar

#### Backend (Servidor)
```bash
cd /srv/greedy/ingSoftII/SCRUM/integrador/greedy_cars
./mvnw clean package
./mvnw spring-boot:run
```

#### Frontend (Cliente)
```bash
cd /srv/greedy/ingSoftII/SCRUM/integrador/gredy_cars_client/gredy_cars_client
./mvnw clean package
./mvnw spring-boot:run
```

### 2. Acceder al Formulario de Registro

- **URL**: `http://161.153.217.110:18082/registro`
- **Alternativa**: Desde el login, clic en "Regístrate aquí"

### 3. Completar el Formulario

**Datos mínimos requeridos:**

#### Datos de Usuario
- Nombre de Usuario: `cliente_prueba` (4-50 caracteres)
- Contraseña: `123456` (mínimo 6 caracteres)

#### Datos Personales
- Nombre: `Juan`
- Apellido: `Pérez`
- Fecha de Nacimiento: `1990-01-15`
- Tipo de Documento: `DNI`
- Número de Documento: `12345678`
- Nacionalidad: `Argentina`

#### Dirección
- Calle: `San Martín`
- Número: `1234`
- País: `Argentina`
- Provincia: `Mendoza`
- Departamento: `Capital`
- Localidad: `Mendoza`

#### Contactos
- Email: `juan.perez@email.com`
- Teléfono: `+54 261 1234567`

#### Opcionales
- Dirección de estadía
- Barrio, Piso, Puerta
- Código Postal
- Foto de perfil
- Observaciones

#### Términos
- ✅ Aceptar términos y condiciones

### 4. Enviar el Formulario

Al hacer clic en "Registrarse":
1. Se valida el formulario en el navegador
2. Se convierte la imagen a Base64 (si existe)
3. Se envía la petición POST a `http://161.153.217.110:18081/api/registro`
4. El servidor crea transaccionalmente todas las entidades
5. Si es exitoso:
   - Muestra mensaje de éxito
   - Redirige automáticamente a `/login` después de 2 segundos
6. Si hay error:
   - Muestra el mensaje de error
   - Permite reintentar

### 5. Verificar en Base de Datos

Después del registro exitoso, se habrán creado registros en las siguientes tablas:
- `usuario` - con contraseña encriptada (BCrypt) y rol CLIENTE
- `persona` y `clientes` - datos del cliente
- `pais`, `provincia`, `departamento`, `localidad` - jerarquía geográfica (si no existían)
- `direccion` - dirección del cliente
- `nacionalidad` - nacionalidad (si no existía)
- `contacto_correo_electronico` - contacto email
- `contacto_telefonico` - contacto teléfono
- `persona_contacto` - relación many-to-many
- `persona_direccion` - relación many-to-many
- `cliente_nacionalidad` - relación many-to-many
- `imagen` - foto de perfil (si se subió)

### 6. Iniciar Sesión

1. Después de la redirección, completar el formulario de login con:
   - Usuario: `cliente_prueba`
   - Contraseña: `123456`
2. Hacer clic en "Iniciar Sesión"
3. Verificar acceso exitoso al sistema

## 🔐 Características de Seguridad

- ✅ Endpoint `/api/registro` es público (sin JWT)
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Validación de usuario único antes de crear
- ✅ Validación de documento único antes de crear
- ✅ Transaccionalidad garantizada (@Transactional)
- ✅ Manejo de errores con mensajes claros
- ✅ CORS configurado para el cliente

## 📝 Validaciones Implementadas

### Backend
- Usuario único (nombre de usuario no duplicado)
- Documento único (número de documento no duplicado)
- Campos obligatorios con anotaciones @NotBlank, @NotNull
- Formato de email con @Email
- Fecha de nacimiento en el pasado con @Past
- Longitudes máximas con @Size

### Frontend
- Validación HTML5 nativa
- Formato de campos (email, date, etc.)
- Longitudes mínimas y máximas
- Campos requeridos
- Preview de imagen antes de subir

## 🎯 Flujo Completo

```
[Usuario] 
   ↓
[Accede a /registro]
   ↓
[Completa formulario]
   ↓
[JavaScript valida y construye JSON]
   ↓
[POST /api/registro (sin JWT)]
   ↓
[RegistroController recibe DTO]
   ↓
[RegistroService procesa transaccionalmente]
   ↓
[Crea: Geo → Dirección → Nacionalidad → Cliente → Contactos → Imagen → Usuario]
   ↓
[Retorna success con datos del cliente]
   ↓
[Frontend muestra mensaje de éxito]
   ↓
[Redirección automática a /login]
   ↓
[Usuario inicia sesión con credenciales creadas]
   ↓
[Acceso al sistema como CLIENTE]
```

## 🛠️ Mantenimiento y Extensiones Futuras

### Posibles Mejoras
1. **Validación asíncrona**: Verificar disponibilidad de usuario en tiempo real
2. **Captcha**: Agregar protección contra bots
3. **Confirmación por email**: Enviar link de activación
4. **Recuperación de contraseña**: Implementar flujo de reset
5. **Progreso visual**: Mostrar pasos del registro (wizard)
6. **OAuth2**: Integrar login con redes sociales (ya tiene UI preparada)
7. **Geolocalización**: Autocompletar dirección con Google Maps API

### Consideraciones de Producción
- Activar limitación de requests (rate limiting)
- Configurar CORS solo para dominios permitidos
- Implementar logging de intentos de registro
- Agregar métricas y monitoreo
- Considerar doble verificación (email/SMS)

## ✅ Resultado Final

El sistema ahora permite que cualquier usuario:
1. Acceda públicamente a la página de registro
2. Complete un formulario intuitivo y bien estructurado
3. Cree automáticamente su cuenta de cliente con todas las entidades relacionadas
4. Inicie sesión inmediatamente después del registro
5. Acceda a las funcionalidades del sistema como CLIENTE

**Todo el proceso es automático, transaccional y seguro, sin necesidad de intervención de un administrador.**
