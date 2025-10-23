# Sistema de Gestión de Mecánico - Instrucciones

## ✅ Configuración Completada

### Base de Datos
- **Base de datos**: `mecanico_db`
- **Usuario MySQL**: `root`
- **Contraseña MySQL**: `adminAdmin`
- **Puerto aplicación**: `9000`

## 🚀 Cómo Iniciar la Aplicación

### 1. Asegúrate de que MySQL/MariaDB esté corriendo
```bash
sudo systemctl status mysql
# Si no está corriendo:
sudo systemctl start mysql
```

### 2. Inicia la aplicación Spring Boot
```bash
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/ej5/G
mvn spring-boot:run
```

### 3. Accede a la aplicación
Abre tu navegador en: **http://localhost:9000**

## 🔐 Credenciales de Acceso

### Usuario Administrador
- **Email**: `admin@mecanico.com`
- **Contraseña**: `password123`

### Usuarios Normales
Todos tienen la contraseña: `password123`

- `carlos.mendez@mecanico.com`
- `juan.ramirez@mecanico.com`
- `ana.torres@mecanico.com`
- `maria.gonzalez@mecanico.com`
- `pedro.martinez@mecanico.com`

## 📝 Repoblar la Base de Datos

Si necesitas reiniciar los datos:
```bash
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/ej5/G
mysql -u root -padminAdmin mecanico_db < poblar_bd.sql
```

## 🔧 Solución de Problemas

### Error: "Cannot connect to database"
```bash
# Verifica que MySQL esté corriendo
sudo systemctl status mysql

# Verifica que puedas conectarte
mysql -u root -padminAdmin mecanico_db -e "SELECT 1;"
```

### Error: "Usuario o contraseña inválidos"
1. Verifica que estés usando `password123` (NO el hash BCrypt)
2. Verifica que el email sea exacto: `admin@mecanico.com`
3. Repobla la base de datos con el script

### El puerto 9000 ya está en uso
Cambia el puerto en `application.properties`:
```properties
server.port=9001
```

## 📚 Estructura del Proyecto

### Entidades
- **BaseEntity**: Clase base con campo `eliminado`
- **Usuario**: Autenticación y autorización
- **Persona**: Clase base para Cliente y Mecánico
- **Cliente**: Clientes del taller
- **Mecánico**: Mecánicos del taller (con Usuario asociado)
- **Vehiculo**: Vehículos de los clientes
- **HistorialArreglo**: Registro de arreglos realizados

### Patrón Template
- **BaseRepository**: Repositorio base
- **BaseService**: Servicio base con operaciones CRUD
- **BaseController**: Controlador base con operaciones web

## 🌐 URLs Principales

- **Login**: http://localhost:9000/usuario/login
- **Inicio**: http://localhost:9000/usuario/inicio (después de login)
- **Personas**: http://localhost:9000/persona
- **Clientes**: http://localhost:9000/cliente
- **Mecánicos**: http://localhost:9000/mecanico
- **Vehículos**: http://localhost:9000/vehiculo
- **Historial**: http://localhost:9000/historialarreglo
