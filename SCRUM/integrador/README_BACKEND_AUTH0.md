# Implementación Backend - Auth0 Integration

## Endpoints Requeridos en el Backend (greedy_cars)

Este documento describe los endpoints que deben implementarse en el backend para soportar la integración con Auth0.

---

## 1. Configuración del Backend

### 1.1. Dependencias (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 1.2. application.properties

```properties
# Auth0 Resource Server Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://YOUR_AUTH0_DOMAIN/
auth0.audience=YOUR_AUTH0_API_AUDIENCE
```

### 1.3. SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    
    @Value("${auth0.audience}")
    private String audience;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/api/registro/**").permitAll()
                .requestMatchers("/api/auth0/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
        
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        
        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }
    
    static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String audience;

        AudienceValidator(String audience) {
            this.audience = audience;
        }

        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            if (jwt.getAudience().contains(audience)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Invalid audience", null)
            );
        }
    }
}
```

---

## 2. Modelo de Datos

### 2.1. Actualizar Entidad Usuario

Agregar campos para usuarios externos (Auth0):

```java
@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String nombreUsuario;
    
    private String clave; // Puede ser null para usuarios Auth0
    
    // ===== CAMPOS PARA AUTH0 =====
    
    @Column(unique = true, name = "external_id")
    private String externalId; // Almacena el auth0Sub (ej: "auth0|123456789")
    
    @Column(name = "is_external")
    private Boolean isExternal; // true si es usuario de Auth0
    
    @Column(name = "provider")
    private String provider; // "AUTH0", "GOOGLE", "FACEBOOK", etc.
    
    @Column(name = "email_verified")
    private Boolean emailVerified; // Si el email fue verificado por Auth0
    
    // ... otros campos existentes
}
```

---

## 3. DTOs

### 3.1. CheckUserRequest.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserRequest {
    private String auth0Sub;
    private String email;
}
```

### 3.2. CheckUserResponse.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserResponse {
    private Boolean exists;
    private Long userId;
    private String message;
}
```

### 3.3. RegistroIntermedioRequest.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroIntermedioRequest {
    private String auth0Sub;
    private String email;
    private RegistroClienteDTO registroData;
}
```

---

## 4. Endpoints a Implementar

### 4.1. POST /api/auth0/check-user

**Descripción**: Verifica si un usuario autenticado con Auth0 ya existe en la base de datos local.

**Request**:
```json
POST /api/auth0/check-user
Content-Type: application/json

{
  "auth0Sub": "auth0|123456789",
  "email": "user@example.com"
}
```

**Response (Usuario Existe)**:
```json
HTTP 200 OK

{
  "exists": true,
  "userId": 456,
  "message": "Usuario encontrado"
}
```

**Response (Usuario No Existe)**:
```json
HTTP 200 OK

{
  "exists": false,
  "userId": null,
  "message": "Usuario no encontrado"
}
```

**Implementación**:

```java
@RestController
@RequestMapping("/api/auth0")
@RequiredArgsConstructor
public class Auth0Controller {
    
    private final UsuarioService usuarioService;
    
    @PostMapping("/check-user")
    public ResponseEntity<CheckUserResponse> checkUser(@RequestBody CheckUserRequest request) {
        
        // Buscar por externalId (auth0Sub)
        Optional<Usuario> usuarioBySub = usuarioService.findByExternalId(request.getAuth0Sub());
        
        if (usuarioBySub.isPresent()) {
            return ResponseEntity.ok(CheckUserResponse.builder()
                .exists(true)
                .userId(usuarioBySub.get().getId())
                .message("Usuario encontrado")
                .build());
        }
        
        // Buscar por email como fallback
        Optional<Usuario> usuarioByEmail = usuarioService.findByEmail(request.getEmail());
        
        if (usuarioByEmail.isPresent()) {
            // Asociar el auth0Sub al usuario existente
            Usuario usuario = usuarioByEmail.get();
            usuario.setExternalId(request.getAuth0Sub());
            usuario.setIsExternal(true);
            usuario.setProvider("AUTH0");
            usuarioService.save(usuario);
            
            return ResponseEntity.ok(CheckUserResponse.builder()
                .exists(true)
                .userId(usuario.getId())
                .message("Usuario encontrado y vinculado con Auth0")
                .build());
        }
        
        return ResponseEntity.ok(CheckUserResponse.builder()
            .exists(false)
            .userId(null)
            .message("Usuario no encontrado")
            .build());
    }
}
```

---

### 4.2. POST /api/auth0/registro-intermedio

**Descripción**: Completa el registro de un usuario que se autenticó con Auth0 por primera vez.

**Headers**:
```
Authorization: Bearer <auth0_access_token>
Content-Type: application/json
```

**Request**:
```json
POST /api/auth0/registro-intermedio
Content-Type: application/json
Authorization: Bearer eyJhbGc...

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
    "direccion": {
      "calle": "Av. San Martín",
      "numeracion": "1234",
      "barrio": "Centro",
      "pisoCasa": null,
      "puertaManzana": null,
      "observacion": null,
      "pais": "Argentina",
      "provincia": "Mendoza",
      "departamento": "Capital",
      "localidad": "Mendoza",
      "codigoPostal": "5500"
    },
    "contactos": [
      {
        "tipoContacto": "PERSONAL",
        "mail": "user@example.com",
        "telefono": null,
        "tipoTelefono": null,
        "observacion": null
      },
      {
        "tipoContacto": "PERSONAL",
        "mail": null,
        "telefono": "+54 261 1234567",
        "tipoTelefono": "CELULAR",
        "observacion": null
      }
    ],
    "imagen": {
      "nombre": "profile.jpg",
      "mime": "image/jpeg",
      "contenidoBase64": "iVBORw0KGgo..."
    }
  }
}
```

**Response (Éxito)**:
```json
HTTP 200 OK

{
  "success": true,
  "message": "Cliente registrado exitosamente",
  "clienteId": 789,
  "usuarioId": 456
}
```

**Response (Error)**:
```json
HTTP 400 BAD REQUEST

{
  "success": false,
  "message": "Error al registrar el cliente: El documento ya existe",
  "errors": ["numeroDocumento: ya existe en el sistema"]
}
```

**Implementación**:

```java
@PostMapping("/registro-intermedio")
public ResponseEntity<Map<String, Object>> registroIntermedio(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody RegistroIntermedioRequest request) {
    
    try {
        // Extraer datos del JWT validado
        String auth0Sub = jwt.getSubject();
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");
        
        // Verificar que el auth0Sub coincida
        if (!auth0Sub.equals(request.getAuth0Sub())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Token no coincide con el usuario"));
        }
        
        // Verificar que el usuario no exista
        if (usuarioService.findByExternalId(auth0Sub).isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "El usuario ya existe"));
        }
        
        // Iniciar transacción
        RegistroClienteDTO registroDTO = request.getRegistroData();
        
        // 1. Crear Nacionalidad
        Nacionalidad nacionalidad = nacionalidadService.crearNacionalidad(
            registroDTO.getNacionalidad()
        );
        
        // 2. Crear Dirección completa (País, Provincia, Departamento, Localidad)
        Direccion direccion = direccionService.crearDireccionCompleta(
            registroDTO.getDireccion()
        );
        
        // 3. Crear Usuario (sin contraseña, marcado como externo)
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(email); // Usar email como username
        usuario.setClave(null); // Sin contraseña
        usuario.setExternalId(auth0Sub);
        usuario.setIsExternal(true);
        usuario.setProvider("AUTH0");
        usuario.setEmailVerified(true); // Auth0 ya verificó el email
        usuario.setActivo(true);
        
        // Asignar rol CLIENTE
        Rol rolCliente = rolService.findByNombre("CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
        usuario.setRol(rolCliente);
        
        usuario = usuarioService.save(usuario);
        
        // 4. Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setNombre(registroDTO.getNombre());
        cliente.setApellido(registroDTO.getApellido());
        cliente.setFechaNacimiento(LocalDate.parse(registroDTO.getFechaNacimiento()));
        cliente.setTipoDocumento(TipoDocumento.valueOf(registroDTO.getTipoDocumento()));
        cliente.setNumeroDocumento(registroDTO.getNumeroDocumento());
        cliente.setDireccionEstadia(registroDTO.getDireccionEstadia());
        cliente.setNacionalidad(nacionalidad);
        cliente.setDireccion(direccion);
        cliente.setUsuario(usuario);
        cliente.setActivo(true);
        
        cliente = clienteService.save(cliente);
        
        // 5. Crear Contactos
        for (var contactoDTO : registroDTO.getContactos()) {
            Contacto contacto = new Contacto();
            contacto.setTipoContacto(TipoContacto.valueOf(contactoDTO.getTipoContacto()));
            contacto.setMail(contactoDTO.getMail());
            contacto.setTelefono(contactoDTO.getTelefono());
            if (contactoDTO.getTipoTelefono() != null) {
                contacto.setTipoTelefono(TipoTelefono.valueOf(contactoDTO.getTipoTelefono()));
            }
            contacto.setObservacion(contactoDTO.getObservacion());
            contacto.setCliente(cliente);
            contactoService.save(contacto);
        }
        
        // 6. Crear Imagen (si existe)
        if (registroDTO.getImagen() != null) {
            Imagen imagen = new Imagen();
            imagen.setNombre(registroDTO.getImagen().getNombre());
            imagen.setMime(registroDTO.getImagen().getMime());
            imagen.setContenido(Base64.getDecoder().decode(
                registroDTO.getImagen().getContenidoBase64()
            ));
            imagen.setCliente(cliente);
            imagenService.save(imagen);
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Cliente registrado exitosamente",
            "clienteId", cliente.getId(),
            "usuarioId", usuario.getId()
        ));
        
    } catch (Exception e) {
        log.error("Error en registro intermedio: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "success", false,
                "message", "Error al registrar el cliente: " + e.getMessage()
            ));
    }
}
```

---

## 5. Servicios Necesarios

### 5.1. UsuarioService

```java
public interface UsuarioService {
    Optional<Usuario> findByExternalId(String externalId);
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
}
```

### 5.2. ClienteService

Ya debería existir, asegurarse de que tenga:

```java
public interface ClienteService {
    Cliente save(Cliente cliente);
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
}
```

---

## 6. Testing

### 6.1. Test del endpoint check-user

```bash
curl -X POST http://localhost:8080/greedy_cars/api/auth0/check-user \
  -H "Content-Type: application/json" \
  -d '{
    "auth0Sub": "auth0|123456789",
    "email": "test@example.com"
  }'
```

### 6.2. Test del endpoint registro-intermedio

```bash
curl -X POST http://localhost:8080/greedy_cars/api/auth0/registro-intermedio \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <access_token>" \
  -d '{
    "auth0Sub": "auth0|123456789",
    "email": "test@example.com",
    "registroData": {
      "nombre": "Juan",
      "apellido": "Pérez",
      ...
    }
  }'
```

---

## 7. Notas Importantes

1. **Transaccionalidad**: El endpoint `registro-intermedio` debe ser `@Transactional` para garantizar que si falla alguna parte, se revierta todo.

2. **Validaciones**: Agregar validaciones para:
   - Documento único
   - Email único
   - Datos requeridos

3. **Seguridad**: El token JWT de Auth0 se valida automáticamente por Spring Security.

4. **Logs**: Agregar logs detallados para debugging.

5. **Manejo de errores**: Implementar manejo robusto de excepciones con mensajes claros.

---

**Fecha de creación**: Noviembre 2025  
**Versión**: 1.0
