# Greedy Biblioteca – API & SPA

Proyecto full-stack que combina Spring Boot 3 (Authorization Server + Resource Server) y una SPA en React para gestionar personas, autores, libros y localidades de una biblioteca.

---

## Endpoints principales

### Autenticación

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/api/auth/token` | Valida `username` + `password` (usuario interno `admin/admin123`), emite un JWT firmado con RSA y con scopes `books.read books.write loans.read loans.write`. | No (requiere credenciales en el body) |

### Personas

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/api/personas` | Lista paginada con filtros opcionales `apellido`, `dni`, `page`, `size`. | Sí (JWT válido) |
| `GET` | `/api/personas/{id}` | Obtiene persona por ID. | Sí |
| `POST` | `/api/personas` | Crea una nueva persona con domicilio asociado. | Sí |
| `PUT` | `/api/personas/{id}` | Actualiza datos de la persona. | Sí |
| `DELETE` | `/api/personas/{id}` | Elimina persona. | Sí |
| `GET` | `/api/personas/{id}/libros` | Lista paginada de libros asociados a la persona. | Sí |

### Autores

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/api/autores` | Lista todos los autores. | Sí |
| `GET` | `/api/autores/{id}` | Obtiene autor por ID. | Sí |
| `POST` | `/api/autores` | Crea un autor. | Sí |
| `PUT` | `/api/autores/{id}` | Actualiza un autor. | Sí |
| `DELETE` | `/api/autores/{id}` | Elimina autor. | Sí |

### Localidades

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/api/localidades` | Lista todas las localidades. | Sí |
| `GET` | `/api/localidades/{id}` | Obtiene localidad por ID. | Sí |
| `POST` | `/api/localidades` | Crea una localidad. | Sí |

### Libros

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/api/libros` | Lista paginada con filtros `autorId`, `personaId`, `genero`. | Sí + `SCOPE_books.read` |
| `GET` | `/api/libros/{id}` | Detalle de libro. | Sí + `SCOPE_books.read` |
| `POST` | `/api/libros` | Crea un libro vinculado a autor y persona. | Sí + `SCOPE_books.write` |
| `PUT` | `/api/libros/{id}` | Actualiza un libro. | Sí + `SCOPE_books.write` |
| `DELETE` | `/api/libros/{id}` | Elimina un libro. | Sí + `SCOPE_books.write` |

### Endpoints OAuth2 estándar

Expuestos por el Authorization Server para integraciones externas:

| Ruta | Descripción |
|------|-------------|
| `/oauth2/authorize` | Inicio de flujo `authorization_code`. |
| `/oauth2/token` | Emisión de tokens (autorización, refresh, client_credentials). |
| `/oauth2/jwks` | Publica la JWK RSA usada para firmar los JWT. |
| `/oauth2/revoke`, `/oauth2/introspect` | Rotación y análisis opcional. |

---

## Flujos de autenticación

### SPA (login básico)
1. El usuario navega a `/login` en la SPA (React).
2. La SPA envía `POST /api/auth/token` con credenciales y recibe un JWT.
3. El token se guarda en `localStorage` y se adjunta como `Authorization: Bearer`.
4. Las llamadas a `/api/**` quedan habilitadas según el scope.

### OAuth2 completo (clientes externos)
Uso del cliente `biblioteca-client` configurado en el servidor:

```bash
# Grant client_credentials (ej. scripts/bots)
curl -u biblioteca-client:secret \
  -d "grant_type=client_credentials&scope=books.read" \
  http://localhost:8080/oauth2/token

# Grant authorization_code (usuarios interactivos)
# 1) Navegar a /oauth2/authorize?response_type=code&client_id=biblioteca-client&...
# 2) Intercambiar code por token en /oauth2/token
```

---

## Scopes disponibles

| Scope | Descripción |
|-------|-------------|
| `books.read` | Permite GET sobre `/api/libros/**`. |
| `books.write` | Permite POST/PUT/PATCH/DELETE sobre libros. |
| `loans.read` / `loans.write` | Reservados para endpoints de préstamos (`/api/loans/**`, `/api/prestamos/**`). |

---

## SPA – Enlaces útiles

| Ruta | Descripción |
|------|-------------|
| `/login` | Formulario de ingreso (obtiene JWT). |
| `/personas` | Listado CRUD de personas. |
| `/personas/:id` | Detalle con libros asociados. |
| `/autores` | Gestión de autores. |
| `/libros` | Gestión de libros (requiere scope write para crear/editar). |

En caso de caducar el token, la SPA redirige de nuevo a `/login`. Se puede cerrar sesión desde el encabezado, lo que limpia el token y vuelve a pedir autenticación.

---

## Credenciales de muestra

| Usuario | Password | Rol |
|---------|----------|-----|
| `admin` | `admin123` | ADMIN (configurado en memoria, con scopes completos) |

---

## Requisitos previos

- Java 17+
- Maven 3.8+
- Node 18+ (para la SPA con Vite)

---

## Ejecución rápida

```bash
# Backend
cd server
mvn spring-boot:run

# Frontend
cd client
npm install
npm run dev
```

La SPA queda en `http://localhost:5173`, comunicándose con el backend en `http://localhost:8080`.
