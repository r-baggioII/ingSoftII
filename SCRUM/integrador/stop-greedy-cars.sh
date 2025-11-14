#!/bin/bash

# Script para detener Greedy Cars - Backend y Frontend
# Uso: ./stop-greedy-cars.sh

echo "================================================"
echo "   GREEDY CARS - Detención del Sistema"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar si un puerto está en uso
check_port() {
    lsof -i :$1 > /dev/null 2>&1
    return $?
}

# Función para detener procesos en un puerto
kill_port() {
    echo -e "${YELLOW}Deteniendo proceso en puerto $1...${NC}"
    fuser -k $1/tcp 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}  ✓ Proceso en puerto $1 detenido${NC}"
    else
        echo -e "${YELLOW}  ⚠ No se encontró proceso en puerto $1${NC}"
    fi
    sleep 1
}

echo -e "${GREEN}Verificando servicios en ejecución...${NC}"
echo ""

# Verificar y detener Backend (puerto 18081)
if check_port 18081; then
    echo -e "${YELLOW}Backend encontrado en puerto 18081${NC}"
    kill_port 18081
else
    echo -e "${GREEN}Backend no está corriendo en puerto 18081${NC}"
fi

echo ""

# Verificar y detener Frontend (puerto 18082)
if check_port 18082; then
    echo -e "${YELLOW}Frontend encontrado en puerto 18082${NC}"
    kill_port 18082
else
    echo -e "${GREEN}Frontend no está corriendo en puerto 18082${NC}"
fi

echo ""

# Verificar que los puertos estén liberados
sleep 2

BACKEND_STOPPED=true
FRONTEND_STOPPED=true

if check_port 18081; then
    echo -e "${RED}⚠ Advertencia: Todavía hay un proceso en puerto 18081${NC}"
    BACKEND_STOPPED=false
fi

if check_port 18082; then
    echo -e "${RED}⚠ Advertencia: Todavía hay un proceso en puerto 18082${NC}"
    FRONTEND_STOPPED=false
fi

echo ""

if $BACKEND_STOPPED && $FRONTEND_STOPPED; then
    echo -e "${GREEN}================================================${NC}"
    echo -e "${GREEN}   ✓ SISTEMA DETENIDO CORRECTAMENTE${NC}"
    echo -e "${GREEN}================================================${NC}"
    exit 0
else
    echo -e "${YELLOW}================================================${NC}"
    echo -e "${YELLOW}   ⚠ ALGUNOS SERVICIOS NO SE DETUVIERON${NC}"
    echo -e "${YELLOW}================================================${NC}"
    echo ""
    echo -e "${YELLOW}Puede intentar detenerlos manualmente:${NC}"
    if ! $BACKEND_STOPPED; then
        echo -e "  Backend:  sudo fuser -k 18081/tcp"
    fi
    if ! $FRONTEND_STOPPED; then
        echo -e "  Frontend: sudo fuser -k 18082/tcp"
    fi
    exit 1
fi
