# Integración Auth0 + Flujo de Registro Intermedio - Greedy Cars

## Descripción General

Este proyecto implementa la autenticación social mediante **Auth0**, permitiendo a los usuarios iniciar sesión con Google y otras redes sociales. El sistema incluye un flujo de registro intermedio para usuarios nuevos que se autentican por primera vez con una cuenta social.

## Arquitectura del Flujo

### 1. Flujo de Autenticación Completo

```
Usuario → Click "Login con Google" → Redirección a Auth0 → Autenticación Google
   ↓
Auth0 callback → Frontend (/callback) → Obtener access_token + user profile
   ↓
Frontend → POST /api/auth0/post-login (con access token)
   ↓
Backend valida token JWT → Busca usuario por auth0Sub o email
   ↓
┌─────────────────────────────────────┐
│ ¿Usuario existe en BD?              │
├─────────────────────────────────────┤
│ SÍ  → Devolver USER_EXISTS          │
│       Frontend redirige a Home      │
├─────────────────────────────────────┤
│ NO  → Devolver REQUIRED_MORE_INFO   │
│       Frontend redirige a           │
│       /registro-intermedio          │
└─────────────────────────────────────┘
   ↓ (Si es usuario nuevo)
Usuario completa formulario (sin usuario/contraseña)
   ↓
Frontend → POST /api/auth0/registro-intermedio (con access token)
   ↓
Backend crea: Cliente + Dirección + Nacionalidad + Contactos + Imagen
Backend asocia auth0Sub al Usuario local
   ↓
Usuario puede usar la aplicación normalmente
```

## Configuración en Auth0

### 1. Crear una API en Auth0

1. Accede a tu [Dashboard de Auth0](https://manage.auth0.com/)
2. Ve a **Applications > APIs**
3. Click en **Create API**
4. Completa:
   - **Name**: Greedy Cars API
   - **Identifier**: `https://greedy-cars-api` (este será tu AUDIENCE)
   - **Signing Algorithm**: RS256
5. Click **Create**

### 2. Crear una Aplicación SPA en Auth0

1. Ve a **Applications > Applications**
2. Click en **Create Application**
3. Completa:
   - **Name**: Greedy Cars Client
   - **Application Type**: Single Page Web Applications
4. Click **Create**

### 3. Configurar la Aplicación

En la pestaña **Settings** de tu aplicación:

#### Application URIs

- **Allowed Callback URLs**: 
  ```
  http://localhost:8080/gredy_cars_client/callback,
  http://161.153.217.110:18081/gredy_cars_client/callback
  ```

- **Allowed Logout URLs**:
  ```
  http://localhost:8080/gredy_cars_client/,
  http://161.153.217.110:18081/gredy_cars_client/
  ```

- **Allowed Web Origins**:
  ```
  http://localhost:8080,
  http://161.153.217.110:18081
  ```

- **Allowed Origins (CORS)**:
  ```
  http://localhost:8080,
  http://161.153.217.110:18081
  ```

#### Guarda los siguientes valores:

- **Domain**: `dev-xxxxxx.us.auth0.com`
- **Client ID**: `xxxxxxxxxxxxxxxxxxxxxx`
- **Client Secret**: `xxxxxxxxxxxxxxxxxxxxxx`

### 4. Habilitar Google Social Connection

1. Ve a **Authentication > Social**
2. Click en **Google**
3. Activa la conexión
4. Completa las credenciales de Google OAuth (Client ID y Secret)
5. En **Applications**, habilita tu aplicación "Greedy Cars Client"

## Configuración en el Frontend (gredy_cars_client)

### 1. Actualizar application.properties

Edita `/src/main/resources/application.properties`:

```properties
# ========================================
# CONFIGURACIÓN AUTH0 (Solo para Frontend)
# ========================================
# Estos valores se inyectan en las vistas HTML para el SDK de Auth0
# La validación JWT se hace en el BACKEND, no aquí
auth0.domain=YOUR_AUTH0_DOMAIN
auth0.clientId=YOUR_AUTH0_CLIENT_ID
auth0.audience=YOUR_AUTH0_API_AUDIENCE
```

**Reemplaza**:
- `YOUR_AUTH0_DOMAIN` → ej: `dev-abc123.us.auth0.com`
- `YOUR_AUTH0_CLIENT_ID` → Client ID de tu aplicación
- `YOUR_AUTH0_API_AUDIENCE` → ej: `https://greedy-cars-api`

**IMPORTANTE**: El cliente NO necesita `client_secret` ni configuración de Resource Server.

### 2. Estructura de Archivos Creados

```
src/main/
├── java/com/gredy_cars_client/gredy_cars_client/
│   ├── config/
│   │   └── SecurityConfig.java (Actualizado con OAuth2)
│   └── shared/
│       ├── controller/
│       │   └── Auth0Controller.java
│       ├── dto/
│       │   ├── Auth0Config.java
│       │   ├── PostLoginResponse.java
│       │   └── RegistroIntermedioDTO.java
│       └── service/
│           └── Auth0Service.java
└── resources/
    └── templates/
        ├── login-auth0.html
        ├── callback.html
        └── registro-intermedio.html
```

## Configuración en el Backend (greedy_cars)

### 1. Agregar Dependencias al pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### 2. Actualizar application.properties

```properties
# Auth0 Resource Server
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://YOUR_AUTH0_DOMAIN/
auth0.audience=YOUR_AUTH0_API_AUDIENCE
```

### 3. Crear Endpoints en el Backend

Debes crear los siguientes endpoints en el backend:

#### `/api/auth0/check-user` (POST)

Verifica si un usuario existe por `auth0Sub` o `email`.

**Request Body**:
```json
{
  "auth0Sub": "auth0|123456789",
  "email": "user@example.com"
}
```

**Response**:
```json
{
  "exists": true,
  "userId": 123
}
```

#### `/api/auth0/registro-intermedio` (POST)

Crea un nuevo cliente con todos los datos.

**Request Body**:
```json
{
  "auth0Sub": "auth0|123456789",
  "email": "user@example.com",
  "registroData": {
    "nombre": "Juan",
    "apellido": "Pérez",
    "fechaNacimiento": "1990-01-01",
    "tipoDocumento": "DNI",
    "numeroDocumento": "12345678",
    "nacionalidad": "Argentina",
    "direccionEstadia": "Hotel XYZ",
    "direccion": { ... },
    "contactos": [ ... ],
    "imagen": { ... }
  }
}
```

**Response**:
```json
{
  "success": true,
  "message": "Cliente registrado exitosamente",
  "clienteId": 456
}
```

### 4. Modelo de Usuario

Asegúrate de que la entidad `Usuario` tenga un campo para almacenar el `auth0Sub`:

```java
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreUsuario;
    private String clave; // Puede ser null para usuarios de Auth0
    
    // Campo para identificar usuarios externos
    @Column(unique = true)
    private String externalId; // Guarda el auth0Sub aquí
    
    private Boolean isExternal; // true si es usuario de Auth0
    
    // ... otros campos
}
```

## Uso de la Aplicación

### 1. Login con Google

1. Accede a: `http://localhost:8080/gredy_cars_client/login-auth0`
2. Click en **"Continuar con Google"**
3. Serás redirigido a Auth0
4. Autentica con tu cuenta de Google
5. Auth0 te redirige a `/callback`

### 2. Primer Login (Usuario Nuevo)

Si es tu primer login:
1. El sistema detecta que no existe tu usuario
2. Te redirige a `/registro-intermedio`
3. Completa tus datos personales, dirección, contactos, etc.
4. Los datos de autenticación (usuario/contraseña) NO se piden
5. Click en **"Completar Registro"**
6. El sistema crea tu perfil y te redirige al home

### 3. Segundo Login (Usuario Existente)

En futuros logins:
1. Autenticas con Google
2. El sistema detecta que ya existes
3. Te redirige directamente al home
4. Puedes usar la aplicación normalmente

## Endpoints Públicos

Los siguientes endpoints NO requieren autenticación:

- `/` - Home
- `/login-auth0` - Página de login con Auth0
- `/callback` - Callback de Auth0
- `/registro` - Registro tradicional
- `/registro-intermedio` - Completar datos después de login social
- `/api/public/**` - Endpoints públicos del API
- `/css/**`, `/js/**`, `/images/**` - Recursos estáticos

## Endpoints Protegidos con Auth0

Requieren `Authorization: Bearer <access_token>`:

- `/api/auth0/post-login` - Verificar usuario después de login
- `/api/auth0/registro-intermedio` - Completar registro

## Seguridad

### Validación de Tokens

El `SecurityConfig` valida automáticamente:
- **Issuer**: Verifica que el token viene de tu dominio de Auth0
- **Audience**: Verifica que el token es para tu API
- **Signature**: Verifica que el token fue firmado por Auth0
- **Expiration**: Verifica que el token no ha expirado

### Flujo Seguro

1. El usuario nunca ve ni manipula el access token directamente
2. Todos los tokens son validados por Spring Security
3. El backend verifica la identidad con cada request
4. No se almacenan contraseñas para usuarios sociales

## Troubleshooting

### Error: "Invalid audience"

Verifica que:
- El `audience` en `application.properties` coincide con el Identifier de tu API en Auth0
- El frontend está solicitando el token con el `audience` correcto

### Error: "Invalid issuer"

Verifica que:
- El `issuer-uri` incluya el `/` al final: `https://dev-xxx.us.auth0.com/`
- El dominio sea exactamente el mismo que en Auth0

### Error: "Redirect URI mismatch"

Verifica que:
- Las URLs en **Allowed Callback URLs** coincidan exactamente
- Incluyas el puerto si estás en desarrollo local
- El context path esté incluido en la URL

### Error al obtener el token

Verifica que:
- La aplicación esté habilitada en la conexión social (Google)
- El `client_id` y `client_secret` sean correctos
- Las APIs requeridas estén habilitadas en Google Cloud Console

## Próximos Pasos

1. **Implementar el backend**: Crear los endpoints `/api/auth0/check-user` y `/api/auth0/registro-intermedio`
2. **Configurar roles**: Implementar JwtAuthenticationConverter para manejar roles locales
3. **Logout**: Implementar logout que invalide la sesión de Auth0
4. **Refresh tokens**: Manejar renovación automática de tokens
5. **MFA**: Habilitar autenticación multifactor en Auth0

## Recursos Adicionales

- [Auth0 Documentation](https://auth0.com/docs)
- [Auth0 SPA SDK](https://auth0.com/docs/libraries/auth0-spa-js)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

---

**Fecha de creación**: Noviembre 2025  
**Versión**: 1.0
