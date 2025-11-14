# Biblioteca API

## Requisitos

- Java 21
- Maven 3.9+
- MariaDB/MySQL en `localhost:3306` con base `biblioteca`

## Variables de entorno opcionales

Si no usas las credenciales por defecto (`root` / `admin`), modifica `src/main/resources/application.properties`.

## Ejecución

```bash
./mvnw spring-boot:run
```

El perfil por defecto es `dev` e inserta datos iniciales si la base está vacía.

## Documentación

- API REST: `http://localhost:8080/api`
- OpenAPI/Swagger UI: `http://localhost:8080/swagger-ui.html`
