# Proyecto Biblioteca con PDFs

Este workspace contiene el backend (Spring Boot) y el frontend (React + Vite) de la biblioteca, ahora con soporte para adjuntar y consultar archivos PDF de cada libro.

---

## Requisitos previos

- Java 21
- Maven 3.9+
- Node.js 18+
- npm 9+
- MariaDB/MySQL con base `greedy_biblioteca` accesible con usuario `juan` y clave `greedy`
- Directorio de almacenamiento en el host: `/srv/biblioteca/libros`

---

## Preparación inicial

1. **Configurar base de datos**  
   Asegurate de tener una instancia MariaDB/MySQL ejecutándose y crea la base si no existe:
   ```sql
   CREATE DATABASE greedy_biblioteca CHARACTER SET utf8mb4;
   ```
   Ajusta credenciales en `server/src/main/resources/application.properties` si difieren.

2. **Crear directorio para PDFs**  
   El backend guarda los PDFs en `/srv/biblioteca/libros`. Crealo y otorga permisos de escritura al usuario que ejecutará la app:
   ```bash
   sudo mkdir -p /srv/biblioteca/libros
   sudo chown $USER:$USER /srv/biblioteca/libros
   ```
   Opcionalmente copia un PDF de prueba con el nombre `libro_el_aleph_<ID>.pdf` para validar la semilla.

3. **Instalar dependencias**  
   - Backend: desde `server/` ejecuta `mvn dependency:go-offline` si querés precargar dependencias.
   - Frontend: desde `client/` ejecuta `npm install`.

---

## Ejecutar el backend (Spring Boot)

```bash
cd server
mvn spring-boot:run
```

- Perfil activo: `dev`.  
- Inserta datos semilla si la base está vacía (incluye un libro con PDF).  
- API disponible en `http://localhost:8080/api`.  
- Documentación: `http://localhost:8080/swagger-ui.html`.

> Si necesitas cambiar el directorio de almacenamiento o los límites de subida, edita `app.storage.pdf-dir` y `spring.servlet.multipart.*` en `application.properties`.

---

## Ejecutar el frontend (React + Vite)

En una terminal aparte:

```bash
cd client
npm install          # solo la primera vez
npm run dev
```

La aplicación queda en `http://localhost:5173`.

---

## Flujo de uso

1. Crear o editar un libro desde la UI y adjuntar un PDF (`application/pdf` hasta 50 MB).  
2. El backend genera el nombre `libro_<slug_titulo>_<id>.pdf` y lo guarda en `/srv/biblioteca/libros`.  
3. En la tabla/listado, si el libro tiene PDF se muestra “Ver PDF”; el enlace abre `GET /api/libros/{id}/pdf` en una pestaña nueva (content-disposition inline).  
4. Al actualizar un libro con un nuevo PDF, el archivo anterior se elimina y se reemplaza.

---

## Scripts útiles

| Comando | Ubicación | Descripción |
|---------|-----------|-------------|
| `mvn -q -DskipTests compile` | `server/` | Compila el backend |
| `npm run build` | `client/` | Genera build de producción del frontend |
| `npm run lint` (si configurado) | `client/` | Ejecuta linting |

---

## Variables relevantes

`server/src/main/resources/application.properties`:

```
app.storage.pdf-dir=/srv/biblioteca/libros
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

Ajusta la ruta si necesitas otro destino o si estás en Windows.

---

## Notas finales

- No se utilizaron contenedores; toda la infraestructura es local.  
- Si cambias la ruta de los PDFs, recordá replicar el directorio y permisos.  
- Para ambientes productivos, apunta `app.storage.pdf-dir` a un volumen persistente con backup.  
- La API espera `multipart/form-data` con `data` (JSON del libro) + `pdf` (archivo opcional). Asegurate de usar el cliente actualizado.
