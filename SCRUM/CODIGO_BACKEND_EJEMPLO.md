# 🔧 Código de Ejemplo para Adaptar el Backend

Este documento contiene el código exacto que necesitas crear/modificar en el proyecto **greedy_cars** (backend).

---

## 📝 PASO 1: Actualizar application.properties

**Archivo:** `greedy_cars/src/main/resources/application.properties`

```properties
spring.application.name=greedy_cars
spring.main.allow-bean-definition-overriding=true

# CAMBIAR PUERTO A 9000 (antes era 9000, ahora debe ser 9000 para el backend)
server.port=9000

# MariaDB (mantener igual)
spring.datasource.url=jdbc:mariadb://localhost:3306/cars_db?useUnicode=true&characterEncoding=utf8&serverTimezone=America/Argentina/Mendoza
spring.datasource.username=root
spring.datasource.password=adminAdmin
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA/Hibernate (mantener igual)
spring.jpa.generate-ddl=true
spring.jpa.show-sql=true
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Logging (mantener igual)
logging.level.org.springframework.security=DEBUG
server.error.include-message=always
```

---

## 📝 PASO 2: Crear VehiculoDTO

**Archivo:** `greedy_cars/src/main/java/com/uncuyo/greedy_cars/shared/template/dto/VehiculoDTO.java`

```java
package com.uncuyo.greedy_cars.shared.template.dto;

import java.time.LocalDateTime;

/**
 * DTO para transferir datos de Vehículo a través de la API REST
 */
public class VehiculoDTO {
    
    private String id;
    private String patente;
    private String estadoVehiculo;
    
    // Campos de auditoría
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Boolean eliminado;

    // Constructores
    public VehiculoDTO() {
    }

    public VehiculoDTO(String id, String patente, String estadoVehiculo) {
        this.id = id;
        this.patente = patente;
        this.estadoVehiculo = estadoVehiculo;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getEstadoVehiculo() {
        return estadoVehiculo;
    }

    public void setEstadoVehiculo(String estadoVehiculo) {
        this.estadoVehiculo = estadoVehiculo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}
```

---

## 📝 PASO 3: Crear VehiculoRestController

**Archivo:** `greedy_cars/src/main/java/com/uncuyo/greedy_cars/shared/template/controller/VehiculoRestController.java`

```java
package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.VehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoVehiculo;
import com.uncuyo.greedy_cars.shared.template.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RestController para exponer endpoints REST de Vehículo
 * Retorna JSON, no vistas HTML
 */
@RestController
@RequestMapping("/api/v1/vehiculos")
@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:9000"})
public class VehiculoRestController {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    /**
     * GET /api/v1/vehiculos
     * Listar todos los vehículos activos
     */
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> listarTodos() {
        try {
            List<Vehiculo> vehiculos = vehiculoRepository.findByEliminadoFalse();
            List<VehiculoDTO> dtos = vehiculos.stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
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
            Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);
            if (vehiculoOpt.isPresent() && !vehiculoOpt.get().getEliminado()) {
                return ResponseEntity.ok(convertirADTO(vehiculoOpt.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/vehiculos/patente/{patente}
     * Buscar vehículo por patente
     */
    @GetMapping("/patente/{patente}")
    public ResponseEntity<VehiculoDTO> buscarPorPatente(@PathVariable String patente) {
        try {
            Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findByPatenteAndEliminadoFalse(patente);
            if (vehiculoOpt.isPresent()) {
                return ResponseEntity.ok(convertirADTO(vehiculoOpt.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/vehiculos/estado/{estado}
     * Listar vehículos por estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VehiculoDTO>> listarPorEstado(@PathVariable String estado) {
        try {
            EstadoVehiculo estadoEnum = EstadoVehiculo.valueOf(estado.toUpperCase());
            List<Vehiculo> vehiculos = vehiculoRepository.findByEstadoVehiculoAndEliminadoFalse(estadoEnum);
            List<VehiculoDTO> dtos = vehiculos.stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
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
            // Validar que no exista la patente
            Optional<Vehiculo> existente = vehiculoRepository.findByPatenteAndEliminadoFalse(
                    vehiculoDTO.getPatente()
            );
            if (existente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Vehiculo vehiculo = convertirAEntidad(vehiculoDTO);
            vehiculo.setFechaCreacion(LocalDateTime.now());
            vehiculo.setEliminado(false);
            
            Vehiculo guardado = vehiculoRepository.save(vehiculo);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(guardado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /api/v1/vehiculos/{id}
     * Actualizar un vehículo existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> actualizar(
            @PathVariable String id, 
            @RequestBody VehiculoDTO vehiculoDTO) {
        try {
            Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);
            if (!vehiculoOpt.isPresent() || vehiculoOpt.get().getEliminado()) {
                return ResponseEntity.notFound().build();
            }

            Vehiculo vehiculo = vehiculoOpt.get();
            vehiculo.setPatente(vehiculoDTO.getPatente());
            vehiculo.setEstadoVehiculo(EstadoVehiculo.valueOf(vehiculoDTO.getEstadoVehiculo()));
            vehiculo.setFechaModificacion(LocalDateTime.now());

            Vehiculo actualizado = vehiculoRepository.save(vehiculo);
            return ResponseEntity.ok(convertirADTO(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * DELETE /api/v1/vehiculos/{id}
     * Eliminar (lógicamente) un vehículo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);
            if (!vehiculoOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Vehiculo vehiculo = vehiculoOpt.get();
            vehiculo.setEliminado(true);
            vehiculo.setFechaModificacion(LocalDateTime.now());
            vehiculoRepository.save(vehiculo);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== MÉTODOS DE CONVERSIÓN ==========

    /**
     * Convertir Entidad a DTO
     */
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

    /**
     * Convertir DTO a Entidad
     */
    private Vehiculo convertirAEntidad(VehiculoDTO dto) {
        Vehiculo entidad = new Vehiculo();
        if (dto.getId() != null) {
            entidad.setId(dto.getId());
        }
        entidad.setPatente(dto.getPatente());
        entidad.setEstadoVehiculo(EstadoVehiculo.valueOf(dto.getEstadoVehiculo()));
        return entidad;
    }
}
```

---

## 📝 PASO 4: Agregar métodos al VehiculoRepository

**Archivo:** `greedy_cars/src/main/java/com/uncuyo/greedy_cars/shared/template/repository/VehiculoRepository.java`

Si el archivo ya existe, agrega estos métodos:

```java
package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {
    
    // Buscar vehículos activos (no eliminados)
    List<Vehiculo> findByEliminadoFalse();
    
    // Buscar por patente (activos)
    Optional<Vehiculo> findByPatenteAndEliminadoFalse(String patente);
    
    // Buscar por estado (activos)
    List<Vehiculo> findByEstadoVehiculoAndEliminadoFalse(EstadoVehiculo estadoVehiculo);
}
```

---

## 📝 PASO 5: Configurar CORS

**Archivo:** `greedy_cars/src/main/java/com/uncuyo/greedy_cars/shared/template/config/CorsConfig.java`

```java
package com.uncuyo.greedy_cars.shared.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuración de CORS para permitir peticiones desde el frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir peticiones desde el frontend
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:9000");
        
        // Permitir todos los métodos HTTP (GET, POST, PUT, DELETE, etc.)
        config.addAllowedMethod("*");
        
        // Permitir todas las cabeceras
        config.addAllowedHeader("*");
        
        // Permitir credenciales
        config.setAllowCredentials(true);
        
        // Aplicar configuración a todos los endpoints /api/**
        source.registerCorsConfiguration("/api/**", config);
        
        return new CorsFilter(source);
    }
}
```

---

## 🧪 PASO 6: Probar los Endpoints

### Usando curl:

```bash
# 1. Listar todos los vehículos
curl http://localhost:9000/api/v1/vehiculos

# 2. Crear un nuevo vehículo
curl -X POST http://localhost:9000/api/v1/vehiculos \
  -H "Content-Type: application/json" \
  -d '{
    "patente": "ABC123",
    "estadoVehiculo": "DISPONIBLE"
  }'

# 3. Obtener un vehículo por ID (reemplaza {id} con el ID real)
curl http://localhost:9000/api/v1/vehiculos/{id}

# 4. Buscar por patente
curl http://localhost:9000/api/v1/vehiculos/patente/ABC123

# 5. Listar por estado
curl http://localhost:9000/api/v1/vehiculos/estado/DISPONIBLE

# 6. Actualizar un vehículo (reemplaza {id})
curl -X PUT http://localhost:9000/api/v1/vehiculos/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "patente": "XYZ789",
    "estadoVehiculo": "VENDIDO"
  }'

# 7. Eliminar un vehículo (reemplaza {id})
curl -X DELETE http://localhost:9000/api/v1/vehiculos/{id}
```

### Respuestas Esperadas:

**GET /api/v1/vehiculos** (Listar todos):
```json
[
  {
    "id": "uuid-123-456",
    "patente": "ABC123",
    "estadoVehiculo": "DISPONIBLE",
    "fechaCreacion": "2024-11-03T10:30:00",
    "fechaModificacion": null,
    "eliminado": false
  }
]
```

**POST /api/v1/vehiculos** (Crear):
```json
{
  "id": "uuid-nuevo",
  "patente": "ABC123",
  "estadoVehiculo": "DISPONIBLE",
  "fechaCreacion": "2024-11-03T12:00:00",
  "fechaModificacion": null,
  "eliminado": false
}
```

---

## 📋 Checklist de Implementación

### Backend (greedy_cars):
- [ ] Cambiar puerto a 9000 en `application.properties`
- [ ] Crear carpeta `dto/`
- [ ] Crear `VehiculoDTO.java`
- [ ] Crear `VehiculoRestController.java`
- [ ] Agregar métodos a `VehiculoRepository.java`
- [ ] Crear `CorsConfig.java`
- [ ] Compilar: `mvn clean compile`
- [ ] Ejecutar: `mvn spring-boot:run`
- [ ] Probar endpoints con curl
- [ ] Verificar que responda JSON

### Frontend (greedy_cars_web):
- [✅] Ya está todo creado y compilado
- [ ] Ejecutar: `mvn spring-boot:run`
- [ ] Abrir: http://localhost:8081
- [ ] Ir a: http://localhost:8081/vehiculos
- [ ] Probar CRUD completo

---

## 🎯 Resultado Esperado

Después de implementar estos cambios:

1. ✅ Backend API corriendo en puerto 9000
2. ✅ Frontend Web corriendo en puerto 8081
3. ✅ Frontend puede:
   - Listar vehículos
   - Ver detalle de un vehículo
   - Crear nuevos vehículos
   - Editar vehículos existentes
   - Eliminar vehículos
   - Buscar por patente
   - Filtrar por estado

---

## 📞 Verificación del Flujo

```
1. Usuario abre navegador → http://localhost:8081/vehiculos

2. Frontend hace petición → GET http://localhost:9000/api/v1/vehiculos

3. Backend responde con JSON:
   [
     {
       "id": "123",
       "patente": "ABC123",
       "estadoVehiculo": "DISPONIBLE"
     }
   ]

4. Frontend convierte JSON a List<VehiculoDTO>

5. Controller pasa datos al Model

6. Thymeleaf renderiza lista.html

7. Usuario ve la tabla de vehículos en HTML
```

---

## 🎉 ¡Todo Listo!

Una vez implementes estos archivos en el backend, tendrás la arquitectura de microservicios funcionando completamente para la entidad Vehículo.

Luego puedes replicar el mismo patrón para las demás entidades siguiendo el `EJEMPLO_IMPLEMENTACION_EMPRESA.md`.

---

¿Necesitas más ejemplos o ayuda? ¡Pregunta! 🚀
