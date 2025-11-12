# Greedy Cars - Docker Deployment

Este proyecto contiene la configuración completa para desplegar todas las aplicaciones de Greedy Cars usando contenedores Docker en una misma máquina con diferentes puertos.

## 🏗️ Arquitectura

### Aplicaciones
- **greedy_cars** - API principal (puerto 9000)
- **greedy_institucional** - Sitio institucional (puerto 8080)
- **gredy_cars_client** - Aplicación cliente (puerto 8081)
- **greedy_cars_db** - Base de datos MariaDB (puerto 3307)

### Conectividad
- Todas las aplicaciones se comunican a través de una red Docker interna
- Las aplicaciones cliente se conectan a la API usando nombres de contenedor
- La base de datos solo es accesible internamente (excepto puerto 3307 para debugging)

## 🚀 Despliegue Rápido

### Prerrequisitos
- Docker instalado
- Docker Compose instalado
- Maven instalado (para construir las aplicaciones)

### 1. Configurar Variables de Entorno
```bash
# Copiar archivo de ejemplo
cp .env.example .env

# Editar con sus configuraciones
nano .env
```

### 2. Construir y Desplegar
```bash
# Usar el script automatizado
./build-and-deploy.sh
```

O manualmente:
```bash
# Construir aplicaciones Java
mvn clean package -DskipTests

# Construir imágenes Docker
docker-compose build

# Iniciar servicios
docker-compose up -d
```

## 🌐 Acceso a las Aplicaciones

Una vez desplegado, puede acceder a las aplicaciones en:

- **API Greedy Cars**: http://localhost:9000/greedy_cars
- **Sitio Institucional**: http://localhost:8080/greedy_institucional
- **App Cliente**: http://localhost:8081/gredy_cars_client

## 🗄️ Base de Datos

- **Host**: localhost
- **Puerto**: 3307
- **Usuario**: greedy_cars
- **Contraseña**: adminAdmin
- **Base de datos**: greedy_cars_db

### Conexión desde cliente SQL:
```sql
mysql -h localhost -P 3307 -u greedy_cars -p greedy_cars_db
```

## 🔧 Comandos Útiles

### Verificar estado
```bash
docker-compose ps
```

### Ver logs
```bash
# Ver todos los logs
docker-compose logs -f

# Ver logs de servicio específico
docker-compose logs -f greedy_cars
docker-compose logs -f greedy_cars_db
```

### Reiniciar servicios
```bash
docker-compose restart
```

### Detener todo
```bash
docker-compose down
```

### Actualizar aplicaciones
```bash
# Detener
docker-compose down

# Reconstruir
mvn clean package -DskipTests
docker-compose build

# Iniciar
docker-compose up -d
```

## 📂 Estructura de Archivos

```
integrador/
├── docker-compose.yml          # Configuración principal
├── docker-compose.prod.yml     # Configuración producción
├── .env.example               # Variables de entorno ejemplo
├── build-and-deploy.sh        # Script de despliegue
├── README.md                  # Este archivo
├── greedy_cars/               # API principal
│   ├── Dockerfile
│   └── target/greedy_cars.war
├── greedy_institucional/      # Sitio institucional
│   ├── Dockerfile
│   └── target/greedy_institucional.war
└── gredy_cars_client/         # App cliente
    └── gredy_cars_client/
        ├── Dockerfile
        └── target/gredy_cars_client.war
```

## 🔐 Configuración de Producción

Para despliegue en producción:

1. **Seguridad**:
   - Cambiar contraseñas por defecto
   - Usar JWT secreto robusto
   - Configurar tokens de MercadoPago reales

2. **Redes**:
   - Usar `docker-compose.prod.yml`
   - Configurar reverse proxy (Nginx)
   - Certificados SSL

3. **Monitoreo**:
   - Configurar health checks
   - Logs centralizados
   - Métricas de aplicación

4. **Base de Datos**:
   - Backup regular
   - Configurar replicación si es necesario
   - Optimizar consultas

## 🐛 Troubleshooting

### Problemas comunes:

**1. Contenedor no inicia:**
```bash
# Ver logs específicos
docker-compose logs [nombre_servicio]
```

**2. Error de conexión a base de datos:**
```bash
# Verificar estado de la base de datos
docker-compose exec greedy_cars_db mysql -u root -p
```

**3. Aplicaciones no se comunican:**
```bash
# Verificar red Docker
docker network ls
docker network inspect integrador_greedy_network
```

**4. Problemas de permisos:**
```bash
# Reconstruir imágenes con permisos correctos
docker-compose build --no-cache
```

## 📞 Soporte

Para issues o preguntas:
1. Verificar logs del servicio afectado
2. Revisar configuración de red
3. Validar variables de entorno
4. Consultar documentación de Spring Boot y Docker