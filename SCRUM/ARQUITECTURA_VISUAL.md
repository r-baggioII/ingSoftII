# 🏛️ Arquitectura del Sistema - Greedy Cars

## 📊 Diagrama General de la Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              USUARIO / NAVEGADOR                         │
│                        http://localhost:8081                             │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 │ HTTP Request (HTML)
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    GREEDY_CARS_WEB (Frontend)                            │
│                         Puerto: 8081                                     │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │  VehiculoController (@Controller)                                 │  │
│  │  - listar()                                                        │  │
│  │  - ver(id)                                                         │  │
│  │  - crear()                                                         │  │
│  └────────────────────────┬──────────────────────────────────────────┘  │
│                           │                                              │
│  ┌────────────────────────▼──────────────────────────────────────────┐  │
│  │  VehiculoService                                                   │  │
│  │  - listarTodos()                                                   │  │
│  │  - obtenerPorId(id)                                                │  │
│  │  - validaciones y lógica de presentación                           │  │
│  └────────────────────────┬──────────────────────────────────────────┘  │
│                           │                                              │
│  ┌────────────────────────▼──────────────────────────────────────────┐  │
│  │  VehiculoDAORest (extends BaseDAORest)                             │  │
│  │  - listarTodos() → RestTemplate.getForEntity()                     │  │
│  │  - crear(dto) → RestTemplate.postForEntity()                       │  │
│  └────────────────────────┬──────────────────────────────────────────┘  │
│                           │                                              │
│  ┌────────────────────────▼──────────────────────────────────────────┐  │
│  │  RestTemplate (Bean)                                               │  │
│  │  - Configurado con timeouts                                        │  │
│  │  - Hace peticiones HTTP al backend                                 │  │
│  └────────────────────────┬──────────────────────────────────────────┘  │
└───────────────────────────┼──────────────────────────────────────────────┘
                            │
                            │ HTTP/JSON
                            │ GET http://localhost:9000/api/v1/vehiculos
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      GREEDY_CARS (Backend API)                           │
│                         Puerto: 9000                                     │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │  VehiculoRestController (@RestController)                         │  │
│  │  @RequestMapping("/api/v1/vehiculos")                             │  │
│  │  - GET /      → listarTodos()                                      │  │
│  │  - GET /{id}  → obtenerPorId(id)                                   │  │
│  │  - POST /     → crear(dto)                                         │  │
│  │  - PUT /{id}  → actualizar(id, dto)                                │  │
│  │  - DELETE /{id} → eliminar(id)                                     │  │
│  └────────────────────────┬──────────────────────────────────────────┘  │
│                           │                                              │
│  ┌────────────────────────▼──────────────────────────────────────────┐  │
│  │  Métodos de Conversión                                             │  │
│  │  - convertirADTO(entidad) → VehiculoDTO                            │  │
│  │  - convertirAEntidad(dto) → Vehiculo                               │  │
│  └────────────────────────┬──────────────────────────────────────────┘  │
│                           │                                              │
│  ┌────────────────────────▼──────────────────────────────────────────┐  │
│  │  VehiculoRepository (JPA)                                          │  │
│  │  extends JpaRepository<Vehiculo, String>                           │  │
│  │  - findByEliminadoFalse()                                          │  │
│  │  - findByPatenteAndEliminadoFalse(patente)                         │  │
│  │  - save(vehiculo)                                                  │  │
│  └────────────────────────┬──────────────────────────────────────────┘  │
└───────────────────────────┼──────────────────────────────────────────────┘
                            │
                            │ SQL Queries
                            │
                            ▼
                    ┌──────────────────┐
                    │   MariaDB        │
                    │   cars_db        │
                    │                  │
                    │ Tabla: vehiculo  │
                    │ - id             │
                    │ - patente        │
                    │ - estado_vehiculo│
                    │ - eliminado      │
                    │ - fecha_creacion │
                    └──────────────────┘
```

---

## 🔄 Flujo de Datos Completo - Ejemplo: Listar Vehículos

### 1️⃣ Usuario accede a la URL

```
http://localhost:8081/vehiculos
```

### 2️⃣ VehiculoController (Frontend)

```java
@GetMapping
public String listar(Model model) {
    List<VehiculoDTO> vehiculos = vehiculoService.listarTodos();
    model.addAttribute("vehiculos", vehiculos);
    return "vehiculo/lista"; // → templates/vehiculo/lista.html
}
```

### 3️⃣ VehiculoServiceImpl (Frontend)

```java
@Override
public List<VehiculoDTO> listarTodos() {
    return vehiculoDAORest.listarTodos();
}
```

### 4️⃣ VehiculoDAORest (Frontend)

```java
public List<VehiculoDTO> listarTodos() {
    ResponseEntity<List<VehiculoDTO>> response = getList(
        "", 
        new ParameterizedTypeReference<List<VehiculoDTO>>() {}
    );
    return response.getBody();
}
```

### 5️⃣ BaseDAORest → RestTemplate (Frontend)

```java
protected ResponseEntity<List<T>> getList(...) {
    return restTemplate.exchange(
        "http://localhost:9000/api/v1/vehiculos", // ← URL completa
        HttpMethod.GET,
        null,
        responseType
    );
}
```

### 6️⃣ Petición HTTP

```
GET http://localhost:9000/api/v1/vehiculos
Accept: application/json
```

### 7️⃣ VehiculoRestController (Backend)

```java
@GetMapping
public ResponseEntity<List<VehiculoDTO>> listarTodos() {
    List<Vehiculo> vehiculos = vehiculoRepository.findByEliminadoFalse();
    List<VehiculoDTO> dtos = vehiculos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
```

### 8️⃣ VehiculoRepository (Backend)

```java
List<Vehiculo> findByEliminadoFalse();
// Genera SQL: SELECT * FROM vehiculo WHERE eliminado = false
```

### 9️⃣ Base de Datos MariaDB

```sql
SELECT id, patente, estado_vehiculo, eliminado, fecha_creacion, fecha_modificacion
FROM vehiculo
WHERE eliminado = false;
```

### 🔟 Respuesta JSON (Backend → Frontend)

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "patente": "ABC123",
    "estadoVehiculo": "DISPONIBLE",
    "fechaCreacion": "2024-11-03T10:30:00",
    "fechaModificacion": null,
    "eliminado": false
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "patente": "XYZ789",
    "estadoVehiculo": "RESERVADO",
    "fechaCreacion": "2024-11-03T11:00:00",
    "fechaModificacion": "2024-11-03T11:30:00",
    "eliminado": false
  }
]
```

### 1️⃣1️⃣ Conversión JSON → DTOs (Frontend)

RestTemplate automáticamente convierte el JSON a `List<VehiculoDTO>`

### 1️⃣2️⃣ Thymeleaf Renderiza HTML (Frontend)

```html
<tr th:each="vehiculo : ${vehiculos}">
    <td th:text="${vehiculo.id}">uuid</td>
    <td th:text="${vehiculo.patente}">ABC123</td>
    <td th:text="${vehiculo.estadoVehiculo}">DISPONIBLE</td>
</tr>
```

### 1️⃣3️⃣ Usuario ve la Página HTML

```
┌────────────────────────────────────────────┐
│ Lista de Vehículos                         │
├────────────────────────────────────────────┤
│ ID         | Patente | Estado             │
├────────────────────────────────────────────┤
│ 550e8400...│ ABC123  │ DISPONIBLE        │
│ 660e8400...│ XYZ789  │ RESERVADO         │
└────────────────────────────────────────────┘
```

---

## 📦 Estructura de DTOs

### VehiculoDTO (Backend y Frontend - DEBEN SER IDÉNTICOS)

```
VehiculoDTO
├── id: String
├── patente: String
├── estadoVehiculo: String
├── fechaCreacion: LocalDateTime
├── fechaModificacion: LocalDateTime
└── eliminado: Boolean
```

---

## 🔐 Configuración de CORS

### ¿Por qué es necesario?

```
Frontend (localhost:8081) → Backend (localhost:9000)
        ↑
    Cross-Origin Request
    (Diferentes puertos)
```

Sin CORS, el navegador bloquearía la petición.

### Solución en el Backend:

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        // ...
    }
}
```

---

## 📂 Estructura de Directorios

### Backend (greedy_cars):

```
greedy_cars/
└── src/main/java/com/uncuyo/greedy_cars/shared/template/
    ├── config/
    │   └── CorsConfig.java                   ← Configuración CORS
    │
    ├── controller/
    │   └── VehiculoRestController.java       ← @RestController (JSON)
    │
    ├── dto/
    │   └── VehiculoDTO.java                  ← Objeto de transferencia
    │
    ├── entity/
    │   └── Vehiculo.java                     ← Entidad JPA (existente)
    │
    ├── repository/
    │   └── VehiculoRepository.java           ← JPA Repository (existente)
    │
    └── enums/
        └── EstadoVehiculo.java               ← Enum (existente)
```

### Frontend (greedy_cars_web):

```
greedy_cars_web/
└── src/main/java/com/uncuyo/greedy_cars_web/
    ├── config/
    │   └── RestTemplateConfig.java           ← Configuración RestTemplate
    │
    ├── controller/
    │   └── VehiculoController.java           ← @Controller (HTML)
    │
    ├── service/
    │   ├── VehiculoService.java              ← Interface
    │   └── impl/
    │       └── VehiculoServiceImpl.java      ← Implementación
    │
    ├── rest/
    │   ├── BaseDAORest.java                  ← Clase base
    │   └── VehiculoDAORest.java              ← Consumidor de API
    │
    ├── dto/
    │   └── VehiculoDTO.java                  ← DTO (idéntico al backend)
    │
    └── exception/
        ├── ErrorServiceException.java
        └── ApiException.java
```

---

## 🚀 Endpoints REST Expuestos

### Backend API (http://localhost:9000):

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/vehiculos` | Listar todos los vehículos activos |
| GET | `/api/v1/vehiculos/{id}` | Obtener un vehículo por ID |
| GET | `/api/v1/vehiculos/patente/{patente}` | Buscar por patente |
| GET | `/api/v1/vehiculos/estado/{estado}` | Listar por estado |
| POST | `/api/v1/vehiculos` | Crear nuevo vehículo |
| PUT | `/api/v1/vehiculos/{id}` | Actualizar vehículo |
| DELETE | `/api/v1/vehiculos/{id}` | Eliminar vehículo |

---

## 🌐 URLs del Frontend

### Frontend Web (http://localhost:8081):

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/` | Página principal |
| GET | `/vehiculos` | Lista de vehículos |
| GET | `/vehiculos/ver/{id}` | Detalle de vehículo |
| GET | `/vehiculos/nuevo` | Formulario crear |
| POST | `/vehiculos/crear` | Procesar creación |
| GET | `/vehiculos/editar/{id}` | Formulario editar |
| POST | `/vehiculos/actualizar/{id}` | Procesar actualización |
| GET | `/vehiculos/eliminar/{id}` | Eliminar vehículo |
| GET | `/vehiculos/buscar?patente=` | Buscar por patente |

---

## 🎨 Vistas Thymeleaf

```
templates/
├── index.html                    ← Página principal
├── error.html                    ← Página de errores
└── vehiculo/
    ├── lista.html                ← Tabla de vehículos
    ├── detalle.html              ← Información detallada
    └── formulario.html           ← Crear/Editar
```

---

## 🔄 Comparación: Antes vs Después

### ANTES (Arquitectura Monolítica):

```
Usuario → Controller (@Controller)
            ↓
          Service
            ↓
          Repository
            ↓
          Base de Datos
```

Todo en un solo proyecto, un solo puerto.

### DESPUÉS (Arquitectura de Microservicios):

```
Usuario → Frontend Controller → Service → DAORest → RestTemplate
                                                        ↓
                                                  HTTP/JSON
                                                        ↓
         Backend RestController → Repository → Base de Datos
```

Dos proyectos separados, dos puertos, comunicación por HTTP/JSON.

---

## ✅ Ventajas de la Nueva Arquitectura

1. **Separación de Responsabilidades**
   - Frontend: Presentación
   - Backend: Lógica de negocio y datos

2. **Escalabilidad Independiente**
   - Puedes escalar frontend y backend por separado

3. **Reutilización de la API**
   - La misma API puede ser consumida por:
     - Aplicación web
     - Aplicación móvil
     - SPA (React, Angular, Vue)
     - Otros servicios

4. **Testing Más Fácil**
   - Puedes probar la API independientemente con curl/Postman
   - Puedes probar el frontend con APIs mock

5. **Despliegue Independiente**
   - Actualizar frontend sin tocar backend
   - Actualizar backend sin tocar frontend

6. **Tecnologías Diferentes**
   - Podrías reemplazar el frontend con React sin tocar el backend
   - Podrías cambiar la base de datos sin tocar el frontend

---

## 📊 Métricas del Proyecto

### Frontend (greedy_cars_web):
- **Archivos Java:** 11
- **Vistas HTML:** 5
- **Tecnologías:** Spring MVC, Thymeleaf, RestTemplate, Bootstrap
- **Puerto:** 8081

### Backend (greedy_cars):
- **Archivos a crear:** 3 (DTO, RestController, CorsConfig)
- **Archivos a modificar:** 2 (application.properties, Repository)
- **Tecnologías:** Spring REST, JPA, MariaDB
- **Puerto:** 9000

---

¡Arquitectura completa y lista para usar! 🚀
