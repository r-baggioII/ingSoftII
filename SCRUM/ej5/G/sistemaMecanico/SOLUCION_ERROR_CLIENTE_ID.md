# Solución al Error "Unknown column 'v1_0.cliente_id'"

## Descripción del Problema

El error ocurre porque la tabla `vehiculo` en la base de datos **no tiene la columna `cliente_id`** que Hibernate espera encontrar para la relación entre Cliente y Vehículo.

```
Unknown column 'v1_0.cliente_id' in 'SELECT'
```

Esto sucede cuando:
1. Las tablas fueron creadas manualmente sin la columna correcta
2. La base de datos tiene una estructura desactualizada
3. El `ddl-auto=create` no se ejecutó correctamente

## Solución

### Opción 1: Recrear la Base de Datos (RECOMENDADO)

**Paso 1:** Ejecuta el script SQL en MariaDB:

```bash
mysql -u root -p < /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/ej5/G/sistemaMecanico/recrear_base_datos.sql
```

O ejecuta manualmente en MySQL/MariaDB:

```sql
USE mecanico_db;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS arreglo_mecanico;
DROP TABLE IF EXISTS historial_arreglo;
DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS mecanico;
DROP TABLE IF EXISTS persona;
DROP TABLE IF EXISTS usuario;

SET FOREIGN_KEY_CHECKS = 1;
```

**Paso 2:** Verifica que `application.properties` tenga:

```properties
spring.jpa.hibernate.ddl-auto=create
```

**Paso 3:** Reinicia la aplicación Spring Boot. Hibernate recreará todas las tablas con la estructura correcta.

**Paso 4:** Una vez que funcione correctamente, cambia a:

```properties
spring.jpa.hibernate.ddl-auto=update
```

### Opción 2: Agregar la Columna Manualmente

Si no quieres perder datos, ejecuta:

```sql
USE mecanico_db;

-- Agregar la columna cliente_id a la tabla vehiculo
ALTER TABLE vehiculo 
ADD COLUMN cliente_id VARCHAR(36) NULL,
ADD CONSTRAINT fk_vehiculo_cliente 
    FOREIGN KEY (cliente_id) 
    REFERENCES cliente(id);

-- Verificar
DESCRIBE vehiculo;
```

## Cambios Realizados en el Código

### 1. En `Vehiculo.java`
- Cambié el fetch de la relación con Cliente de `EAGER` a `LAZY` para evitar cargas innecesarias.

### 2. En `BaseController.java`
- Agregué `isDisabled` en todos los métodos que manejan errores para evitar el error de Thymeleaf.

## Verificación

Después de aplicar la solución, prueba:

1. **Crear un nuevo cliente** desde el formulario
2. Verificar que **redirija correctamente** a la lista de clientes
3. **Consultar el cliente** creado sin errores

## Estructura Correcta de la Tabla Vehiculo

La tabla `vehiculo` debe tener esta estructura:

```sql
CREATE TABLE vehiculo (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    patente VARCHAR(20) NOT NULL UNIQUE,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    eliminado BOOLEAN DEFAULT FALSE,
    cliente_id VARCHAR(36) NULL,  -- <-- Esta columna debe existir
    CONSTRAINT fk_vehiculo_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);
```

## Notas Adicionales

- El error ocurría después de guardar el cliente porque Hibernate intentaba recargar la entidad con sus relaciones
- La relación `@OneToMany` en Cliente y `@ManyToOne` en Vehiculo requiere la columna `cliente_id` en la tabla `vehiculo`
- Con `LAZY` fetch, la relación solo se carga cuando se accede explícitamente a ella
