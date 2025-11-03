# 🏗️ Ejemplo Completo: Implementación de CRUD para Empresa

Este documento te muestra cómo replicar el patrón de Vehículos para la entidad **Empresa**.

---

## 📋 PASO 1: Backend (greedy_cars) - API REST

### 1.1. Crear EmpresaDTO

**Archivo:** `greedy_cars/src/main/java/com/uncuyo/greedy_cars/shared/template/dto/EmpresaDTO.java`

```java
package com.uncuyo.greedy_cars.shared.template.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmpresaDTO {
    private String id;
    private String nombreFantasia;
    private String razonSocial;
    private String cuit;
    private String email;
    private String telefono;
    
    // Campos de auditoría
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Boolean eliminado;
}
```

### 1.2. Crear EmpresaRestController

**Archivo:** `greedy_cars/src/main/java/com/uncuyo/greedy_cars/shared/template/controller/EmpresaRestController.java`

```java
package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.EmpresaDTO;
import com.uncuyo.greedy_cars.shared.template.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
@CrossOrigin(origins = "http://localhost:8081")
public class EmpresaRestController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> listarTodas() {
        try {
            List<EmpresaDTO> empresas = empresaService.listarTodas();
            return ResponseEntity.ok(empresas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> obtenerPorId(@PathVariable String id) {
        try {
            EmpresaDTO empresa = empresaService.obtenerPorId(id);
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cuit/{cuit}")
    public ResponseEntity<EmpresaDTO> buscarPorCuit(@PathVariable String cuit) {
        try {
            EmpresaDTO empresa = empresaService.buscarPorCuit(cuit);
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EmpresaDTO> crear(@RequestBody EmpresaDTO empresaDTO) {
        try {
            EmpresaDTO empresaCreada = empresaService.crear(empresaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(empresaCreada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> actualizar(
            @PathVariable String id, 
            @RequestBody EmpresaDTO empresaDTO) {
        try {
            EmpresaDTO empresaActualizada = empresaService.actualizar(id, empresaDTO);
            return ResponseEntity.ok(empresaActualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            empresaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

### 1.3. Adaptar EmpresaService

Necesitas agregar métodos que retornen DTOs:

```java
public interface EmpresaService {
    List<EmpresaDTO> listarTodas();
    EmpresaDTO obtenerPorId(String id);
    EmpresaDTO buscarPorCuit(String cuit);
    EmpresaDTO crear(EmpresaDTO empresaDTO);
    EmpresaDTO actualizar(String id, EmpresaDTO empresaDTO);
    void eliminar(String id);
}
```

En la implementación, agrega métodos de conversión:

```java
private EmpresaDTO convertirADTO(Empresa entidad) {
    EmpresaDTO dto = new EmpresaDTO();
    dto.setId(entidad.getId());
    dto.setNombreFantasia(entidad.getNombreFantasia());
    dto.setRazonSocial(entidad.getRazonSocial());
    dto.setCuit(entidad.getCuit());
    // ... otros campos
    return dto;
}

private Empresa convertirAEntidad(EmpresaDTO dto) {
    Empresa entidad = new Empresa();
    if (dto.getId() != null) {
        entidad.setId(dto.getId());
    }
    entidad.setNombreFantasia(dto.getNombreFantasia());
    entidad.setRazonSocial(dto.getRazonSocial());
    entidad.setCuit(dto.getCuit());
    // ... otros campos
    return entidad;
}
```

---

## 📋 PASO 2: Frontend (greedy_cars_web) - Consumidor

### 2.1. Crear EmpresaDTO

**Archivo:** `greedy_cars_web/src/main/java/com/uncuyo/greedy_cars_web/dto/EmpresaDTO.java`

```java
package com.uncuyo.greedy_cars_web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {
    private String id;
    private String nombreFantasia;
    private String razonSocial;
    private String cuit;
    private String email;
    private String telefono;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Boolean eliminado;
}
```

### 2.2. Crear EmpresaDAORest

**Archivo:** `greedy_cars_web/src/main/java/com/uncuyo/greedy_cars_web/rest/EmpresaDAORest.java`

```java
package com.uncuyo.greedy_cars_web.rest;

import com.uncuyo.greedy_cars_web.dto.EmpresaDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class EmpresaDAORest extends BaseDAORest<EmpresaDTO> {

    @Override
    protected String getResourcePath() {
        return "/api/v1/empresas";
    }

    public List<EmpresaDTO> listarTodas() {
        ResponseEntity<List<EmpresaDTO>> response = getList(
                "", 
                new ParameterizedTypeReference<List<EmpresaDTO>>() {}
        );
        return response.getBody();
    }

    public EmpresaDTO obtenerPorId(String id) {
        ResponseEntity<EmpresaDTO> response = getOne(id, EmpresaDTO.class);
        return response.getBody();
    }

    public EmpresaDTO buscarPorCuit(String cuit) {
        try {
            String url = getFullUrl("/cuit/" + cuit);
            ResponseEntity<EmpresaDTO> response = restTemplate.getForEntity(url, EmpresaDTO.class);
            return response.getBody();
        } catch (Exception e) {
            throw new com.uncuyo.greedy_cars_web.exception.ApiException(
                "Error al buscar empresa por CUIT: " + e.getMessage(), e
            );
        }
    }

    public EmpresaDTO crear(EmpresaDTO empresaDTO) {
        ResponseEntity<EmpresaDTO> response = post(empresaDTO, EmpresaDTO.class);
        return response.getBody();
    }

    public EmpresaDTO actualizar(String id, EmpresaDTO empresaDTO) {
        ResponseEntity<EmpresaDTO> response = put(id, empresaDTO, EmpresaDTO.class);
        return response.getBody();
    }

    public void eliminar(String id) {
        delete(id);
    }
}
```

### 2.3. Crear EmpresaService

**Interfaces y implementación similar a VehiculoService...**

### 2.4. Crear EmpresaController

**Archivo:** `greedy_cars_web/src/main/java/com/uncuyo/greedy_cars_web/controller/EmpresaController.java`

```java
package com.uncuyo.greedy_cars_web.controller;

import com.uncuyo.greedy_cars_web.dto.EmpresaDTO;
import com.uncuyo.greedy_cars_web.exception.ErrorServiceException;
import com.uncuyo.greedy_cars_web.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("empresas", empresaService.listarTodas());
            model.addAttribute("titulo", "Lista de Empresas");
            return "empresa/lista";
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable String id, Model model) {
        try {
            model.addAttribute("empresa", empresaService.obtenerPorId(id));
            return "empresa/detalle";
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("empresa", new EmpresaDTO());
        model.addAttribute("accion", "crear");
        return "empresa/formulario";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute EmpresaDTO empresaDTO, 
                       RedirectAttributes redirectAttributes) {
        try {
            empresaService.crear(empresaDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Empresa creada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/empresas";
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/empresas/nuevo";
        }
    }

    // ... resto de los métodos similares a VehiculoController
}
```

### 2.5. Crear vistas HTML

Crea las vistas en `greedy_cars_web/src/main/resources/templates/empresa/`:

- `lista.html` - Lista de empresas (similar a vehiculo/lista.html)
- `detalle.html` - Detalle de empresa
- `formulario.html` - Formulario de creación/edición

---

## 🔄 Patrón a Seguir para Otras Entidades

Para cada entidad (Persona, Dirección, Contacto, etc.):

### Backend (greedy_cars):
1. Crear `[Entidad]DTO.java`
2. Crear `[Entidad]RestController.java` con endpoints `/api/v1/[entidades]`
3. Adaptar `[Entidad]Service` para retornar DTOs
4. Agregar métodos de conversión Entity ↔ DTO

### Frontend (greedy_cars_web):
1. Crear `[Entidad]DTO.java` (igual al del backend)
2. Crear `[Entidad]DAORest.java` extendiendo `BaseDAORest`
3. Crear `[Entidad]Service.java` e implementación
4. Crear `[Entidad]Controller.java` con métodos MVC
5. Crear vistas HTML en `templates/[entidad]/`:
   - `lista.html`
   - `detalle.html`
   - `formulario.html`

---

## ✅ Checklist por Entidad

- [ ] Backend: DTO creado
- [ ] Backend: RestController creado
- [ ] Backend: Service adaptado
- [ ] Backend: Endpoints probados con curl/Postman
- [ ] Frontend: DTO creado
- [ ] Frontend: DAORest creado
- [ ] Frontend: Service creado
- [ ] Frontend: Controller creado
- [ ] Frontend: Vistas HTML creadas
- [ ] Frontend: Probado en navegador

---

## 🎯 Entidades a Migrar (según tu proyecto)

Basándome en los archivos que vi, deberías migrar:

1. ✅ **Vehiculo** (Ya tiene ejemplo completo)
2. **Empresa**
3. **Persona**
4. **Contacto**
5. **ContactoTelefonico**
6. **ContactoCorreoElectronico**
7. **Direccion**
8. **Localidad**
9. **Departamento**
10. **Provincia**
11. **Pais**

---

Sigue este patrón y tendrás toda tu aplicación migrada a la arquitectura de microservicios! 🚀
