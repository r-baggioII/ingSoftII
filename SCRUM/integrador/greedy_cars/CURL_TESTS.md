# CURL Tests for Cliente and Empleado Creation

## Prerequisites

Before creating Clientes or Empleados, ensure you have:
1. At least one `Direccion` in the database (e.g., ID: 1)
2. At least one `Nacionalidad` in the database for Cliente creation

### Get existing Nacionalidad IDs:
```bash
curl -s 'http://161.153.217.110:18082/greedy_cars/api/nacionalidades' | python3 -m json.tool
```

### Get existing Direccion IDs:
```bash
curl -s 'http://161.153.217.110:18082/greedy_cars/api/direcciones' | python3 -m json.tool
```

## Creating a Cliente (Customer)

### Minimal Cliente (only required fields):
```bash
curl -X POST 'http://161.153.217.110:18082/greedy_cars/api/clientes' \
-H 'Content-Type: application/json' \
-d '{
    "nombre": "Carlos",
    "apellido": "Fernández",
    "fechaNacimiento": "1988-05-15",
    "tipoDocumento": "DNI",
    "numeroDocumento": "35678901",
    "direccionIds": [1],
    "nacionalidadIds": ["<NACIONALIDAD_ID>"]
}'
```

### Cliente with all optional fields:
```bash
curl -X POST 'http://161.153.217.110:18082/greedy_cars/api/clientes' \
-H 'Content-Type: application/json' \
-d '{
    "nombre": "Laura",
    "apellido": "Martínez",
    "fechaNacimiento": "1990-11-20",
    "tipoDocumento": "PASAPORTE",
    "numeroDocumento": "AB1234567",
    "direccionIds": [1],
    "direccionEstadia": "Hotel Continental, Habitación 305",
    "nacionalidadIds": ["<NACIONALIDAD_ID_1>", "<NACIONALIDAD_ID_2>"],
    "contactoIds": ["<CONTACTO_ID>"],
    "imagenIds": ["<IMAGEN_ID>"]
}'
```

### Cliente with multiple nationalities (example):
```bash
curl -X POST 'http://161.153.217.110:18082/greedy_cars/api/clientes' \
-H 'Content-Type: application/json' \
-d '{
    "nombre": "Roberto",
    "apellido": "Silva",
    "fechaNacimiento": "1985-03-10",
    "tipoDocumento": "DNI",
    "numeroDocumento": "28456789",
    "direccionIds": [1],
    "direccionEstadia": "Airbnb Mendoza Centro",
    "nacionalidadIds": ["argentina-id", "brasilera-id"]
}'
```

## Creating an Empleado (Employee)

### Minimal Empleado (only required fields):
```bash
curl -X POST 'http://161.153.217.110:18082/greedy_cars/api/empleados' \
-H 'Content-Type: application/json' \
-d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "fechaNacimiento": "1985-08-22",
    "tipoDocumento": "DNI",
    "numeroDocumento": "30123456",
    "direccionIds": [1],
    "tipoEmpleado": "ADMINISTRATIVO"
}'
```

### Empleado as JEFE (manager):
```bash
curl -X POST 'http://161.153.217.110:18082/greedy_cars/api/empleados' \
-H 'Content-Type: application/json' \
-d '{
    "nombre": "María",
    "apellido": "González",
    "fechaNacimiento": "1980-02-14",
    "tipoDocumento": "DNI",
    "numeroDocumento": "25987654",
    "direccionIds": [1],
    "tipoEmpleado": "JEFE"
}'
```

### Empleado with contacts and images:
```bash
curl -X POST 'http://161.153.217.110:18082/greedy_cars/api/empleados' \
-H 'Content-Type: application/json' \
-d '{
    "nombre": "Pedro",
    "apellido": "Ramírez",
    "fechaNacimiento": "1992-12-05",
    "tipoDocumento": "DNI",
    "numeroDocumento": "38765432",
    "direccionIds": [1],
    "tipoEmpleado": "ADMINISTRATIVO",
    "contactoIds": ["<CONTACTO_ID>"],
    "imagenIds": ["<IMAGEN_ID>"]
}'
```

## Verification Queries

### List all Clientes:
```bash
curl -s 'http://161.153.217.110:18082/greedy_cars/api/clientes' | python3 -m json.tool
```

### List all Empleados:
```bash
curl -s 'http://161.153.217.110:18082/greedy_cars/api/empleados' | python3 -m json.tool
```

### Get specific Cliente:
```bash
curl -s 'http://161.153.217.110:18082/greedy_cars/api/clientes/<CLIENTE_ID>' | python3 -m json.tool
```

### Get specific Empleado:
```bash
curl -s 'http://161.153.217.110:18082/greedy_cars/api/empleados/<EMPLEADO_ID>' | python3 -m json.tool
```

## Database Verification

### Verify Cliente in database:
```sql
SELECT c.id, c.nombre, c.apellido, c.numero_documento, c.tipo_persona,
       c.direccion_estadia,
       GROUP_CONCAT(DISTINCT n.nombre) as nacionalidades,
       COUNT(DISTINCT cn.contacto_id) as num_contactos,
       COUNT(DISTINCT i.id) as num_imagenes
FROM persona c
LEFT JOIN cliente_nacionalidad cn_rel ON c.id = cn_rel.cliente_id
LEFT JOIN nacionalidad n ON cn_rel.nacionalidad_id = n.id
LEFT JOIN contacto cn ON cn.persona_id = c.id
LEFT JOIN imagen i ON i.persona_id = c.id
WHERE c.tipo_persona = 'CLIENTE'
GROUP BY c.id;
```

### Verify Empleado in database:
```sql
SELECT e.id, e.nombre, e.apellido, e.numero_documento, e.tipo_persona,
       emp.tipo_empleado,
       COUNT(DISTINCT c.id) as num_contactos,
       COUNT(DISTINCT i.id) as num_imagenes
FROM persona e
JOIN empleados emp ON e.id = emp.id
LEFT JOIN contacto c ON c.persona_id = e.id
LEFT JOIN imagen i ON i.persona_id = e.id
WHERE e.tipo_persona = 'EMPLEADO'
GROUP BY e.id;
```

## Notes

1. **TipoEmpleado** values: `ADMINISTRATIVO` or `JEFE`
2. **TipoDocumento** values: `DNI`, `PASAPORTE`, `CEDULA`, `LICENCIA`, `OTRO`
3. **numeroDocumento** must be unique across all Personas (including Clientes and Empleados)
4. **nacionalidadIds** is required for Cliente (at least one)
5. Both Cliente and Empleado inherit all properties from Persona:
   - nombre, apellido, fechaNacimiento
   - tipoDocumento, numeroDocumento
   - direccionIds, contactoIds, imagenIds
