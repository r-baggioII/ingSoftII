# Greedy Cars Web - Frontend Client

Este es el proyecto **frontend/web consumer** de la arquitectura de microservicios de Greedy Cars. Consume la API REST expuesta por el proyecto `greedy_cars` (backend).

## 🏗️ Arquitectura

```
Usuario → greedy_cars_web (Puerto 8081) → greedy_cars API (Puerto 9000) → Base de Datos
          [Frontend MVC]                    [Backend REST API]
```

### Responsabilidades de este proyecto:

- ✅ Controladores MVC (`@Controller`)
- ✅ Renderización de vistas HTML con Thymeleaf
- ✅ Validación de formularios
- ✅ Consumo de API REST mediante `RestTemplate`
- ✅ Manejo de sesión y navegación
- ❌ NO accede directamente a la base de datos
- ❌ NO contiene lógica de negocio

## 🚀 Cómo ejecutar

### Prerrequisitos
- Java 17
- Maven 3.6+
- El proyecto `greedy_cars` (backend) debe estar corriendo en el puerto 9000

### Pasos

1. **Asegúrate de que el backend esté corriendo:**
   ```bash
   cd /ruta/a/greedy_cars
   mvn spring-boot:run
   # Debería estar corriendo en http://localhost:9000
   ```

2. **Ejecutar este proyecto (frontend):**
   ```bash
   cd /ruta/a/greedy_cars_web
   mvn clean install
   mvn spring-boot:run
   # Estará corriendo en http://localhost:8081
   ```

3. **Acceder a la aplicación:**
   - Abre tu navegador en: `http://localhost:8081`

## 📁 Estructura del Proyecto

```
greedy_cars_web/
├── src/
│   ├── main/
│   │   ├── java/com/uncuyo/greedy_cars_web/
│   │   │   ├── GreedyCarsWebApplication.java
│   │   │   ├── config/
│   │   │   │   └── RestTemplateConfig.java          # Configuración de RestTemplate
│   │   │   ├── controller/
│   │   │   │   └── HomeController.java              # Controladores MVC
│   │   │   ├── service/
│   │   │   │   └── [ServiciosDeNegocioDeVista]     # Servicios del frontend
│   │   │   ├── rest/
│   │   │   │   └── BaseDAORest.java                 # Clase base para consumir APIs
│   │   │   ├── dto/
│   │   │   │   └── [DTOs]                           # Objetos de transferencia
│   │   │   └── exception/
│   │   │       ├── ErrorServiceException.java
│   │   │       └── ApiException.java
│   │   └── resources/
│   │       ├── application.properties                # Configuración
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       └── templates/                            # Vistas Thymeleaf
│   │           └── index.html
│   └── test/
└── pom.xml
```

## 🔧 Configuración

### application.properties

```properties
# Puerto del frontend
server.port=8081

# URL del backend API
api.base.url=http://localhost:9000

# Timeouts
api.connection.timeout=5000
api.read.timeout=5000
```

## 📝 Cómo crear un nuevo módulo (Ejemplo: Vehículos)

### 1. Crear DTO
```java
// VehiculoDTO.java
package com.uncuyo.greedy_cars_web.dto;

import lombok.Data;

@Data
public class VehiculoDTO {
    private String id;
    private String marca;
    private String modelo;
    // ... otros campos
}
```

### 2. Crear DAO REST
```java
// VehiculoDAORest.java
package com.uncuyo.greedy_cars_web.rest;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class VehiculoDAORest extends BaseDAORest<VehiculoDTO> {
    
    @Override
    protected String getResourcePath() {
        return "/api/v1/vehiculos";
    }
    
    public List<VehiculoDTO> listarTodos() {
        return getList("", new ParameterizedTypeReference<List<VehiculoDTO>>() {})
                .getBody();
    }
    
    public VehiculoDTO obtenerPorId(String id) {
        return getOne(id, VehiculoDTO.class).getBody();
    }
    
    public VehiculoDTO crear(VehiculoDTO dto) {
        return post(dto, VehiculoDTO.class).getBody();
    }
}
```

### 3. Crear Service
```java
// VehiculoService.java
package com.uncuyo.greedy_cars_web.service;

import com.uncuyo.greedy_cars_web.dto.VehiculoDTO;
import java.util.List;

public interface VehiculoService {
    List<VehiculoDTO> listarTodos();
    VehiculoDTO obtenerPorId(String id);
    VehiculoDTO crear(VehiculoDTO dto);
}
```

### 4. Crear Controller
```java
// VehiculoController.java
package com.uncuyo.greedy_cars_web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {
    
    private final VehiculoService vehiculoService;
    
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }
    
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vehiculos", vehiculoService.listarTodos());
        return "vehiculo/lista";
    }
    
    @GetMapping("/{id}")
    public String ver(@PathVariable String id, Model model) {
        model.addAttribute("vehiculo", vehiculoService.obtenerPorId(id));
        return "vehiculo/detalle";
    }
}
```

### 5. Crear Vista HTML
```html
<!-- templates/vehiculo/lista.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Lista de Vehículos</title>
</head>
<body>
    <h1>Vehículos</h1>
    <table>
        <tr th:each="vehiculo : ${vehiculos}">
            <td th:text="${vehiculo.marca}"></td>
            <td th:text="${vehiculo.modelo}"></td>
        </tr>
    </table>
</body>
</html>
```

## 🔄 Flujo de una petición

```
1. Usuario → http://localhost:8081/vehiculos
2. VehiculoController.listar()
3. VehiculoService.listarTodos()
4. VehiculoDAORest.listarTodos()
5. RestTemplate → GET http://localhost:9000/api/v1/vehiculos
6. Backend API responde con JSON
7. JSON se convierte a List<VehiculoDTO>
8. Controller agrega al Model
9. Thymeleaf renderiza lista.html
10. Usuario ve HTML en el navegador
```

## 🛠️ Comandos útiles

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Empaquetar
mvn clean package

# Saltar tests al empaquetar
mvn clean package -DskipTests
```

## 📚 Tecnologías utilizadas

- **Spring Boot 3.5.7** - Framework base
- **Spring Web MVC** - Para controladores y navegación
- **Thymeleaf** - Motor de plantillas para vistas
- **RestTemplate** - Cliente HTTP para consumir APIs
- **Lombok** - Reducción de código boilerplate
- **Bootstrap 5** - Framework CSS
- **Maven** - Gestión de dependencias

## ⚠️ Notas importantes

1. **Este proyecto NO debe tener dependencias de JPA** - No accede a la base de datos
2. **Siempre debe consumir la API** - Nunca hacer lógica de negocio aquí
3. **El backend debe estar corriendo primero** - Sin él, este proyecto no funciona
4. **Usa puertos diferentes** - Frontend: 8081, Backend: 9000

## 🐛 Troubleshooting

### Error: Connection refused
- ✅ Verifica que el backend esté corriendo en el puerto 9000
- ✅ Revisa la URL en `application.properties`: `api.base.url`

### Error: Timeout
- ✅ Aumenta los timeouts en `application.properties`
- ✅ Verifica que el backend responda correctamente

### No se ven los estilos CSS
- ✅ Los archivos estáticos deben estar en `src/main/resources/static/`
- ✅ Usa `th:href="@{/css/style.css}"` en Thymeleaf
