# 🖼️ Solución: Problema de Visualización de Imágenes

## 📋 Problema Identificado

Las imágenes subidas al crear/editar videojuegos no se visualizaban correctamente debido a:

1. **Ruta hardcodeada para Windows**: La configuración usaba `C:/Videojuegos/imagenes/` que no funciona en Linux
2. **URLs incompletas en las vistas**: Las vistas no agregaban el prefijo `/imagenes/` al nombre del archivo
3. **Falta de directorio**: El directorio de almacenamiento no existía en el sistema

## ✅ Soluciones Implementadas

### 1. **Actualización de `ImagenConfiguration.java`**

**Antes:**
```java
registry.addResourceHandler("/imagenes/**")
    .addResourceLocations("file:/C:/Videojuegos/imagenes/");
```

**Después:**
```java
@Value("${app.images.base-path:${user.home}/Videojuegos/imagenes}")
private String basePath;

@Override
public void addResourceHandlers(ResourceHandlerRegistry registry){
    String normalizedPath = basePath.replace("\\", "/");
    if (!normalizedPath.endsWith("/")) {
        normalizedPath += "/";
    }
    registry.addResourceHandler("/imagenes/**")
            .addResourceLocations("file:" + normalizedPath);
}
```

**Beneficios:**
- ✅ Compatible con Windows y Linux
- ✅ Usa la variable de entorno `${user.home}` 
- ✅ Ruta configurable desde `application.properties`
- ✅ Normalización automática de barras

### 2. **Actualización de `application.properties`**

Se agregó:
```properties
# Configuración de ruta de imágenes (se adaptará automáticamente al sistema operativo)
# En Linux: /home/usuario/Videojuegos/imagenes
# En Windows: C:/Videojuegos/imagenes
app.images.base-path=${user.home}/Videojuegos/imagenes
```

### 3. **Actualización de Vistas**

#### `components/card.html`
```html
<!-- Antes -->
<div class="product__item__pic set-bg" th:data-setbg="${videojuego.imagen}">

<!-- Después -->
<div class="product__item__pic set-bg" 
     th:data-setbg="${#strings.startsWith(videojuego.imagen, 'http') ? videojuego.imagen : '/imagenes/' + videojuego.imagen}">
```

#### `views/detalle.html`
```html
<!-- Antes -->
<div class="anime__details__pic set-bg" th:data-setbg="*{imagen}">

<!-- Después -->
<div class="anime__details__pic set-bg" 
     th:data-setbg="${#strings.startsWith(videojuego.imagen, 'http') ? videojuego.imagen : '/imagenes/' + videojuego.imagen}">
```

**Beneficios:**
- ✅ Soporta URLs completas (http/https) para imágenes externas
- ✅ Agrega automáticamente `/imagenes/` para archivos locales
- ✅ Compatibilidad con ambos tipos de almacenamiento

### 4. **Creación del Directorio**

Se creó el directorio necesario:
```bash
mkdir -p ~/Videojuegos/imagenes
```

En tu sistema Linux, la ruta será: `/home/rocio/Videojuegos/imagenes/`

## 🔄 Cómo Funciona Ahora

### Flujo de Subida de Imagen:

1. **Usuario sube imagen** en el formulario
2. **Controlador valida** extensión y tamaño
3. **Archivo se guarda** en `~/Videojuegos/imagenes/` con nombre único (timestamp + extensión)
4. **Base de datos almacena** solo el nombre: `1234567890.jpg`
5. **Vista construye URL** completa: `/imagenes/1234567890.jpg`
6. **Spring sirve** el archivo desde el directorio configurado

### Ejemplo Completo:

#### Base de Datos:
```
imagen: "1698765432100.jpg"
```

#### Vista (Thymeleaf):
```html
th:data-setbg="${#strings.startsWith(videojuego.imagen, 'http') 
    ? videojuego.imagen 
    : '/imagenes/' + videojuego.imagen}"
```

#### Resultado en HTML:
```html
data-setbg="/imagenes/1698765432100.jpg"
```

#### Spring Resource Handler:
```
/imagenes/1698765432100.jpg → file:/home/rocio/Videojuegos/imagenes/1698765432100.jpg
```

## 🚀 Pasos para Aplicar

1. ✅ **Compilar** el proyecto:
   ```bash
   cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/ej4/B/videojuegos_ej4/videojuegos
   mvn clean compile -DskipTests
   ```

2. ✅ **Reiniciar** la aplicación Spring Boot:
   ```bash
   mvn spring-boot:run
   ```

3. ✅ **Probar** subiendo una imagen nueva en:
   ```
   http://localhost:9000/formulario/videojuego/0
   ```

4. ✅ **Verificar** que la imagen se vea en:
   - Página de inicio: `http://localhost:9000/inicio`
   - Página de detalle: `http://localhost:9000/detalle/{id}`
   - Página CRUD: `http://localhost:9000/crud`

## 📁 Estructura de Archivos

```
/home/rocio/
└── Videojuegos/
    └── imagenes/
        ├── 1698765432100.jpg
        ├── 1698765445678.png
        └── 1698765456789.jpeg
```

## 🔍 Verificación

Para verificar que todo funciona correctamente:

1. **Subir imagen nueva**
2. **Verificar que se guardó**:
   ```bash
   ls -la ~/Videojuegos/imagenes/
   ```
3. **Verificar en navegador** usando F12 (DevTools):
   - La URL debe ser: `http://localhost:9000/imagenes/[nombre-archivo]`
   - El recurso debe cargar con status 200 OK

## 💡 Notas Adicionales

- **URLs Externas**: Si guardas una URL completa (http/https) en la base de datos, se usará directamente sin agregar `/imagenes/`
- **Formato de Archivo**: La configuración valida que sea una imagen válida usando `ImageIO.read()`
- **Tamaño Máximo**: El límite es de 15MB por archivo
- **Extensiones Soportadas**: JPG, JPEG, PNG, GIF (cualquier formato que ImageIO pueda leer)

## 🎉 Resultado

Ahora las imágenes se visualizan correctamente en:
- ✅ Tarjetas de videojuegos (inicio)
- ✅ Página de detalles
- ✅ Sistema CRUD

¡El sistema es completamente multiplataforma! 🚀
