#!/bin/bash

# Script para actualizar código, recompilar y reiniciar servicios (sin tocar la BD)
# Uso: ./rebuild-and-restart.sh

echo "================================================"
echo "   GREEDY CARS - Actualización y Reinicio"
echo "   (Manteniendo Base de Datos)"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Directorios
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   1. Actualizando código desde Git...${NC}"
echo -e "${BLUE}================================================${NC}"

cd "$SCRIPT_DIR" || exit 1

# Guardar estado actual
CURRENT_BRANCH=$(git branch --show-current)
echo -e "${YELLOW}Rama actual: ${CURRENT_BRANCH}${NC}"

# Hacer stash de cambios locales si hay
if ! git diff-index --quiet HEAD --; then
    echo -e "${YELLOW}⚠️  Hay cambios locales, guardando con stash...${NC}"
    git stash
    STASHED=true
else
    STASHED=false
fi

# Pull desde origin
echo -e "${GREEN}Descargando última versión...${NC}"
git pull origin "$CURRENT_BRANCH"

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al hacer pull desde Git${NC}"
    exit 1
fi

# Restaurar stash si fue necesario
if [ "$STASHED" = true ]; then
    echo -e "${YELLOW}Restaurando cambios locales...${NC}"
    git stash pop
fi

echo -e "${GREEN}✓ Código actualizado a la última versión${NC}"
echo ""

# Verificar último commit
echo -e "${BLUE}Último commit:${NC}"
git log -1 --oneline
echo ""

# Asegurar permisos de ejecución en Maven Wrappers
echo -e "${GREEN}Verificando permisos de Maven Wrappers...${NC}"
chmod +x "$SCRIPT_DIR/greedy_cars/mvnw" 2>/dev/null
chmod +x "$SCRIPT_DIR/gredy_cars_client/gredy_cars_client/mvnw" 2>/dev/null
chmod +x "$SCRIPT_DIR/greedy_institucional/mvnw" 2>/dev/null
echo -e "${GREEN}✓ Permisos configurados${NC}"
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   2. Deteniendo servicios (excepto BD)...${NC}"
echo -e "${BLUE}================================================${NC}"

# Detener solo los servicios de aplicación, no la BD
docker-compose stop greedy_cars gredy_cars_client greedy_institucional

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al detener servicios${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Servicios detenidos (BD sigue corriendo)${NC}"
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   3. Compilando Backend (greedy_cars)...${NC}"
echo -e "${BLUE}================================================${NC}"

cd "$SCRIPT_DIR/greedy_cars" || exit 1

# Compilar backend usando Maven Wrapper con Docker
docker run --rm \
    -v "$SCRIPT_DIR/greedy_cars":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el backend${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Backend compilado exitosamente${NC}"
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   4. Compilando Frontend Cliente...${NC}"
echo -e "${BLUE}================================================${NC}"

cd "$SCRIPT_DIR/gredy_cars_client/gredy_cars_client" || exit 1

# Compilar frontend cliente usando Maven Wrapper con Docker
docker run --rm \
    -v "$SCRIPT_DIR/gredy_cars_client/gredy_cars_client":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el frontend cliente${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Frontend cliente compilado exitosamente${NC}"
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   5. Compilando Sitio Institucional...${NC}"
echo -e "${BLUE}================================================${NC}"

cd "$SCRIPT_DIR/greedy_institucional" || exit 1

# Compilar sitio institucional usando Maven Wrapper con Docker
docker run --rm \
    -v "$SCRIPT_DIR/greedy_institucional":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el sitio institucional${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Sitio institucional compilado exitosamente${NC}"
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   6. Reconstruyendo imágenes Docker...${NC}"
echo -e "${BLUE}================================================${NC}"

cd "$SCRIPT_DIR" || exit 1

# Reconstruir solo las imágenes de los servicios, no la BD
docker-compose build greedy_cars gredy_cars_client greedy_institucional

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al reconstruir imágenes Docker${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Imágenes reconstruidas${NC}"
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   7. Iniciando servicios...${NC}"
echo -e "${BLUE}================================================${NC}"

# Iniciar servicios (la BD ya debería estar corriendo)
docker-compose up -d

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al iniciar los servicios${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}Esperando a que los servicios inicien...${NC}"
sleep 15

echo ""
echo -e "${YELLOW}================================================${NC}"
echo -e "${YELLOW}   Verificando creación de usuarios...${NC}"
echo -e "${YELLOW}================================================${NC}"
echo ""
echo -e "${YELLOW}Buscando mensajes sobre creación de usuario 'jefe' en los logs...${NC}"
echo ""

# Verificar logs del backend para el usuario jefe
docker-compose logs greedy_cars | grep -E "(jefe|Usuario jefe|Persona jefe|Error al crear)" || \
    echo -e "${RED}⚠️  NO SE ENCONTRARON LOGS SOBRE EL USUARIO JEFE${NC}"

echo ""
echo -e "${YELLOW}Logs completos de inicialización de usuarios:${NC}"
docker-compose logs greedy_cars | grep -E "(✅|❌|ℹ️)" || \
    echo -e "${RED}⚠️  NO SE ENCONTRARON MENSAJES DE INICIALIZACIÓN${NC}"

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   ✓ ACTUALIZACIÓN COMPLETADA${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""
echo -e "📍 URLs de acceso:"
echo -e "   ${GREEN}Backend API:${NC}        http://localhost:9000/greedy_cars"
echo -e "   ${GREEN}Frontend Cliente:${NC}   http://localhost:8081"
echo -e "   ${GREEN}Sitio Institucional:${NC} http://localhost:8080"
echo -e "   ${GREEN}Base de Datos:${NC}      localhost:3307 ${YELLOW}(no modificada)${NC}"
echo ""
echo -e "📝 Ver logs en tiempo real:"
echo -e "   ${YELLOW}docker-compose logs -f greedy_cars${NC}"
echo -e "   ${YELLOW}docker-compose logs -f gredy_cars_client${NC}"
echo -e "   ${YELLOW}docker-compose logs -f greedy_institucional${NC}"
echo ""
echo -e "⏹ Para detener el sistema:"
echo -e "   ${YELLOW}docker-compose stop${NC}"
echo ""
echo -e "🗑️  Para eliminar base de datos (si es necesario):"
echo -e "   ${RED}docker-compose down -v${NC}"
echo ""
