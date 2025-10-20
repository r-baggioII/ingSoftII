# 📚 Guía de Uso de BaseService - Métodos en Español

## 🎯 Métodos CRUD Principales

El `BaseService` proporciona los siguientes métodos en español para las operaciones CRUD:

---

### 1. **ALTA** - Crear/Insertar una entidad

```java
// Dar de alta una nueva categoría
Categoria categoria = new Categoria();
categoria.setNombre("Acción");

Categoria guardada = servicioCategoria.alta(categoria);
```

**Comportamiento:**
- ✅ Establece `activo = true` automáticamente
- ✅ Llama a `validar(entidad)` antes de guardar
- ✅ Llama a `preAlta(entidad)` antes de guardar
- ✅ Guarda la entidad en la base de datos
- ✅ Llama a `postAlta(entidad)` después de guardar
- ⚠️ Lanza `Exception` si ocurre algún error

---

### 2. **OBTENER** - Buscar por ID

```java
// Obtener un videojuego por su ID
Videojuego videojuego = servicioVideojuego.obtener(1L);
```

**Comportamiento:**
- ✅ Busca la entidad por ID
- ⚠️ Lanza `Exception` si no existe: "Entidad no encontrada con ID: X"
- ✅ Retorna la entidad encontrada

---

### 3. **LISTAR** - Obtener todas las entidades

```java
// Listar todos los estudios (activos e inactivos)
List<Estudio> todosLosEstudios = servicioEstudio.listar();
```

**Comportamiento:**
- ✅ Retorna TODAS las entidades (sin filtrar por `activo`)
- ✅ Retorna una lista vacía si no hay entidades

---

### 4. **LISTAR ACTIVOS** - Obtener solo entidades activas

```java
// Listar solo las categorías activas
List<Categoria> categoriasActivas = servicioCategoria.listarActivos();
```

**Comportamiento:**
- ✅ Filtra automáticamente: solo entidades con `activo = true`
- ✅ Retorna una lista vacía si no hay entidades activas

---

### 5. **MODIFICAR** - Actualizar una entidad existente

```java
// Modificar un videojuego existente
Videojuego videojuego = new Videojuego();
videojuego.setTitulo("The Last of Us Part II");
videojuego.setPrecio(59.99f);
videojuego.setStock(100);
// ... otros campos ...

Videojuego actualizado = servicioVideojuego.modificar(videojuego, 5L);
```

**Comportamiento:**
- ✅ Verifica que la entidad con ese ID existe
- ✅ Establece el ID en la entidad automáticamente
- ✅ Llama a `validar(entidad)` antes de actualizar
- ✅ Llama a `preModificacion(entidad)` antes de actualizar
- ✅ Actualiza la entidad en la base de datos
- ✅ Llama a `postModificacion(entidad)` después de actualizar
- ⚠️ Lanza `Exception` si la entidad no existe

---

### 6. **BAJA** - Eliminar lógicamente (soft delete)

```java
// Dar de baja un estudio (cambiar su estado activo)
boolean exitoso = servicioEstudio.baja(3L);
```

**Comportamiento:**
- ✅ Busca la entidad por ID
- ✅ Llama a `preBaja(entidad)` antes de la baja
- ✅ **Invierte el estado** del campo `activo`:
  - Si `activo = true` → cambia a `false`
  - Si `activo = false` → cambia a `true`
- ✅ Llama a `postBaja(entidad)` después de la baja
- ✅ Retorna `true` si la operación fue exitosa
- ⚠️ Lanza `Exception` si la entidad no existe

**⚠️ Importante:** El método `baja()` **NO elimina** la entidad de la base de datos, solo invierte su estado `activo`. Esto permite:
- Dar de baja entidades activas (`activo: true → false`)
- Reactivar entidades inactivas (`activo: false → true`)

---

## 🔄 Compatibilidad con Nombres en Inglés

Para mantener compatibilidad con código existente (controladores, etc.), también puedes usar los nombres en inglés:

```java
// Nombres en inglés (llaman a los métodos en español internamente)
Categoria categoria = servicioCategoria.saveOne(nuevaCategoria);  // → llama a alta()
Estudio estudio = servicioEstudio.findById(1L);                   // → llama a obtener()
List<Videojuego> todos = servicioVideojuego.findAll();            // → llama a listar()
Videojuego actualizado = servicioVideojuego.updateOne(v, 5L);     // → llama a modificar()
boolean ok = servicioCategoria.deleteById(2L);                    // → llama a baja()
```

**Recomendación:** Usa los nombres en español para código nuevo, y los nombres en inglés solo si necesitas mantener compatibilidad.

---

## 🎨 Personalización - Métodos Hook

Puedes sobrescribir estos métodos en tus servicios específicos para agregar lógica personalizada:

### Ejemplo: Validación en ServicioVideojuego

```java
@Service
public class ServicioVideojuego extends BaseService<Videojuego, RepositorioVideojuego> {
    
    @Autowired
    public ServicioVideojuego(RepositorioVideojuego repositorio) {
        super(repositorio);
    }
    
    @Override
    protected void validar(Videojuego entidad) throws Exception {
        // Esta validación se ejecutará automáticamente en alta() y modificar()
        
        if (entidad.getTitulo() == null || entidad.getTitulo().trim().isEmpty()) {
            throw new Exception("El título del videojuego es obligatorio");
        }
        
        if (entidad.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a 0");
        }
        
        if (entidad.getStock() < 0) {
            throw new Exception("El stock no puede ser negativo");
        }
        
        if (entidad.getFechaLanzamiento() == null) {
            throw new Exception("La fecha de lanzamiento es obligatoria");
        }
    }
    
    @Override
    protected void preAlta(Videojuego entidad) throws Exception {
        // Lógica ANTES de dar de alta
        System.out.println("Dando de alta videojuego: " + entidad.getTitulo());
    }
    
    @Override
    protected void postAlta(Videojuego entidad) throws Exception {
        // Lógica DESPUÉS de dar de alta
        System.out.println("Videojuego dado de alta exitosamente con ID: " + entidad.getId());
        // Aquí podrías enviar notificaciones, registrar en logs, etc.
    }
    
    @Override
    protected void preBaja(Videojuego entidad) throws Exception {
        // Verificar si el videojuego tiene pedidos pendientes antes de darlo de baja
        if (entidad.getStock() > 0) {
            throw new Exception("No se puede dar de baja un videojuego con stock disponible");
        }
    }
}
```

### Métodos Hook Disponibles:

| Método | Cuándo se ejecuta |
|--------|-------------------|
| `validar(entidad)` | Antes de `alta()` y `modificar()` |
| `preAlta(entidad)` | Justo antes de guardar en `alta()` |
| `postAlta(entidad)` | Justo después de guardar en `alta()` |
| `preModificacion(entidad)` | Justo antes de actualizar en `modificar()` |
| `postModificacion(entidad)` | Justo después de actualizar en `modificar()` |
| `preBaja(entidad)` | Justo antes de cambiar estado en `baja()` |
| `postBaja(entidad)` | Justo después de cambiar estado en `baja()` |

---

## 💡 Ejemplos de Uso en Controladores

### Ejemplo 1: Controlador de Categorías

```java
@Controller
@RequestMapping("/categorias")
public class ControladorCategoria {
    
    @Autowired
    private ServicioCategoria servicioCategoria;
    
    // Listar todas las categorías activas
    @GetMapping
    public String listar(Model model) {
        try {
            List<Categoria> categorias = servicioCategoria.listarActivos();
            model.addAttribute("categorias", categorias);
            return "categorias/lista";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    // Crear nueva categoría
    @PostMapping("/crear")
    public String crear(@ModelAttribute Categoria categoria, RedirectAttributes attr) {
        try {
            servicioCategoria.alta(categoria);
            attr.addFlashAttribute("exito", "Categoría creada exitosamente");
            return "redirect:/categorias";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias/nuevo";
        }
    }
    
    // Actualizar categoría
    @PostMapping("/editar/{id}")
    public String editar(@PathVariable long id, @ModelAttribute Categoria categoria, 
                         RedirectAttributes attr) {
        try {
            servicioCategoria.modificar(categoria, id);
            attr.addFlashAttribute("exito", "Categoría actualizada exitosamente");
            return "redirect:/categorias";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias/editar/" + id;
        }
    }
    
    // Dar de baja/alta una categoría (toggle)
    @PostMapping("/baja/{id}")
    public String baja(@PathVariable long id, RedirectAttributes attr) {
        try {
            servicioCategoria.baja(id);
            attr.addFlashAttribute("exito", "Estado de la categoría cambiado exitosamente");
            return "redirect:/categorias";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias";
        }
    }
}
```

---

## ✅ Ventajas del Sistema

1. **📝 Código más legible en español:** Los métodos están en español para mejor comprensión
2. **🔄 Compatibilidad:** Mantiene métodos en inglés para no romper código existente
3. **🎯 Validación centralizada:** Todas las validaciones en un solo lugar
4. **🔌 Extensible:** Fácil agregar lógica personalizada con hooks
5. **🛡️ Manejo de errores:** Todas las excepciones capturadas con mensajes descriptivos
6. **♻️ Reutilizable:** Misma lógica para todas las entidades

---

## 🚀 Resumen de Métodos

| Operación | Método en Español | Método en Inglés | Descripción |
|-----------|-------------------|------------------|-------------|
| **Crear** | `alta(entidad)` | `saveOne(entidad)` | Da de alta una nueva entidad |
| **Leer uno** | `obtener(id)` | `findById(id)` | Obtiene una entidad por ID |
| **Leer todos** | `listar()` | `findAll()` | Lista todas las entidades |
| **Leer activos** | `listarActivos()` | - | Lista solo entidades activas |
| **Actualizar** | `modificar(entidad, id)` | `updateOne(entidad, id)` | Modifica una entidad existente |
| **Eliminar** | `baja(id)` | `deleteById(id)` | Baja lógica (toggle activo) |

---

**¡Listo para usar!** 🎉

Todos tus servicios (`ServicioCategoria`, `ServicioEstudio`, `ServicioVideojuego`) ya heredan estos métodos automáticamente.
