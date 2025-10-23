# Sistema de Gestión de Taller Mecánico

Sistema completo desarrollado en Spring Boot siguiendo el patrón Template (herencia) y arquitectura MVC.

## 📋 Diagrama UML Implementado

### Entidades del Sistema

1. **Persona** (Clase Base)
   - id: String
   - nombre: String
   - apellido: String
   - eliminado: Boolean

2. **Cliente** (Hereda de Persona)
   - documento: String
   - Relación: *...1 con Vehiculo

3. **Mecanico** (Hereda de Persona)
   - legajo: String
   - Relación: *...1 con Usuario
   - Relación: *...1 con HistorialArreglo

4. **Usuario** (Clase Independiente)
   - id: String
   - nombre: String
   - clave: String
   - rol: Enum (Rol)
   - eliminado: Boolean

5. **Vehiculo**
   - id: String
   - patente: String
   - marca: String
   - modelo: String
   - eliminado: Boolean
   - Relación: *...1 con Cliente
   - Relación: 1..* con HistorialArreglo (Composición)

6. **HistorialArreglo**
   - id: String
   - fechaArreglo: Date
   - detalleArreglo: String
   - eliminado: Boolean
   - Relación: *...1 con Vehiculo
   - Relación: *...1 con Mecanico

## 🏗️ Arquitectura del Proyecto

### Patrón Template Implementado

El proyecto implementa el patrón Template mediante herencia en tres capas:

#### 1. Capa de Dominio (Entities)
```
BaseEntity<ID> (Clase abstracta)
    ├── Persona
    │   ├── Cliente
    │   └── Mecanico
    ├── Usuario
    ├── Vehiculo
    └── HistorialArreglo
```

#### 2. Capa de Persistencia (Repositories)
```
BaseRepository<T, ID> (Interface)
    ├── PersonaRepository
    ├── ClienteRepository
    ├── MecanicoRepository
    ├── UsuarioRepository
    ├── VehiculoRepository
    └── HistorialArregloRepository
```

#### 3. Capa de Lógica de Negocio (Services)
```
BaseService<T, ID> (Clase abstracta)
    ├── PersonaService
    ├── ClienteService
    ├── MecanicoService
    ├── UsuarioService
    ├── VehiculoService
    └── HistorialArregloService
```

#### 4. Capa de Presentación (Controllers)
```
BaseController<T, ID> (Clase abstracta)
    ├── PersonaController
    ├── ClienteController
    ├── MecanicoController
    ├── UsuarioController
    ├── VehiculoController
    └── HistorialArregloController
```

## 📁 Estructura de Directorios

```
src/main/java/com/is/biblioteca/
├── MecanicoApplication.java
├── Security.java
├── business/
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── BaseEntity.java
│   │   │   ├── Persona.java
│   │   │   ├── Cliente.java
│   │   │   ├── Mecanico.java
│   │   │   ├── Usuario.java
│   │   │   ├── Vehiculo.java
│   │   │   └── HistorialArreglo.java
│   │   └── enumeration/
│   │       ├── BaseUseCaseService.java
│   │       ├── BaseUseCaseController.java
│   │       └── Rol.java
│   ├── logic/
│   │   ├── service/
│   │   │   ├── BaseService.java
│   │   │   ├── PersonaService.java
│   │   │   ├── ClienteService.java
│   │   │   ├── MecanicoService.java
│   │   │   ├── UsuarioService.java
│   │   │   ├── VehiculoService.java
│   │   │   └── HistorialArregloService.java
│   │   └── error/
│   │       └── ErrorServiceException.java
│   └── persistence/
│       └── repository/
│           ├── BaseRepository.java
│           ├── PersonaRepository.java
│           ├── ClienteRepository.java
│           ├── MecanicoRepository.java
│           ├── UsuarioRepository.java
│           ├── VehiculoRepository.java
│           └── HistorialArregloRepository.java
└── controller/
    ├── BaseController.java
    └── view/
        ├── HomeController.java
        ├── PersonaController.java
        ├── ClienteController.java
        ├── MecanicoController.java
        ├── UsuarioController.java
        ├── VehiculoController.java
        └── HistorialArregloController.java

src/main/resources/
└── templates/
    ├── index_sistema.html
    └── admin/
        ├── persona.html
        ├── persona-form.html
        ├── cliente.html
        ├── cliente-form.html
        ├── mecanico.html
        ├── mecanico-form.html
        ├── usuario.html
        ├── usuario-form.html
        ├── vehiculo.html
        ├── vehiculo-form.html
        ├── historialarreglo.html
        └── historialarreglo-form.html
```

## 🚀 Funcionalidades ABM

Cada entidad del sistema cuenta con operaciones completas de ABM (Alta, Baja, Modificación):

### 1. **Personas**
- ✅ Listar personas activas
- ✅ Crear nueva persona
- ✅ Consultar detalle de persona
- ✅ Modificar persona existente
- ✅ Dar de baja persona (soft delete)

### 2. **Clientes**
- ✅ Listar clientes activos
- ✅ Crear nuevo cliente (con documento)
- ✅ Consultar detalle de cliente
- ✅ Modificar cliente existente
- ✅ Dar de baja cliente

### 3. **Mecánicos**
- ✅ Listar mecánicos activos
- ✅ Crear nuevo mecánico (con legajo y usuario)
- ✅ Consultar detalle de mecánico
- ✅ Modificar mecánico existente
- ✅ Dar de baja mecánico
- ✅ Asignar usuario a mecánico

### 4. **Vehículos**
- ✅ Listar vehículos activos
- ✅ Crear nuevo vehículo (con patente, marca, modelo)
- ✅ Consultar detalle de vehículo
- ✅ Modificar vehículo existente
- ✅ Dar de baja vehículo
- ✅ Asignar vehículo a cliente

### 5. **Historial de Arreglos**
- ✅ Listar historial de arreglos
- ✅ Crear nuevo registro de arreglo
- ✅ Consultar detalle de arreglo
- ✅ Modificar arreglo existente
- ✅ Dar de baja arreglo
- ✅ Vincular arreglo con vehículo y mecánico

### 6. **Usuarios**
- ✅ Listar usuarios activos
- ✅ Crear nuevo usuario (con nombre, clave y rol)
- ✅ Consultar detalle de usuario
- ✅ Modificar usuario existente
- ✅ Dar de baja usuario

## 🎯 Características del Patrón Template

### BaseEntity
- Proporciona el campo `eliminado` para soft deletes
- Define métodos abstractos `getId()` y `setId()`
- Permite reutilización en todas las entidades

### BaseRepository
- Interface genérica que extiende JpaRepository
- Utiliza `@NoRepositoryBean` para evitar instanciación
- Permite operaciones CRUD sin código repetido

### BaseService
- Implementa operaciones CRUD genéricas
- Métodos template: `alta()`, `baja()`, `modificar()`, `listar()`
- Hooks para personalización: `preAlta()`, `postAlta()`, `validar()`
- Manejo centralizado de excepciones

### BaseController
- Maneja todas las rutas estándar: `/list`, `/alta`, `/modificar/{id}`, `/baja/{id}`
- Utiliza Thymeleaf para renderizar vistas dinámicas
- Mensajes de éxito y error unificados
- Inicialización automática de entidades

## 🔗 Relaciones Entre Entidades

1. **Cliente → Vehiculo** (1:N)
   - Un cliente puede tener múltiples vehículos
   - Un vehículo pertenece a un solo cliente

2. **Vehiculo → HistorialArreglo** (1:N Composición)
   - Un vehículo tiene múltiples arreglos en su historial
   - Los arreglos dependen del vehículo (ciclo de vida vinculado)

3. **Mecanico → HistorialArreglo** (1:N)
   - Un mecánico puede realizar múltiples arreglos
   - Cada arreglo es realizado por un solo mecánico

4. **Mecanico → Usuario** (N:1)
   - Múltiples mecánicos pueden compartir un usuario
   - Un mecánico está vinculado a un usuario del sistema

5. **Persona → Cliente, Mecanico** (Herencia)
   - Cliente y Mecanico heredan de Persona
   - Comparten atributos comunes: nombre, apellido

## 🎨 Vistas HTML

Todas las vistas incluyen:
- Navegación consistente entre módulos
- Bootstrap 5 para diseño responsive
- Formularios con validación
- Tablas para listar registros
- Botones de acción (Ver, Editar, Eliminar)
- Mensajes de confirmación y error

## 📝 URLs del Sistema

### Página Principal
- `/` - Página de inicio del sistema

### Personas
- `/persona/list` - Listar personas
- `/persona/alta` - Crear persona
- `/persona/consultar/{id}` - Ver detalle
- `/persona/modificar/{id}` - Editar
- `/persona/baja/{id}` - Eliminar

### Clientes
- `/cliente/list` - Listar clientes
- `/cliente/alta` - Crear cliente
- `/cliente/consultar/{id}` - Ver detalle
- `/cliente/modificar/{id}` - Editar
- `/cliente/baja/{id}` - Eliminar

### Mecánicos
- `/mecanico/list` - Listar mecánicos
- `/mecanico/alta` - Crear mecánico
- `/mecanico/consultar/{id}` - Ver detalle
- `/mecanico/modificar/{id}` - Editar
- `/mecanico/baja/{id}` - Eliminar

### Vehículos
- `/vehiculo/list` - Listar vehículos
- `/vehiculo/alta` - Crear vehículo
- `/vehiculo/consultar/{id}` - Ver detalle
- `/vehiculo/modificar/{id}` - Editar
- `/vehiculo/baja/{id}` - Eliminar

### Historial de Arreglos
- `/historialarreglo/list` - Listar historial
- `/historialarreglo/alta` - Crear arreglo
- `/historialarreglo/consultar/{id}` - Ver detalle
- `/historialarreglo/modificar/{id}` - Editar
- `/historialarreglo/baja/{id}` - Eliminar

### Usuarios
- `/usuario/list` - Listar usuarios
- `/usuario/alta` - Crear usuario
- `/usuario/consultar/{id}` - Ver detalle
- `/usuario/modificar/{id}` - Editar
- `/usuario/baja/{id}` - Eliminar

## 🔧 Tecnologías Utilizadas

- **Spring Boot** - Framework principal
- **Spring Data JPA** - Capa de persistencia
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5** - Framework CSS
- **Hibernate** - ORM
- **MySQL/PostgreSQL** - Base de datos (configurable)

## 💡 Ventajas del Patrón Template Implementado

1. **Reutilización de Código**: Las operaciones CRUD no se repiten
2. **Mantenibilidad**: Cambios en BaseController/BaseService afectan a todos
3. **Consistencia**: Todas las entidades se comportan de manera similar
4. **Extensibilidad**: Fácil agregar nuevas entidades
5. **Separación de Responsabilidades**: Cada capa tiene su propósito claro
6. **DRY (Don't Repeat Yourself)**: Código limpio y sin duplicación

## 🏁 Cómo Ejecutar

1. Configurar base de datos en `application.properties`
2. Ejecutar `MecanicoApplication.java`
3. Acceder a `http://localhost:8080/`

## 📚 Documentación Adicional

Para más información sobre el patrón Template y las mejores prácticas de Spring Boot, consultar:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Template Method Pattern](https://refactoring.guru/design-patterns/template-method)
