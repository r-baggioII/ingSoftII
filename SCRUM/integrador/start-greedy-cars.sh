#!/bin/bash

# Script para iniciar Greedy Cars - Backend y Frontend
# Uso: ./start-greedy-cars.sh

echo "================================================"
echo "   GREEDY CARS - Inicio del Sistema"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Directorios
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/greedy_cars"
FRONTEND_DIR="$SCRIPT_DIR/gredy_cars_client/gredy_cars_client"

# Función para verificar si un puerto está en uso
check_port() {
    lsof -i :$1 > /dev/null 2>&1
    return $?
}

# Función para detener procesos en un puerto
kill_port() {
    echo -e "${YELLOW}Deteniendo proceso en puerto $1...${NC}"
    fuser -k $1/tcp 2>/dev/null
    sleep 2
}

echo -e "${GREEN}Verificando puertos...${NC}"

# Backend en puerto 18081
if check_port 18081; then
    echo -e "${YELLOW}⚠ Puerto 18081 (Backend) está en uso${NC}"
    read -p "¿Desea detener el proceso existente? (s/n): " respuesta
    if [[ "$respuesta" == "s" || "$respuesta" == "S" ]]; then
        kill_port 18081
    else
        echo -e "${RED}Abortando inicio...${NC}"
        exit 1
    fi
fi

# Frontend en puerto 18082
if check_port 18082; then
    echo -e "${YELLOW}⚠ Puerto 18082 (Frontend) está en uso${NC}"
    read -p "¿Desea detener el proceso existente? (s/n): " respuesta
    if [[ "$respuesta" == "s" || "$respuesta" == "S" ]]; then
        kill_port 18082
    else
        echo -e "${RED}Abortando inicio...${NC}"
        exit 1
    fi
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Iniciando Base de Datos...${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""

cd "$SCRIPT_DIR"

# Verificar si existe el contenedor de base de datos
if docker ps -a --format '{{.Names}}' | grep -q "^greedy_cars_db$"; then
    if docker ps --format '{{.Names}}' | grep -q "^greedy_cars_db$"; then
        echo -e "${GREEN}✓ Base de datos ya está corriendo${NC}"
    else
        echo -e "${YELLOW}Iniciando base de datos existente...${NC}"
        docker start greedy_cars_db
        echo -e "${YELLOW}Esperando 10 segundos para que la base de datos inicie...${NC}"
        sleep 10
    fi
else
    echo -e "${YELLOW}Creando y iniciando base de datos...${NC}"
    
    # Cargar variables de entorno desde .env
    if [ -f "$SCRIPT_DIR/.env" ]; then
        export $(grep -v '^#' "$SCRIPT_DIR/.env" | xargs)
    fi
    
    # Crear red si no existe
    docker network create greedy_network 2>/dev/null || true
    
    # Crear volumen si no existe
    docker volume create greedy_cars_db_data 2>/dev/null || true
    
    # Iniciar contenedor de MariaDB
    docker run -d \
        --name greedy_cars_db \
        --network greedy_network \
        --restart unless-stopped \
        -p 3307:3306 \
        -e MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-adminAdmin}" \
        -e MYSQL_DATABASE="${MYSQL_DATABASE:-greedy_cars_db}" \
        -e MYSQL_USER="${MYSQL_USER:-greedy_cars}" \
        -e MYSQL_PASSWORD="${MYSQL_PASSWORD:-adminAdmin}" \
        -v greedy_cars_db_data:/var/lib/mysql \
        mariadb:10.6
    
    echo -e "${YELLOW}Esperando 20 segundos para que la base de datos inicie...${NC}"
    sleep 20
fi

# Verificar que el puerto 3307 está disponible
if check_port 3307; then
    echo -e "${GREEN}✓ Base de datos disponible en puerto 3307${NC}"
else
    echo -e "${RED}✗ Error: Base de datos no pudo iniciar en puerto 3307${NC}"
    echo -e "${RED}Ver logs: docker logs greedy_cars_db${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Compilando Backend con Docker (Java 17)...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$BACKEND_DIR" || exit 1

# Compilar backend usando Docker con Java 17
docker run --rm \
    -v "$BACKEND_DIR":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el backend${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Compilando Frontend con Docker (Java 17)...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$FRONTEND_DIR" || exit 1

# Compilar frontend usando Docker con Java 17
docker run --rm \
    -v "$FRONTEND_DIR":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el frontend${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Iniciando Servicios...${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""

# Crear directorio para logs si no existe
mkdir -p "$SCRIPT_DIR/logs"

# Iniciar Backend en segundo plano con Docker
echo -e "${GREEN}▶ Iniciando Backend en puerto 18081 con Docker (Java 17)...${NC}"
cd "$BACKEND_DIR"

# Cargar variables de entorno desde .env
if [ -f "$SCRIPT_DIR/.env" ]; then
    export $(grep -v '^#' "$SCRIPT_DIR/.env" | xargs)
fi

# Encontrar el archivo WAR generado
WAR_FILE=$(find "$BACKEND_DIR/target" -name "*.war" | head -n 1)
if [ -z "$WAR_FILE" ]; then
    echo -e "${RED}✗ No se encontró el archivo WAR del backend${NC}"
    exit 1
fi

docker run -d \
    --name greedy-cars-backend \
    --network host \
    -v "$BACKEND_DIR/target":/app \
    -w /app \
    --env-file "$SCRIPT_DIR/.env" \
    -e SERVER_PORT=18081 \
    --restart unless-stopped \
    eclipse-temurin:17-jre \
    java -jar $(basename "$WAR_FILE") > "$SCRIPT_DIR/logs/backend.log" 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al iniciar el backend${NC}"
    exit 1
fi

# Esperar a que el backend inicie (máximo 60 segundos)
echo -e "${YELLOW}  Esperando a que el backend inicie...${NC}"
BACKEND_WAIT=0
while [ $BACKEND_WAIT -lt 60 ]; do
    if check_port 18081; then
        echo -e "${GREEN}  ✓ Backend iniciado correctamente en puerto 18081 (${BACKEND_WAIT}s)${NC}"
        break
    fi
    sleep 2
    BACKEND_WAIT=$((BACKEND_WAIT + 2))
    echo -ne "  Esperando... ${BACKEND_WAIT}s\r"
done

# Verificar que el backend está corriendo
if ! check_port 18081; then
    echo ""
    echo -e "${RED}  ✗ Error: Backend no pudo iniciar en puerto 18081 después de 60s${NC}"
    echo -e "${RED}  Ver logs: docker logs greedy-cars-backend${NC}"
    exit 1
fi

echo ""

# Iniciar Frontend en segundo plano con Docker
echo -e "${GREEN}▶ Iniciando Frontend en puerto 18082 con Docker (Java 17)...${NC}"
cd "$FRONTEND_DIR"

# Encontrar el archivo WAR generado
WAR_FILE=$(find "$FRONTEND_DIR/target" -name "*.war" | head -n 1)
if [ -z "$WAR_FILE" ]; then
    echo -e "${RED}✗ No se encontró el archivo WAR del frontend${NC}"
    exit 1
fi

docker run -d \
    --name greedy-cars-frontend \
    --network host \
    -v "$FRONTEND_DIR/target":/app \
    -w /app \
    --env-file "$SCRIPT_DIR/.env" \
    -e SERVER_PORT=18082 \
    --restart unless-stopped \
    eclipse-temurin:17-jre \
    java -jar $(basename "$WAR_FILE") > "$SCRIPT_DIR/logs/frontend.log" 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al iniciar el frontend${NC}"
    exit 1
fi

# Esperar a que el frontend inicie (máximo 60 segundos)
echo -e "${YELLOW}  Esperando a que el frontend inicie...${NC}"
FRONTEND_WAIT=0
while [ $FRONTEND_WAIT -lt 60 ]; do
    if check_port 18082; then
        echo -e "${GREEN}  ✓ Frontend iniciado correctamente en puerto 18082 (${FRONTEND_WAIT}s)${NC}"
        break
    fi
    sleep 2
    FRONTEND_WAIT=$((FRONTEND_WAIT + 2))
    echo -ne "  Esperando... ${FRONTEND_WAIT}s\r"
done

# Verificar que el frontend está corriendo
if ! check_port 18082; then
    echo ""
    echo -e "${RED}  ✗ Error: Frontend no pudo iniciar en puerto 18082 después de 60s${NC}"
    echo -e "${RED}  Ver logs: docker logs greedy-cars-frontend${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   ✓ SISTEMA INICIADO CORRECTAMENTE${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""
echo -e "📍 URLs de acceso:"
echo -e "   ${GREEN}Backend API:${NC}   http://161.153.217.110:18081"
echo -e "   ${GREEN}Frontend Web:${NC}  http://161.153.217.110:18082"
echo ""
echo -e "🔐 Endpoints principales:"
echo -e "   ${GREEN}Registro:${NC}      http://161.153.217.110:18082/registro"
echo -e "   ${GREEN}Login:${NC}         http://161.153.217.110:18082/login"
echo -e "   ${GREEN}API Registro:${NC}  http://161.153.217.110:18081/api/registro"
echo ""
echo -e "📝 Ver logs:"
echo -e "   ${GREEN}Backend:${NC}  docker logs -f greedy-cars-backend"
echo -e "   ${GREEN}Frontend:${NC} docker logs -f greedy-cars-frontend"
echo ""
echo -e "🐳 Contenedores Docker:"
echo -e "   ${GREEN}Backend:${NC}  greedy-cars-backend"
echo -e "   ${GREEN}Frontend:${NC} greedy-cars-frontend"
echo ""
echo -e "⏹ Para detener el sistema:"
echo -e "   ${YELLOW}./stop-greedy-cars.sh${NC}"
echo -e "   O manualmente: docker stop greedy-cars-backend greedy-cars-frontend"
echo ""
