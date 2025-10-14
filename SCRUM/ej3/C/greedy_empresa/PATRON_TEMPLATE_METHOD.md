# Patrón Template Method - Greedy Empresa

## Descripción

Este documento describe la implementación del **Patrón de Diseño Template Method** en el proyecto Greedy Empresa. Este patrón permite definir el esqueleto de un algoritmo en una clase base, dejando que las subclases redefinan ciertos pasos del algoritmo sin cambiar su estructura.

El patrón se ha aplicado en **dos capas principales**:
1. **Capa de Servicios** (`BaseService`)
2. **Capa de Controladores** (`BaseController`)

---

## CAPA DE SERVICIOS

### Clase Base: `BaseService<T, REPOSITORY>`

La clase abstracta `BaseService` define el **Template Method** para operaciones CRUD comunes:

```
BaseService (Clase Abstracta)
│
├── Template Methods (definen el flujo)
│   ├── buscar(filtro, pageable)
│   ├── buscarPorId(id)
│   ├── guardar(entidad) ← Principal Template Method
│   └── eliminar(id)
│
├── Hook Methods (pueden sobrescribirse)
│   ├── buscarConFiltro()
│   ├── validarEntidad()
│   ├── normalizarDatos()
│   ├── validarUnicidad()
│   ├── procesarRelaciones()
│   ├── actualizarEntidad()
│   ├── crearNuevaEntidad()
│   └── antesDeEliminar()
│
└── Métodos Abstractos (deben implementarse)
    ├── actualizarCampos()
    ├── getEntityClass()
    └── getEntityName()
```

### Flujo del Template Method `guardar()`

El método `guardar()` define el siguiente algoritmo:

```
1. validarEntidad(entidad)      → Hook Method
2. normalizarDatos(entidad)     → Hook Method
3. validarUnicidad(entidad)     → Hook Method
4. procesarRelaciones(entidad)  → Hook Method
5. SI tiene ID:
   └── actualizarEntidad()      → Hook Method
       └── actualizarCampos()   → Abstract Method
   SINO:
   └── crearNuevaEntidad()      → Hook Method
```

## Implementaciones Concretas

### 1. EmpresaService

**Características específicas:**
- Valida que la razón social sea obligatoria
- Normaliza la razón social (trim)
- Valida unicidad por razón social
- Procesa las direcciones asociadas

**Hooks sobrescritos:**
```java
- buscarConFiltro()    → Busca por razón social
- validarEntidad()     → Valida razón social obligatoria
- normalizarDatos()    → Normaliza razón social
- validarUnicidad()    → Verifica unicidad de razón social
- procesarRelaciones() → Configura direcciones
- actualizarCampos()   → Actualiza razón social y direcciones
```

### 2. ProveedorService

**Características específicas:**
- Valida que el CUIT sea obligatorio
- Normaliza el CUIT (trim)
- Valida unicidad por CUIT
- Procesa persona (ProveedorPersona) y direcciones asociadas

**Hooks sobrescritos:**
```java
- buscarConFiltro()     → Busca por CUIT
- validarEntidad()      → Valida CUIT y persona obligatorios
- normalizarDatos()     → Normaliza CUIT
- validarUnicidad()     → Verifica unicidad de CUIT
- procesarRelaciones()  → Configura persona y direcciones
- actualizarCampos()    → Actualiza CUIT, persona y direcciones
- crearNuevaEntidad()   → Marca persona como no eliminada
```

### 3. UsuarioService

**Características específicas:**
- Valida que username y password sean obligatorios
- Normaliza el username (trim)
- Valida unicidad por username
- Encripta la contraseña usando PasswordService
- Procesa persona (UsuarioPersona)
- Valida confirmación de contraseña

**Hooks sobrescritos:**
```java
- buscarConFiltro()     → Busca por username
- validarEntidad()      → Valida username/password obligatorios
- normalizarDatos()     → Normaliza username
- validarUnicidad()     → Verifica unicidad de username
- procesarRelaciones()  → Configura persona
- actualizarCampos()    → Actualiza username, rol, persona, password
- crearNuevaEntidad()   → Encripta password y marca persona
```

## Beneficios de la Implementación

### 1. Reutilización de Código
- Toda la lógica común de CRUD está centralizada en `BaseService`
- Evita duplicación de código entre servicios

### 2. Consistencia
- Todos los servicios siguen el mismo flujo de operaciones
- Facilita el mantenimiento y la comprensión del código

### 3. Extensibilidad
- Fácil agregar nuevos servicios heredando de `BaseService`
- Los hooks permiten personalizar comportamiento sin modificar la estructura

### 4. Separación de Responsabilidades
- Cada hook tiene una responsabilidad clara y específica
- Facilita testing unitario de cada paso del algoritmo

### 5. Principio Abierto/Cerrado (OCP)
- `BaseService` está **cerrada para modificación** pero **abierta para extensión**
- Las subclases extienden funcionalidad sin modificar la clase base

## Ejemplo de Uso

```java
@Service
public class EmpresaService extends BaseService<Empresa, EmpresaRepository> {
    
    // Constructor
    public EmpresaService(EmpresaRepository empresaRepository) {
        super(empresaRepository);
    }
    
    // Implementar métodos abstractos
    @Override
    public Class<Empresa> getEntityClass() {
        return Empresa.class;
    }
    
    @Override
    protected String getEntityName() {
        return "Empresa";
    }
    
    @Override
    protected void actualizarCampos(Empresa existente, Empresa nueva) {
        existente.setRazonSocial(nueva.getRazonSocial());
        existente.getDirecciones().clear();
        if (nueva.getDirecciones() != null) {
            existente.getDirecciones().addAll(nueva.getDirecciones());
        }
    }
    
    // Sobrescribir hooks opcionales
    @Override
    protected void validarEntidad(Empresa empresa) {
        super.validarEntidad(empresa);
        if (empresa.getRazonSocial() == null || empresa.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }
    }
}
```

## Diagrama de Secuencia

```
Cliente → EmpresaService.guardar(empresa)
          │
          ├→ BaseService.guardar(empresa)  [Template Method]
             │
             ├→ 1. EmpresaService.validarEntidad(empresa)
             │
             ├→ 2. EmpresaService.normalizarDatos(empresa)
             │
             ├→ 3. EmpresaService.validarUnicidad(empresa)
             │
             ├→ 4. EmpresaService.procesarRelaciones(empresa)
             │
             └→ 5. [Si tiene ID]
                   ├→ EmpresaService.actualizarEntidad(empresa)
                   │  └→ EmpresaService.actualizarCampos(existente, nueva)
                   │
                   [Si NO tiene ID]
                   └→ BaseService.crearNuevaEntidad(empresa)
                      └→ Repository.save(empresa)
```

## Testing

El patrón facilita el testing porque:

1. **Puedes testear cada hook por separado**
```java
@Test
void testValidarEntidad() {
    Empresa empresa = new Empresa();
    assertThrows(IllegalArgumentException.class, 
        () -> empresaService.validarEntidad(empresa));
}
```

2. **Puedes mockear repositorios fácilmente**
```java
@Mock
private EmpresaRepository empresaRepository;

@InjectMocks
private EmpresaService empresaService;
```

3. **Puedes verificar el flujo completo**
```java
@Test
void testGuardarFlujoCompleto() {
    // Arrange
    Empresa empresa = crearEmpresaValida();
    
    // Act
    Empresa resultado = empresaService.guardar(empresa);
    
    // Assert
    assertNotNull(resultado);
    verify(empresaRepository).save(any(Empresa.class));
}
```

## Conclusión

La implementación del patrón Template Method en Greedy Empresa proporciona:

✅ **Código más limpio y mantenible**  
✅ **Menos duplicación**  
✅ **Comportamiento consistente entre servicios**  
✅ **Facilita agregar nuevos servicios**  
✅ **Mejora la testeabilidad**  
✅ **Sigue principios SOLID**  

---

**Autor:** Sistema de Gestión Greedy Empresa  
**Fecha:** Octubre 2025  
**Patrón:** Template Method (GoF)

---

## CAPA DE CONTROLADORES

### Clase Base: `BaseController<T, S>`

La clase abstracta `BaseController` define el **Template Method** para operaciones CRUD en controladores web:

```
BaseController (Clase Abstracta)
│
├── Template Methods (endpoints CRUD)
│   ├── listar(filtro, pageable, model)
│   ├── nuevo(model)
│   ├── crear(entidad, bindingResult, redirectAttributes, model)
│   ├── editar(id, model)
│   ├── actualizar(id, entidad, bindingResult, redirectAttributes, model)
│   └── eliminar(id, redirectAttributes)
│
├── Hook Methods (pueden sobrescribirse)
│   ├── agregarDatosAdicionalesListado()
│   ├── agregarDatosAdicionalesFormulario()
│   ├── validacionesAdicionales()
│   ├── prepararEntidadParaEdicion()
│   ├── getMensajeExitoCrear()
│   ├── getMensajeExitoActualizar()
│   └── getMensajeExitoEliminar()
│
└── Métodos Abstractos (deben implementarse)
    ├── getActiveMenu()
    ├── getBasePath()
    ├── getModelAttributeName()
    └── crearNuevaEntidad()
```

### Flujo del Template Method en Controladores

#### `crear()` y `actualizar()`

```
1. validacionesAdicionales(entidad, bindingResult)   → Hook Method
2. SI hay errores de validación:
   └── agregarDatosAdicionalesFormulario(model)      → Hook Method
   └── retornar formulario
3. SI NO hay errores:
   └── service.guardar(entidad)
   └── mensaje de éxito                              → Hook Method
   └── redireccionar a listado
```

## Implementaciones en Controladores

### 1. EmpresaController

**Características específicas:**
- Carga localidades para el formulario
- Asegura al menos una dirección
- Genera archivos Excel

**Hooks sobrescritos:**
```java
- getActiveMenu()                     → "empresas"
- getBasePath()                       → "empresas"
- getModelAttributeName()             → "empresa"
- crearNuevaEntidad()                 → Empresa con una dirección
- agregarDatosAdicionalesFormulario() → Carga localidades
- validacionesAdicionales()           → Valida direcciones
```

**Métodos adicionales:**
- `descargarExcel()` - Exporta empresas a Excel

### 2. ProveedorController

**Características específicas:**
- Carga localidades para el formulario
- Inicializa persona y direcciones
- Genera archivos PDF

**Hooks sobrescritos:**
```java
- getActiveMenu()                     → "proveedores"
- getBasePath()                       → "proveedores"
- getModelAttributeName()             → "proveedor"
- crearNuevaEntidad()                 → Proveedor con persona y dirección
- agregarDatosAdicionalesFormulario() → Carga localidades
- validacionesAdicionales()           → Valida persona y direcciones
- prepararEntidadParaEdicion()        → Inicializa persona si es null
```

**Métodos adicionales:**
- `descargarPdf()` - Exporta proveedores a PDF

### 3. UsuarioController

**Características específicas:**
- Inicializa persona de usuario
- Limpia contraseña en edición
- Proporciona lista de roles

**Hooks sobrescritos:**
```java
- getActiveMenu()                  → "usuarios"
- getBasePath()                    → "usuarios"
- getModelAttributeName()          → "usuario"
- crearNuevaEntidad()              → Usuario con rol USER y persona
- validacionesAdicionales()        → Valida persona
- prepararEntidadParaEdicion()     → Limpia password para formulario
```

**Métodos auxiliares:**
- `roles()` - Proporciona los roles disponibles al formulario

---

## Comparación de Capas

### Servicios vs Controladores

| Aspecto | BaseService | BaseController |
|---------|-------------|----------------|
| **Propósito** | Lógica de negocio y persistencia | Manejo de requests HTTP |
| **Template Methods** | `guardar()`, `buscar()`, `eliminar()` | `crear()`, `listar()`, `actualizar()` |
| **Hooks principales** | Validación, normalización, relaciones | Preparación de datos, validaciones UI |
| **Retorno** | Entidades de dominio | Vistas y redirecciones |
| **Manejo de errores** | Excepciones de dominio | BindingResult, FlashAttributes |

---

## Diagrama Completo de la Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                         CAPA WEB                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐    │
│  │           BaseController<T, S>                         │    │
│  │  (Template Method para operaciones HTTP)               │    │
│  └────────────────────────────────────────────────────────┘    │
│           △                △                △                   │
│           │                │                │                   │
│  ┌────────┴────┐  ┌────────┴────┐  ┌────────┴────┐            │
│  │  Empresa    │  │  Proveedor  │  │  Usuario    │            │
│  │ Controller  │  │ Controller  │  │ Controller  │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
│                                                                  │
└──────────────────────────┬───────────────────────────────────────┘
                           │
                           │ usa
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                      CAPA DE SERVICIOS                            │
├───────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │           BaseService<T, REPOSITORY>                   │     │
│  │  (Template Method para lógica de negocio)              │     │
│  └────────────────────────────────────────────────────────┘     │
│           △                △                △                    │
│           │                │                │                    │
│  ┌────────┴────┐  ┌────────┴────┐  ┌────────┴────┐             │
│  │  Empresa    │  │  Proveedor  │  │  Usuario    │             │
│  │  Service    │  │  Service    │  │  Service    │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
│                                                                   │
└──────────────────────────┬────────────────────────────────────────┘
                           │
                           │ usa
                           │
┌──────────────────────────▼────────────────────────────────────────┐
│                    CAPA DE PERSISTENCIA                            │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌────────────────────────────────────────────────────────┐      │
│  │              BaseEntity                                │      │
│  │  (Clase base con ID y soft delete)                     │      │
│  └────────────────────────────────────────────────────────┘      │
│           △                △                △                     │
│           │                │                │                     │
│  ┌────────┴────┐  ┌────────┴────┐  ┌────────┴────┐              │
│  │  Empresa    │  │  Proveedor  │  │  Usuario    │              │
│  │  Entity     │  │  Entity     │  │  Entity     │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

---

## Beneficios Globales de la Implementación

### 1. **Consistencia en Toda la Aplicación**
- Misma estructura en servicios y controladores
- Facilita el onboarding de nuevos desarrolladores
- Código predecible y fácil de entender

### 2. **Máxima Reutilización**
- ~80% del código CRUD es compartido
- Reducción dramática de código duplicado
- Menos bugs por inconsistencias

### 3. **Mantenimiento Simplificado**
- Cambios en una sola clase afectan a todos los servicios/controladores
- Fácil agregar nuevas funcionalidades globales
- Debugging más eficiente

### 4. **Extensibilidad**
- Agregar nuevos CRUDs es trivial
- Solo implementar métodos abstractos
- Sobrescribir hooks según necesidad

### 5. **Testing Mejorado**
- Test de BaseService/BaseController cubre comportamiento común
- Tests específicos solo para lógica personalizada
- Mayor cobertura con menos esfuerzo

---

## Ejemplo Completo: Flujo de Crear Empresa

```
Usuario → HTTP POST /empresas
          │
          ├→ EmpresaController.crear()          [Capa Web]
             │
             ├→ BaseController.crear()          [Template Method]
                │
                ├→ 1. EmpresaController.validacionesAdicionales()
                │     └─ Asegurar al menos una dirección
                │
                ├→ 2. BindingResult.hasErrors()?
                │     └─ SI: EmpresaController.agregarDatosAdicionalesFormulario()
                │          └─ Cargar localidades
                │          └─ Retornar vista "empresas/form"
                │
                └→ 3. EmpresaService.guardar(empresa)  [Capa Servicio]
                      │
                      ├→ BaseService.guardar()        [Template Method]
                         │
                         ├→ 3.1. EmpresaService.validarEntidad()
                         │       └─ Verificar razón social obligatoria
                         │
                         ├→ 3.2. EmpresaService.normalizarDatos()
                         │       └─ Trim razón social
                         │
                         ├→ 3.3. EmpresaService.validarUnicidad()
                         │       └─ Buscar razón social duplicada
                         │
                         ├→ 3.4. EmpresaService.procesarRelaciones()
                         │       └─ Configurar direcciones
                         │
                         └→ 3.5. BaseService.crearNuevaEntidad()
                                └─ Repository.save()
                                └─ Retornar empresa guardada
          │
          └→ Redirect /empresas con mensaje de éxito
```

---

## Conclusión

La implementación del patrón Template Method en **dos capas** (Servicios y Controladores) proporciona:

✅ **Arquitectura limpia y consistente**  
✅ **Código DRY (Don't Repeat Yourself)**  
✅ **Fácil mantenimiento y evolución**  
✅ **Excelente testeabilidad**  
✅ **Onboarding rápido para nuevos desarrolladores**  
✅ **Cumple con principios SOLID**  
✅ **Separación clara de responsabilidades**  

El patrón demuestra su valor al permitir que el **80% del código CRUD** sea reutilizable, mientras que el **20% restante** se personaliza según las necesidades específicas de cada entidad.

---

**Autor:** Sistema de Gestión Greedy Empresa  
**Fecha:** Octubre 2025  
**Patrón:** Template Method (GoF)
