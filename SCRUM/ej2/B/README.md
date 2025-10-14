# Proyecto Biblioteca

Este workspace contiene dos aplicaciones separadas:

- `server`: API REST en Spring Boot 3 (Java 21) conectada a MariaDB.
- `client`: SPA en React + Vite (TypeScript).

## Requisitos previos

- Java 21
- Maven 3.9+
- Node.js 18+
- MariaDB/MySQL con base `biblioteca` (usuario `juan`, clave `greedy` por defecto).

## Ejecución

### Back-end

```bash
cd server
./mvnw spring-boot:run
```

El perfil activo es `dev` e inserta datos semilla si la base está vacía. Swagger UI en `http://localhost:8080/swagger-ui.html`.

### Front-end

```bash
cd client
npm install
npm run dev
```

La aplicación queda disponible en `http://localhost:5173`.
