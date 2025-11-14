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
BACKEND_DIR="/srv/greedy/ingSoftII/SCRUM/integrador/greedy_cars"
FRONTEND_DIR="/srv/greedy/ingSoftII/SCRUM/integrador/gredy_cars_client/gredy_cars_client"

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
echo -e "${GREEN}   Compilando Backend...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$BACKEND_DIR" || exit 1

# Compilar backend (sin tests para ser más rápido)
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el backend${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Compilando Frontend...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$FRONTEND_DIR" || exit 1

# Compilar frontend
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
mkdir -p logs

# Iniciar Backend en segundo plano
echo -e "${GREEN}▶ Iniciando Backend en puerto 18081...${NC}"
cd "$BACKEND_DIR"
nohup ./mvnw spring-boot:run > logs/backend.log 2>&1 &
BACKEND_PID=$!
echo "  Backend PID: $BACKEND_PID"

# Esperar unos segundos para que el backend inicie
echo -e "${YELLOW}  Esperando 15 segundos para que el backend inicie...${NC}"
sleep 15

# Verificar que el backend está corriendo
if check_port 18081; then
    echo -e "${GREEN}  ✓ Backend iniciado correctamente en puerto 18081${NC}"
else
    echo -e "${RED}  ✗ Error: Backend no pudo iniciar en puerto 18081${NC}"
    echo -e "${RED}  Ver logs en: $BACKEND_DIR/logs/backend.log${NC}"
    exit 1
fi

echo ""

# Iniciar Frontend en segundo plano
echo -e "${GREEN}▶ Iniciando Frontend en puerto 18082...${NC}"
cd "$FRONTEND_DIR"
nohup ./mvnw spring-boot:run > logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "  Frontend PID: $FRONTEND_PID"

# Esperar unos segundos para que el frontend inicie
echo -e "${YELLOW}  Esperando 15 segundos para que el frontend inicie...${NC}"
sleep 15

# Verificar que el frontend está corriendo
if check_port 18082; then
    echo -e "${GREEN}  ✓ Frontend iniciado correctamente en puerto 18082${NC}"
else
    echo -e "${RED}  ✗ Error: Frontend no pudo iniciar en puerto 18082${NC}"
    echo -e "${RED}  Ver logs en: $FRONTEND_DIR/logs/frontend.log${NC}"
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
echo -e "📝 Logs:"
echo -e "   Backend:  tail -f $BACKEND_DIR/logs/backend.log"
echo -e "   Frontend: tail -f $FRONTEND_DIR/logs/frontend.log"
echo ""
echo -e "⏹ Para detener el sistema:"
echo -e "   kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo -e "${YELLOW}Presione Ctrl+C para detener este script (los servicios seguirán corriendo)${NC}"
echo ""

# Mantener el script corriendo para mostrar los PIDs
wait
