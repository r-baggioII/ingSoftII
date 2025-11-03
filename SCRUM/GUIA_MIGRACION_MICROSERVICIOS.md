# 🔄 Guía de Migración a Arquitectura de Microservicios

## 📋 Resumen de la Arquitectura

Has creado exitosamente la estructura base para migrar de una arquitectura MVC monolítica a una arquitectura de microservicios con dos proyectos:

### 1. **greedy_cars** (Backend API - Puerto 9000)
- **Responsabilidad:** API REST que expone endpoints JSON
- **Ubicación:** `/home/rocio/Documentos/GitHub/ingSoftII/SCRUM/greedy_cars`
- **Puerto:** 9000
- **Tecnologías:** Spring Boot, JPA, MariaDB

### 2. **greedy_cars_web** (Frontend Client - Puerto 8081) ✅ YA CREADO
- **Responsabilidad:** Aplicación web MVC que consume la API
- **Ubicación:** `/home/rocio/Documentos/GitHub/ingSoftII/SCRUM/greedy_cars_web`
- **Puerto:** 8081
- **Tecnologías:** Spring Boot, Thymeleaf, RestTemplate

---

## 🎯 Próximos Pasos

### PASO 1: Configurar el Backend (greedy_cars) como API REST

El proyecto actual `greedy_cars` necesita ser transformado para exponer endpoints REST en lugar de vistas HTML.

#### 1.1. Actualizar application.properties

```properties
# NUEVO puerto para el backend
server.port=9000

# Resto de la configuración permanece igual
spring.application.name=greedy_cars_api
# ... (mantener configuración de base de datos)
```

#### 1.2. Crear DTOs (Data Transfer Objects)

Los DTOs son objetos simplificados para transferir datos en JSON. Debes crear uno por cada entidad.

**Ubicación:** `src/main/java/com/uncuyo/greedy_cars/shared/template/dto/`

**Ejemplo: VehiculoDTO.java**
```java
package com.uncuyo.greedy_cars.shared.template.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehiculoDTO {
    private String id;
    private String patente;
    private String estadoVehiculo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Boolean eliminado;
}
```

#### 1.3. Crear RestControllers

Los `@RestController` reemplazan a los `@Controller` tradicionales. Retornan JSON en lugar de vistas HTML.

**Ubicación:** `src/main/java/com/uncuyo/greedy_cars/shared/template/controller/`

**Ejemplo: VehiculoRestController.java**
```java
package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.VehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@CrossOrigin(origins = "http://localhost:8081") // Permitir peticiones del frontend
public class VehiculoRestController {

    @Autowired
    private VehiculoService vehiculoService;

    /**
     * GET /api/v1/vehiculos
     * Listar todos los vehículos
     */
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> listarTodos() {
        try {
            List<VehiculoDTO> vehiculos = vehiculoService.listarTodos();
            return ResponseEntity.ok(vehiculos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/vehiculos/{id}
     * Obtener un vehículo por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDTO> obtenerPorId(@PathVariable String id) {
        try {
            VehiculoDTO vehiculo = vehiculoService.obtenerPorId(id);
            return ResponseEntity.ok(vehiculo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/v1/vehiculos/patente/{patente}
     * Buscar por patente
     */
    @GetMapping("/patente/{patente}")
    public ResponseEntity<VehiculoDTO> buscarPorPatente(@PathVariable String patente) {
        try {
            VehiculoDTO vehiculo = vehiculoService.buscarPorPatente(patente);
            return ResponseEntity.ok(vehiculo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/v1/vehiculos/estado/{estado}
     * Listar por estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VehiculoDTO>> listarPorEstado(@PathVariable String estado) {
        try {
            List<VehiculoDTO> vehiculos = vehiculoService.listarPorEstado(estado);
            return ResponseEntity.ok(vehiculos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/v1/vehiculos
     * Crear un nuevo vehículo
     */
    @PostMapping
    public ResponseEntity<VehiculoDTO> crear(@RequestBody VehiculoDTO vehiculoDTO) {
        try {
            VehiculoDTO vehiculoCreado = vehiculoService.crear(vehiculoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /api/v1/vehiculos/{id}
     * Actualizar un vehículo
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> actualizar(
            @PathVariable String id, 
            @RequestBody VehiculoDTO vehiculoDTO) {
        try {
            VehiculoDTO vehiculoActualizado = vehiculoService.actualizar(id, vehiculoDTO);
            return ResponseEntity.ok(vehiculoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * DELETE /api/v1/vehiculos/{id}
     * Eliminar un vehículo (lógicamente)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            vehiculoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

#### 1.4. Adaptar Services

Los servicios deben retornar DTOs en lugar de entidades. Necesitas crear métodos de conversión.

**Ejemplo: Método de conversión en VehiculoServiceImpl**
```java
private VehiculoDTO convertirADTO(Vehiculo entidad) {
    VehiculoDTO dto = new VehiculoDTO();
    dto.setId(entidad.getId());
    dto.setPatente(entidad.getPatente());
    dto.setEstadoVehiculo(entidad.getEstadoVehiculo().name());
    dto.setFechaCreacion(entidad.getFechaCreacion());
    dto.setFechaModificacion(entidad.getFechaModificacion());
    dto.setEliminado(entidad.getEliminado());
    return dto;
}

private Vehiculo convertirAEntidad(VehiculoDTO dto) {
    Vehiculo entidad = new Vehiculo();
    if (dto.getId() != null) {
        entidad.setId(dto.getId());
    }
    entidad.setPatente(dto.getPatente());
    entidad.setEstadoVehiculo(EstadoVehiculo.valueOf(dto.getEstadoVehiculo()));
    return entidad;
}

// Ejemplo de método del servicio
public List<VehiculoDTO> listarTodos() {
    List<Vehiculo> entidades = vehiculoRepository.findByEliminadoFalse();
    return entidades.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
}
```

#### 1.5. Configurar CORS

Permite que el frontend (puerto 8081) pueda llamar al backend (puerto 9000).

**Crear archivo:** `src/main/java/com/uncuyo/greedy_cars/shared/template/config/CorsConfig.java`

```java
package com.uncuyo.greedy_cars.shared.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir peticiones desde el frontend
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:9000");
        
        // Permitir todos los métodos HTTP
        config.addAllowedMethod("*");
        
        // Permitir todas las cabeceras
        config.addAllowedHeader("*");
        
        // Permitir credenciales
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
```

---

### PASO 2: Probar el Backend API

#### 2.1. Ejecutar el backend

```bash
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/greedy_cars
mvn clean compile
mvn spring-boot:run
```

El backend debería estar corriendo en: `http://localhost:9000`

#### 2.2. Probar endpoints con curl o Postman

```bash
# Listar todos los vehículos
curl http://localhost:9000/api/v1/vehiculos

# Obtener un vehículo por ID
curl http://localhost:9000/api/v1/vehiculos/{id}

# Crear un nuevo vehículo
curl -X POST http://localhost:9000/api/v1/vehiculos \
  -H "Content-Type: application/json" \
  -d '{"patente":"ABC123","estadoVehiculo":"DISPONIBLE"}'
```

---

### PASO 3: Ejecutar el Frontend

Una vez que el backend esté funcionando:

```bash
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/greedy_cars_web
mvn clean compile
mvn spring-boot:run
```

El frontend estará disponible en: `http://localhost:8081`

---

## 🔍 Verificación del Flujo Completo

1. **Usuario accede a:** `http://localhost:8081/vehiculos`
2. **VehiculoController (Web)** llama a `vehiculoService.listarTodos()`
3. **VehiculoServiceImpl (Web)** llama a `vehiculoDAORest.listarTodos()`
4. **VehiculoDAORest** hace petición HTTP GET a `http://localhost:9000/api/v1/vehiculos`
5. **VehiculoRestController (API)** recibe la petición
6. **VehiculoService (API)** consulta la base de datos
7. **VehiculoRepository** ejecuta query en MariaDB
8. **Respuesta JSON** viaja de vuelta hasta el frontend
9. **Thymeleaf** renderiza la vista HTML con los datos
10. **Usuario** ve la página HTML

---

## 📝 Checklist de Migración

### Backend (greedy_cars):
- [ ] Cambiar puerto a 9000 en `application.properties`
- [ ] Crear DTOs para cada entidad
- [ ] Crear RestControllers con endpoints `/api/v1/...`
- [ ] Adaptar Services para retornar DTOs
- [ ] Configurar CORS
- [ ] Probar endpoints con curl/Postman
- [ ] Verificar que la API responda JSON correctamente

### Frontend (greedy_cars_web):
- [✅] Proyecto creado con estructura base
- [✅] RestTemplate configurado
- [✅] Ejemplo completo de Vehículos implementado
- [ ] Adaptar para tus otras entidades (Empresa, Persona, etc.)
- [ ] Probar conexión con el backend

---

## 🚀 Comandos Rápidos

### Terminal 1 - Backend
```bash
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/greedy_cars
mvn spring-boot:run
# Debería estar en http://localhost:9000
```

### Terminal 2 - Frontend
```bash
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/greedy_cars_web
mvn spring-boot:run
# Debería estar en http://localhost:8081
```

---

## 🐛 Troubleshooting Común

### 1. Error "Connection refused" en el frontend
**Solución:** Asegúrate de que el backend esté corriendo en el puerto 9000

### 2. Error de CORS
**Solución:** Verifica que la configuración CORS esté correcta en el backend

### 3. Error 404 en endpoints
**Solución:** Verifica que las rutas en DAORest coincidan con las del RestController
- Frontend llama: `GET /api/v1/vehiculos`
- Backend debe tener: `@GetMapping` en `/api/v1/vehiculos`

### 4. Error de deserialización JSON
**Solución:** Asegúrate de que los DTOs del frontend y backend tengan los mismos campos

### 5. Timeout en peticiones
**Solución:** Aumenta los timeouts en `application.properties` del frontend:
```properties
api.connection.timeout=10000
api.read.timeout=10000
```

---

## 📚 Recursos Adicionales

- **Spring RestTemplate:** https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html
- **Spring REST Controllers:** https://spring.io/guides/gs/rest-service/
- **Thymeleaf:** https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html

---

## 🎓 Próximos Pasos Recomendados

1. **Completar la migración del backend** siguiendo esta guía
2. **Probar el ejemplo de Vehículos** que ya está implementado
3. **Replicar el patrón** para las demás entidades (Empresa, Persona, etc.)
4. **Agregar seguridad** (JWT, OAuth2) si es necesario
5. **Agregar manejo de errores global** con `@ControllerAdvice`
6. **Agregar logging** para debugging
7. **Documentar la API** con Swagger/OpenAPI

---

¡Éxito con la refactorización! 🚀
