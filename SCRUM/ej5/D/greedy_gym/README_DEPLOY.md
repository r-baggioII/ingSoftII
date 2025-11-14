# README_DEPLOY — Despliegue completo (desde 0)

Este documento reúne todos los pasos necesarios para preparar, versionar y desplegar el proyecto `greedy_gym` en un servidor remoto (VPS) usando Docker / Docker Compose. Sigue las instrucciones exactamente y reemplaza los valores entre `<>` por los reales.

---
## Índice
1. Requisitos locales
2. Preparar el repositorio local y añadir el .war
3. Publicar en un repositorio remoto público (GitHub / GitLab)
4. Preparación del VPS (Don Web) — conexión SSH
5. Instalación de herramientas en VPS (Git, Docker, Docker Compose)
6. Clonar, construir y desplegar en el VPS
7. Recomendaciones de producción y hardening
8. Comprobación / troubleshooting
9. Scripts útiles y ejemplos

---
## 1) Requisitos locales
- Java 17 (OpenJDK 17)
- Maven (o usar `./mvnw` incluido)
- Docker (si quieres probar imágenes localmente)
- Acceso a la cuenta del repositorio remoto donde vas a subir el código (GitHub/GitLab)

Verifica el build local:

```bash
cd /ruta/al/proyecto/SCRUM/ej5/D/greedy_gym
./mvnw clean install
# o si usas mvn instalado: mvn clean install
```
Al final deberías tener `target/greedy_gym.war`.

---
## 2) Preparar el repositorio local y añadir el .war
> Nota: Por seguridad, NO comitees archivos con secretos (.env) — añade `.env` a `.gitignore`.

1) Inicializar repo y crear rama principal (si no existe):

```bash
cd /ruta/al/proyecto/SCRUM/ej5/D/greedy_gym
git init
git switch -c main
```

2) Asegurarse de ignorar `.env` y otros secretos:

```bash
# Añadir .env a .gitignore si no está
grep -qxF '.env' .gitignore || echo '.env' >> .gitignore
# Añadir .env a index si accidentalmente ya fue trackeado
git rm --cached .env || true
```

3) Forzar inclusión del WAR en el repo (si `.gitignore` contiene `target/`):

```bash
# Forzar add del war
git add -f target/greedy_gym.war
# Añadir otros cambios seguros (README, Dockerfile, docker-compose, .env.example, etc.)
git add Dockerfile docker-compose.yml README_DOCKER_ENV.md .env.example
# O añadir todo (ver antes git status)
git add .

# Comitear
git commit -m "Prepare project for ASE II: include built artifact and Docker files"
```

> Si prefieres no forzar, puedes alternativamente editar `.gitignore` para permitir el archivo con una excepción:

```
# dentro de .gitignore
target/
!target/greedy_gym.war
```

---
## 3) Publicar en un repositorio remoto público (GitHub/GitLab)
1) Crear el repositorio remoto en GitHub/GitLab y copia la URL que termina en `.git` (ej: `https://github.com/usuario/greedy_gym.git`).

2) Añadir remoto y subir:

```bash
git remote add origin <REMOTE_URL.git>
git push -u origin main
```

Guarda la URL remota (se la piden en ASE II) y comprueba que el `.war` aparece en el repo.

---
## 4) Preparación del VPS (Don Web) — conexión SSH
- Abre Putty o tu cliente SSH favorito.
- Hostname / IP: `<VPS_IP>`
- Puerto: `<5307>` (ejemplo)
- Usuario: `root` (según instrucciones)

Conexión por terminal (ejemplo):

```bash
ssh root@<VPS_IP> -p 5307
```

Crear directorio de trabajo en VPS:

```bash
mkdir -p /opt/greedy_gym
chown root:root /opt/greedy_gym
cd /opt/greedy_gym
```

---
## 5) Instalación de herramientas en VPS (Ubuntu 22.04)
Ejecuta en la sesión SSH como root (o con sudo):

```bash
apt update && apt upgrade -y
apt install -y git curl ca-certificates gnupg lsb-release

# Instalar Docker (método oficial)
mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
  | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt update
apt install -y docker-ce docker-ce-cli containerd.io

# Instalar Docker Compose (plugin v2)
apt install -y docker-compose-plugin

# Verificar
docker --version
docker compose version
```

Opcional: crear usuario no-root y añadir al grupo docker si no quieres usar sudo:

```bash
useradd -m deployer
usermod -aG docker deployer
# luego usar 'su - deployer' o configurar claves SSH
```

---
## 6) Clonar, construir y desplegar en el VPS
En el VPS, dentro de `/opt/greedy_gym`:

```bash
cd /opt/greedy_gym
# Clona el repo público
git clone <REMOTE_URL.git> .

# Si no subiste el .war, puedes compilar en el servidor (requiere JDK/Maven instalados)
# ./mvnw clean install

# Levantar con docker compose (usa el Dockerfile que copia el war desde target/)
docker compose up --build -d

# Ver estado
docker compose ps
# Ver logs (tail)
docker compose logs -f
```

Notas:
- Si incluiste `target/greedy_gym.war` en el repo, la `COPY target/greedy_gym.war app.war` del Dockerfile funcionará.
- Si NO lo incluiste, ejecuta `./mvnw clean install` en el VPS antes de `docker compose up --build -d`.

---
## 7) Manejo del arranque cuando la base de datos tarda
`depends_on` solo controla orden de arranque, no disponibilidad. Opciones:

**A) Healthchecks en docker-compose** (recomendado)

```yaml
services:
  greedy_gym_db:
    image: mariadb:10.6
    environment:
      - MARIADB_ROOT_PASSWORD=${MARIADB_ROOT_PASSWORD}
      - MARIADB_DATABASE=${MARIADB_DATABASE}
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 5s
      timeout: 5s
      retries: 10

  greedy_gym:
    depends_on:
      greedy_gym_db:
        condition: service_healthy
```

> Nota: `condition: service_healthy` funciona con la sintaxis de Compose v2; verifica la versión de Compose usada.

**B) Script de espera**
- Un script en el VPS que pruebe la conexión a la DB y luego haga `docker compose restart greedy_gym` cuando la DB esté lista.

**C) Configurar la app para reintentos**
- HikariCP y Spring Boot suelen intentar reconectar; asegúrate de los parámetros de timeout y reintento.

---
## 8) Recomendaciones de producción y hardening (prioridad alta)
1. No comitees secretos. Añade `.env` a `.gitignore` y usa `docker secrets` o un gestor (Vault, AWS Secrets Manager).
2. Usa un `docker-compose.prod.yml` que no exponga el puerto 3306 al host.
3. Cambia Dockerfile para ejecutar la app como usuario no-root y añadir `HEALTHCHECK`.
4. Añade Spring Boot Actuator y endpoints de salud y métricas (/actuator/health, /actuator/prometheus) y habilita seguridad.
5. Automatiza backups de la BD (cron + `mysqldump` o snapshot del volumen) y replica si es necesario.
6. Añade un proxy inverso (nginx) con TLS y certificados (Let's Encrypt) para exponer la app.
7. Habilita firewall (ufw) y solo abre los puertos necesarios.

---
## 9) Scripts útiles
### Script para esperar DB y reiniciar app (ejecutar en VPS)

```bash
#!/bin/bash
set -e
DB_CONTAINER=$(docker compose ps -q greedy_gym_db)
if [ -z "$DB_CONTAINER" ]; then
  echo "DB container not found"
  exit 1
fi
MARIADB_ROOT_PASSWORD=${MARIADB_ROOT_PASSWORD:-adminAdmin}
until docker exec "$DB_CONTAINER" mysql -uroot -p"$MARIADB_ROOT_PASSWORD" -e 'SELECT 1' >/dev/null 2>&1; do
  echo "Waiting for DB to be ready..."
  sleep 5
done

echo "DB ready — restarting app"
docker compose restart greedy_gym
```

Guarda este script como `wait-db-and-restart.sh`, hazlo ejecutable `chmod +x wait-db-and-restart.sh` y ejecútalo después de arrancar compose si observas errores por DB no lista.

---
## 10) Checklist final antes de entregar (ASE II)
- [ ] `target/greedy_gym.war` incluido en el repo o disponible para build en el servidor.
- [ ] `.env` no comiteado y listado en `.gitignore`.
- [ ] `docker-compose.yml` funcional y `Dockerfile` existente.
- [ ] URL del repo remoto (.git) guardada.
- [ ] Documentación de cómo conectar al VPS (hostname/IP, puerto, usuario).
- [ ] Comprobado `docker compose up --build -d` en el VPS y servicios "Up".

---
## 11) Preguntas frecuentes (FAQ)
Q: ¿Puedo usar MySQL en lugar de MariaDB?  
A: Sí. Cambia la imagen a `mysql:8.0.33` y ajusta variables (ej. `MYSQL_ROOT_PASSWORD`) y driver en `pom.xml` si fuera necesario.

Q: ¿Dónde pongo el token de MercadoPago?  
A: En producción usa `docker secrets` o los secretos de tu proveedor CI/CD; en local puedes usar `.env` (no comitear).

Q: ¿Cómo hago rollback?  
A: Mantén tags de imagen y cuando necesites rollback `docker compose pull` de la versión anterior o `docker compose down && docker compose up -d <image:oldtag>`.

---
Si quieres, puedo:
- Añadir `docker-compose.prod.yml` y un `Dockerfile` multi-stage + non-root automáticamente.
- Crear un pequeño GitHub Action para construir y subir la imagen a un registry.

Dime qué prefieres y lo implemento a continuación.
